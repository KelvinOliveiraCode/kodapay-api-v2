package com.kodapay.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada da aplicação Kodapay API v2.
 *
 * <p>Spring Boot que expõe a API REST de pagamentos, webhooks do Stripe e
 * controle de idempotência.</p>
 */
@SpringBootApplication
public class KodapayApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(KodapayApiApplication.class, args);
    }
}
