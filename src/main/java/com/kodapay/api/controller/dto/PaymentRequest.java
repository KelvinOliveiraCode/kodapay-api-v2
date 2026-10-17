package com.kodapay.api.controller.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Requesta de criação de pagamento.
 */
public record PaymentRequest(

    @NotBlank(message = "currency é obrigatória")
    String currency,

    @NotNull(message = "amount é obrigatório")
    @DecimalMin(value = "0.01", inclusive = false, message = "amount deve ser positivo")
    BigDecimal amount,

    @NotBlank(message = "idempotencyKey é obrigatória")
    String idempotencyKey
) {
}
