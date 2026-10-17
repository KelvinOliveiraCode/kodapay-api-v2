package com.kodapay.api.exception;

/**
 * Lançada quando a assinatura de um webhook do Stripe não pode ser verificada.
 */
public class InvalidWebhookSignatureException extends RuntimeException {

    public InvalidWebhookSignatureException(String message) {
        super(message);
    }
}
