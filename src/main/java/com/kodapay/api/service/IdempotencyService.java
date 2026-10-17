package com.kodapay.api.service;

import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Controle de idempotência em memória (MVP).
 *
 * <p>Garante que uma mesma chave de idempotência não gere dois pagamentos
 * distintos. Em produção esta camada deve ser substituída por um armazenamento
 * persistente (ex: tabela dedicada no PostgreSQL com {@code UNIQUE(key)}).</p>
 */
@Service
public class IdempotencyService {

    private final ConcurrentHashMap<String, String> processedKeys = new ConcurrentHashMap<>();

    public boolean isProcessed(String key) {
        return processedKeys.containsKey(key);
    }

    public void markProcessed(String key, String paymentId) {
        processedKeys.put(key, paymentId);
    }

    public Optional<String> getPaymentId(String key) {
        return Optional.ofNullable(processedKeys.get(key));
    }
}
