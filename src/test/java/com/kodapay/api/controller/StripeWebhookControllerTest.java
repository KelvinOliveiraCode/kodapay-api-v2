package com.kodapay.api.controller;

import com.kodapay.api.exception.InvalidWebhookSignatureException;
import com.kodapay.api.service.StripeWebhookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StripeWebhookController.class)
class StripeWebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StripeWebhookService webhookService;

    @Test
    void shouldReturnNoContent_whenWebhookReceived() throws Exception {
        mockMvc.perform(post("/webhooks/stripe")
                        .header("Stripe-Signature", "sig")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"payment_intent.succeeded\"}"))
                .andExpect(status().isNoContent());

        verify(webhookService).handle("{\"type\":\"payment_intent.succeeded\"}", "sig");
    }

    @Test
    void shouldReturnBadRequest_whenInvalidSignature() throws Exception {
        doThrow(new InvalidWebhookSignatureException("bad"))
                .when(webhookService).handle("payload", "bad");

        mockMvc.perform(post("/webhooks/stripe")
                        .header("Stripe-Signature", "bad")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("payload"))
                .andExpect(status().isBadRequest());
    }
}
