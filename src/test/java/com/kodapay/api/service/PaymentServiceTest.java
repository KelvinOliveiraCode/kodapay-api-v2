package com.kodapay.api.service;

import com.kodapay.api.controller.dto.PaymentRequest;
import com.kodapay.api.controller.dto.PaymentResponse;
import com.kodapay.api.domain.Payment;
import com.kodapay.api.domain.PaymentRepository;
import com.kodapay.api.domain.PaymentStatus;
import com.kodapay.api.exception.IdempotencyConflictException;
import com.kodapay.api.exception.PaymentNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private IdempotencyService idempotencyService;

    @Mock
    private StripeService stripeService;

    @InjectMocks
    private PaymentService paymentService;

    private static final String IDEMPOTENCY_KEY = "key-" + UUID.randomUUID();
    private static final BigDecimal AMOUNT = new BigDecimal("100.00");
    private static final String CURRENCY = "BRL";

    @Test
    void shouldCreatePayment_whenValidRequest() throws Exception {
        String intentId = "pi_" + UUID.randomUUID();
        when(stripeService.createPaymentIntent(AMOUNT, CURRENCY)).thenReturn(intentId);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            payment.setId("pay_" + UUID.randomUUID());
            return payment;
        });
        when(idempotencyService.isProcessed(IDEMPOTENCY_KEY)).thenReturn(false);

        PaymentRequest request = new PaymentRequest(CURRENCY, AMOUNT, IDEMPOTENCY_KEY);
        PaymentResponse response = paymentService.createPayment(request);

        assertThat(response.id()).isNotNull();
        assertThat(response.amount()).isEqualByComparingTo(AMOUNT);
        assertThat(response.currency()).isEqualTo(CURRENCY);
        assertThat(response.status()).isEqualTo(PaymentStatus.PENDING.name());
        assertThat(response.stripePaymentIntentId()).isEqualTo(intentId);
        assertThat(response.idempotencyKey()).isEqualTo(IDEMPOTENCY_KEY);
        verify(idempotencyService).markProcessed(IDEMPOTENCY_KEY, response.id());
    }

    @Test
    void shouldThrowConflict_whenIdempotencyKeyAlreadyProcessed() {
        when(idempotencyService.isProcessed(IDEMPOTENCY_KEY)).thenReturn(true);

        PaymentRequest request = new PaymentRequest(CURRENCY, AMOUNT, IDEMPOTENCY_KEY);

        assertThatThrownBy(() -> paymentService.createPayment(request))
                .isInstanceOf(IdempotencyConflictException.class)
                .hasMessage("Chave de idempotência já processada: " + IDEMPOTENCY_KEY);
        verifyNoInteractions(stripeService);
        verifyNoInteractions(paymentRepository);
    }

    @Test
    void shouldReturnExistingPayment_whenGetById() {
        String id = "pay_" + UUID.randomUUID();
        Payment payment = new Payment(id, AMOUNT, CURRENCY, PaymentStatus.COMPLETED,
                "pi_x", IDEMPOTENCY_KEY, Instant.now(), Instant.now());
        when(paymentRepository.findById(id)).thenReturn(Optional.of(payment));

        PaymentResponse response = paymentService.getPayment(id);

        assertThat(response.id()).isEqualTo(id);
        assertThat(response.status()).isEqualTo(PaymentStatus.COMPLETED.name());
    }

    @Test
    void shouldThrowNotFound_whenGetMissingPayment() {
        String id = "pay_missing";
        when(paymentRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.getPayment(id))
                .isInstanceOf(PaymentNotFoundException.class);
    }

    @Test
    void shouldCompletePayment_whenWebhookSucceeded() {
        String intentId = "pi_" + UUID.randomUUID();
        Payment payment = new Payment("pay_1", AMOUNT, CURRENCY, PaymentStatus.PENDING,
                intentId, null, Instant.now(), Instant.now());
        when(paymentRepository.findByStripePaymentIntentId(intentId)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponse response = paymentService.handleWebhook(intentId, "COMPLETED");

        assertThat(response.status()).isEqualTo(PaymentStatus.COMPLETED.name());
        assertThat(response.id()).isEqualTo("pay_1");
    }

    @Test
    void shouldFailPayment_whenWebhookFailed() {
        String intentId = "pi_" + UUID.randomUUID();
        Payment payment = new Payment("pay_2", AMOUNT, CURRENCY, PaymentStatus.PENDING,
                intentId, null, Instant.now(), Instant.now());
        when(paymentRepository.findByStripePaymentIntentId(intentId)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponse response = paymentService.handleWebhook(intentId, "FAILED");

        assertThat(response.status()).isEqualTo(PaymentStatus.FAILED.name());
    }

    @Test
    void shouldThrowNotFound_whenWebhookUnknownIntent() {
        when(paymentRepository.findByStripePaymentIntentId(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.handleWebhook("pi_desconhecido", "COMPLETED"))
                .isInstanceOf(PaymentNotFoundException.class);
    }

    @Test
    void shouldPropagateStripeIntentId_whenCreatePayment() throws Exception {
        String intentId = "pi_" + UUID.randomUUID();
        when(stripeService.createPaymentIntent(AMOUNT, CURRENCY)).thenReturn(intentId);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(idempotencyService.isProcessed(IDEMPOTENCY_KEY)).thenReturn(false);

        PaymentResponse response = paymentService.createPayment(
                new PaymentRequest(CURRENCY, AMOUNT, IDEMPOTENCY_KEY));

        assertThat(response.stripePaymentIntentId()).isEqualTo(intentId);
    }

    @Test
    void shouldSetCreatedAndUpdatedAt_whenCreatePayment() throws Exception {
        when(stripeService.createPaymentIntent(AMOUNT, CURRENCY)).thenReturn("pi_x");
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(idempotencyService.isProcessed(IDEMPOTENCY_KEY)).thenReturn(false);

        Instant before = Instant.now();
        PaymentResponse response = paymentService.createPayment(
                new PaymentRequest(CURRENCY, AMOUNT, IDEMPOTENCY_KEY));
        Instant after = Instant.now();

        assertThat(response.createdAt()).isNotNull();
        assertThat(response.updatedAt()).isNotNull();
        assertThat(response.createdAt()).isAfterOrEqualTo(before);
        assertThat(response.createdAt()).isBeforeOrEqualTo(after);
    }
}
