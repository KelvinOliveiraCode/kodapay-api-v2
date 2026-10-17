package com.kodapay.api.controller;

import com.kodapay.api.controller.dto.PaymentResponse;
import com.kodapay.api.exception.IdempotencyConflictException;
import com.kodapay.api.exception.PaymentNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private com.kodapay.api.service.PaymentService paymentService;

    private static final String ID = "pay_1";
    private static final BigDecimal AMOUNT = new BigDecimal("100.00");
    private static final String CURRENCY = "BRL";
    private static final String KEY = "key_1";

    private static final PaymentResponse RESPONSE = new PaymentResponse(
            ID, AMOUNT, CURRENCY, "COMPLETED", "pi_1", KEY, Instant.now(), Instant.now()
    );

    @Test
    void shouldReturnCreated_whenCreatePayment() throws Exception {
        when(paymentService.createPayment(any(com.kodapay.api.controller.dto.PaymentRequest.class)))
                .thenReturn(RESPONSE);

        String json = """
            {"currency":"BRL","amount":100.00,"idempotencyKey":"key_1"}
            """;

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(ID))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void shouldReturnConflict_whenDuplicateIdempotency() throws Exception {
        when(paymentService.createPayment(any(com.kodapay.api.controller.dto.PaymentRequest.class)))
                .thenThrow(new IdempotencyConflictException(KEY));

        String json = """
            {"currency":"BRL","amount":100.00,"idempotencyKey":"key_1"}
            """;

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("IDEMPOTENCY_CONFLICT"));
    }

    @Test
    void shouldReturnOk_whenGetExistingPayment() throws Exception {
        when(paymentService.getPayment(ID)).thenReturn(RESPONSE);

        mockMvc.perform(get("/payments/{id}", ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID));
    }

    @Test
    void shouldReturnNotFound_whenGetMissingPayment() throws Exception {
        when(paymentService.getPayment(anyString()))
                .thenThrow(new PaymentNotFoundException("missing"));

        mockMvc.perform(get("/payments/{id}", "missing"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequest_whenInvalidRequest() throws Exception {
        String json = """
            {"currency":"","amount":-5,"idempotencyKey":""}
            """;

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }
}
