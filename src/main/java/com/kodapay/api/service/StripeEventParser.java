package com.kodapay.api.service;

import com.kodapay.api.exception.InvalidWebhookSignatureException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Implementação do {@link EventParser} baseada na biblioteca oficial do Stripe.
 *
 * <p>Verifica a assinatura do webhook e constrói o objeto {@link Event}.</p>
 */
@Component
public class StripeEventParser implements EventParser {

    private final String webhookSecret;

    public StripeEventParser(@Value("${kodapay.stripe.webhook-secret:}") String webhookSecret) {
        this.webhookSecret = webhookSecret;
    }

    @Override
    public Event parse(String payload, String signature) {
        try {
            return Webhook.constructEvent(payload, signature, webhookSecret);
        } catch (SignatureVerificationException e) {
            throw new InvalidWebhookSignatureException(
                    "Assinatura do webhook do Stripe inválida: " + e.getMessage());
        }
    }
}
