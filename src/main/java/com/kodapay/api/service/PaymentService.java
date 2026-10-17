package com.kodapay.api.service;

import com.kodapay.api.controller.dto.PaymentRequest;
import com.kodapay.api.controller.dto.PaymentResponse;
import com.kodapay.api.domain.Payment;
import com.kodapay.api.domain.PaymentRepository;
import com.kodapay.api.domain.PaymentStatus;
import com.kodapay.api.exception.IdempotencyConflictException;
import com.kodapay.api.exception.PaymentNotFoundException;
import com.stripe.exception.StripeException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Serviço de domínio de pagamentos.
 *
 * <p>Coordena a criação de pagamentos, a consulta por identificador e o
 * tratamento de webhooks do Stripe, aplicando controle de idempotência.</p>
 */
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final IdempotencyService idempotencyService;
    private final StripeService stripeService;

    public PaymentService(PaymentRepository paymentRepository,
                          IdempotencyService idempotencyService,
                          StripeService stripeService) {
        this.paymentRepository = paymentRepository;
        this.idempotencyService = idempotencyService;
        this.stripeService = stripeService;
    }

    @Transactional
    public PaymentResponse createPayment(PaymentRequest request) {
        if (request.idempotencyKey() != null && idempotencyService.isProcessed(request.idempotencyKey())) {
            throw new IdempotencyConflictException(request.idempotencyKey());
        }

        String stripePaymentIntentId;
        try {
            stripePaymentIntentId = stripeService.createPaymentIntent(request.amount(), request.currency());
        } catch (StripeException ex) {
            throw new IllegalStateException("Falha ao criar Payment Intent no Stripe", ex);
        }

        Instant now = Instant.now();
        Payment payment = new Payment(
                UUID.randomUUID().toString(),
                request.amount(),
                request.currency(),
                PaymentStatus.PENDING,
                stripePaymentIntentId,
                request.idempotencyKey(),
                now,
                now
        );

        Payment saved = paymentRepository.save(payment);

        if (request.idempotencyKey() != null) {
            idempotencyService.markProcessed(request.idempotencyKey(), saved.getId());
        }

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPayment(String id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
        return toResponse(payment);
    }

    @Transactional
    public PaymentResponse handleWebhook(String stripePaymentIntentId, String status) {
        Payment payment = paymentRepository.findByStripePaymentIntentId(stripePaymentIntentId)
                .orElseThrow(() -> new PaymentNotFoundException(
                        "Payment intent do Stripe: " + stripePaymentIntentId));

        PaymentStatus newStatus = PaymentStatus.valueOf(status);
        payment.setStatus(newStatus);
        payment.setUpdatedAt(Instant.now());

        Payment saved = paymentRepository.save(payment);
        return toResponse(saved);
    }

    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus().name(),
                payment.getStripePaymentIntentId(),
                payment.getIdempotencyKey(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }
}
