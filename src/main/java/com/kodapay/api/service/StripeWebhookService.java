package com.kodapay.api.service;

import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import org.springframework.stereotype.Service;

/**
 * Serviço de tratamento de webhooks do Stripe.
 *
 * <p>Recebe o evento já verificado pelo {@link EventParser} e dispara a ação de
 * domínio correspondente (pagamento concluído ou falhado). Eventos não tratados
 * são ignorados silenciosamente (retorna 200 ao Stripe).</p>
 */
@Service
public class StripeWebhookService {

    private final PaymentService paymentService;
    private final EventParser eventParser;

    public StripeWebhookService(PaymentService paymentService, EventParser eventParser) {
        this.paymentService = paymentService;
        this.eventParser = eventParser;
    }

    public void handle(String payload, String signature) {
        Event event = eventParser.parse(payload, signature);
        String type = event.getType();

        switch (type) {
            case "payment_intent.succeeded" -> handleSucceeded(event);
            case "payment_intent.payment_failed" -> handleFailed(event);
            default -> {
                // Evento não tratado: ignorar e retornar 200 ao Stripe
            }
        }
    }

    private void handleSucceeded(Event event) {
        String intentId = extractIntentId(event);
        paymentService.handleWebhook(intentId, "COMPLETED");
    }

    private void handleFailed(Event event) {
        String intentId = extractIntentId(event);
        paymentService.handleWebhook(intentId, "FAILED");
    }

    private String extractIntentId(Event event) {
        StripeObject dataObject = event.getData().getObject();
        if (dataObject instanceof PaymentIntent intent) {
            return intent.getId();
        }
        throw new IllegalStateException("Objeto do evento não é um Payment Intent");
    }
}
