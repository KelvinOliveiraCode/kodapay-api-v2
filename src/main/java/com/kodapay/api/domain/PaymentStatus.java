package com.kodapay.api.domain;

/**
 * Status possíveis de um pagamento no ciclo de vida do Kodapay.
 */
public enum PaymentStatus {
    PENDING,
    COMPLETED,
    FAILED,
    REFUNDED
}
