package com.kodapay.api.exception;

/**
 * Lançada quando um pagamento não é encontrado pelo identificador informado.
 */
public class PaymentNotFoundException extends RuntimeException {

    public PaymentNotFoundException(String id) {
        super("Pagamento não encontrado: " + id);
    }
}
