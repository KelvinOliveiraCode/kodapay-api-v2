package com.kodapay.api.service;

import com.stripe.model.Event;

/**
 * Analisador de eventos do Stripe (webhook).
 *
 * <p>Interface de separação para permitir testes unitários do serviço de
 * webhook sem dependir da verificação de assinatura do Stripe.</p>
 */
public interface EventParser {

    Event parse(String payload, String signature);
}
