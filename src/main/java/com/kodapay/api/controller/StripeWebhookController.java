package com.kodapay.api.controller;

import com.kodapay.api.service.StripeWebhookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Webhook do Stripe.
 *
 * <p>Recebe eventos do Stripe ({@code POST /webhooks/stripe}), verifica a
 * assinatura e encaminha para o serviço de domínio.</p>
 */
@RestController
@RequestMapping("/webhooks/stripe")
public class StripeWebhookController {

    private final StripeWebhookService webhookService;

    public StripeWebhookController(StripeWebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping
    public ResponseEntity<Void> receive(@RequestHeader("Stripe-Signature") String signature,
                                        @RequestBody String payload) {
        webhookService.handle(payload, signature);
        return ResponseEntity.noContent().build();
    }
}
