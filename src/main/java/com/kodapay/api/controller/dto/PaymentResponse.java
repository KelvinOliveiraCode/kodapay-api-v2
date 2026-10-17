package com.kodapay.api.controller.dto;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Resposta de pagamento devolvida pela API.
 */
public record PaymentResponse(
    String id,
    BigDecimal amount,
    String currency,
    String status,
    String stripePaymentIntentId,
    String idempotencyKey,
    Instant createdAt,
    Instant updatedAt
) {
}
