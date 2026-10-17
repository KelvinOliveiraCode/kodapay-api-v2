package com.kodapay.api.exception;

/**
 * Lançada quando uma requesta reutiliza uma chave de idempotência já processada.
 */
public class IdempotencyConflictException extends RuntimeException {

    public IdempotencyConflictException(String key) {
        super("Chave de idempotência já processada: " + key);
    }
}
