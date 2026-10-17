package com.kodapay.api.service;

import com.kodapay.api.controller.dto.PaymentResponse;
import com.kodapay.api.exception.InvalidWebhookSignatureException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StripeWebhookServiceTest {

    @Mock
    private PaymentService paymentService;

    @Mock
    private EventParser eventParser;

    @InjectMocks
    private StripeWebhookService webhookService;

    private static final String INTENT_ID = "pi_test_123";
    private static final String PAYMENT_ID = "pay_test_123";

    private Event eventComTipo(String tipo) {
        Event event = mock(Event.class);
        Event.Data data = mock(Event.Data.class);
        PaymentIntent intent = mock(PaymentIntent.class);
        when(event.getType()).thenReturn(tipo);
        when(event.getData()).thenReturn(data);
        when(data.getObject()).thenReturn(intent);
        when(intent.getId()).thenReturn(INTENT_ID);
        return event;
    }

    @Test
    void shouldCompletePayment_whenPaymentIntentSucceeded() {
        Event event = eventComTipo("payment_intent.succeeded");
        when(eventParser.parse(anyString(), anyString())).thenReturn(event);
        when(paymentService.handleWebhook(INTENT_ID, "COMPLETED"))
                .thenReturn(new PaymentResponse(PAYMENT_ID, new BigDecimal("10.00"), "BRL",
                        "COMPLETED", INTENT_ID, null, Instant.now(), Instant.now()));

        webhookService.handle("payload", "signature");

        verify(paymentService).handleWebhook(INTENT_ID, "COMPLETED");
    }

    @Test
    void shouldFailPayment_whenPaymentIntentFailed() {
        Event event = eventComTipo("payment_intent.payment_failed");
        when(eventParser.parse(anyString(), anyString())).thenReturn(event);
        when(paymentService.handleWebhook(INTENT_ID, "FAILED")).thenReturn(null);

        webhookService.handle("payload", "signature");

        verify(paymentService).handleWebhook(INTENT_ID, "FAILED");
    }

    @Test
    void shouldIgnoreUnknownEventType() {
        Event event = mock(Event.class);
        when(event.getType()).thenReturn("charge.refunded");
        when(eventParser.parse(anyString(), anyString())).thenReturn(event);

        webhookService.handle("payload", "signature");

        verifyNoInteractions(paymentService);
    }

    @Test
    void shouldThrowInvalidSignature_whenSignatureInvalid() {
        when(eventParser.parse("payload", "bad-signature"))
                .thenThrow(new InvalidWebhookSignatureException("Assinatura inválida"));

        assertThatThrownBy(() -> webhookService.handle("payload", "bad-signature"))
                .isInstanceOf(InvalidWebhookSignatureException.class);
    }
}
