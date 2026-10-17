package com.kodapay.api.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StripeServiceTest {

    @Test
    void shouldThrowIllegalState_whenSecretKeyIsBlank() {
        StripeService service = new StripeService("   ");

        assertThatThrownBy(() -> service.createPaymentIntent(new BigDecimal("10.00"), "BRL"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("kodapay.stripe.secret-key");
    }
}
