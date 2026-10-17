package com.kodapay.api.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Cliente do Stripe: criação de Payment Intent.
 *
 * <p>Encapsula a chamada ao Stripe para que o restante da aplicação não dependa
 * diretamente da biblioteca do Stripe.</p>
 */
@Service
public class StripeService {

    private final String secretKey;

    public StripeService(@Value("${kodapay.stripe.secret-key:}") String secretKey) {
        this.secretKey = secretKey;
    }

    public String createPaymentIntent(BigDecimal amount, String currency) throws StripeException {
        if (secretKey == null || secretKey.isBlank()) {
            throw new IllegalStateException("kodapay.stripe.secret-key não configurada");
        }
        Stripe.apiKey = secretKey;
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount.multiply(BigDecimal.valueOf(100L)).longValue())
                .setCurrency(currency)
                .build();
        PaymentIntent intent = PaymentIntent.create(params);
        return intent.getId();
    }
}
