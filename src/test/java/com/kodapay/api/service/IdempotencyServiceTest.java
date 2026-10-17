package com.kodapay.api.service;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class IdempotencyServiceTest {

    @Test
    void shouldReturnFalse_whenKeyNeverSeen() {
        IdempotencyService service = new IdempotencyService();
        assertThat(service.isProcessed("nova-chave")).isFalse();
    }

    @Test
    void shouldReturnTrue_afterMarkProcessed() {
        IdempotencyService service = new IdempotencyService();
        service.markProcessed("chave-1", "pay-1");
        assertThat(service.isProcessed("chave-1")).isTrue();
    }

    @Test
    void shouldRetrievePaymentId_whenMarked() {
        IdempotencyService service = new IdempotencyService();
        service.markProcessed("chave-2", "pay-2");
        Optional<String> id = service.getPaymentId("chave-2");
        assertThat(id).isPresent().hasValue("pay-2");
    }

    @Test
    void shouldReturnEmpty_whenKeyNotMarked() {
        IdempotencyService service = new IdempotencyService();
        assertThat(service.getPaymentId("desconhecida")).isEmpty();
    }

    @Test
    void shouldBeIndependentPerInstance() {
        IdempotencyService a = new IdempotencyService();
        IdempotencyService b = new IdempotencyService();
        a.markProcessed("compartilhada", "pay-a");
        assertThat(b.isProcessed("compartilhada")).isFalse();
    }

    @Test
    void shouldOverwritePreviousPaymentId_whenReMarked() {
        IdempotencyService service = new IdempotencyService();
        service.markProcessed("chave-3", "pay-primeiro");
        service.markProcessed("chave-3", "pay-segundo");
        assertThat(service.getPaymentId("chave-3")).hasValue("pay-segundo");
    }
}
