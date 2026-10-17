# kodapay-api-v2

API REST de pagamentos com webhooks do Stripe, controle de idempotência e consulta de pagamento.

---

## Descrição

Criar um pagamento (`POST /payments`), consultar um pagamento pelo ID (`GET /payments/{id}`) e receber webhooks do Stripe (`POST /webhooks/stripe`). A chave de idempotência impede que a mesma requisição gere dois pagamentos distintos.

Feito para ser o núcleo de um serviço de pagamentos: simples, determinístico e sem dependências externas nos testes.

---

## Description

Create a payment (`POST /payments`), look up a payment by ID (`GET /payments/{id}`), and receive Stripe webhooks (`POST /webhooks/stripe`). The idempotency key prevents the same request from producing two different payments.

Built as the core of a payment service: simple, deterministic, and with no external services required by the tests.

---

## Stack

- Java 21
- Spring Boot 3.5.16
- Spring Data JPA + PostgreSQL
- Stripe Java 34.0.0
- JUnit 5, Mockito, AssertJ (testes)
- Maven 3.9.9 (wrapper `./mvnw`)

---

## Endpoints

| Método | Caminho | O que faz |
|--------|---------|-----------|
| POST | `/payments` | Cria um pagamento. Requer `currency`, `amount` e `idempotencyKey`. Retorna 201. |
| GET | `/payments/{id}` | Consulta um pagamento pelo ID. Retorna 200 ou 404. |
| POST | `/webhooks/stripe` | Recebe webhook do Stripe (`payment_intent.succeeded`, `payment_intent.payment_failed`). Retorna 204. |

| Method | Path | What it does |
|--------|------|--------------|
| POST | `/payments` | Creates a payment. Requires `currency`, `amount` and `idempotencyKey`. Returns 201. |
| GET | `/payments/{id}` | Looks up a payment by ID. Returns 200 or 404. |
| POST | `/webhooks/stripe` | Receives a Stripe webhook (`payment_intent.succeeded`, `payment_intent.payment_failed`). Returns 204. |

---

## Como usar

### Pré-requisitos

- JDK 21
- Maven 3.9.9 (o wrapper `./mvnw` está no projeto)
- PostgreSQL para produção (configure em `application.properties`)

### Variáveis

Defina em `src/main/resources/application.properties`:

```properties
kodapay.stripe.secret-key=sk_test_...
kodapay.stripe.webhook-secret=whsec_...
spring.datasource.url=jdbc:postgresql://localhost:5432/kodapay
spring.datasource.username=kodapay
spring.datasource.password=kodapay
```

### Rodar

```sh
./mvnw spring-boot:run
```

A API escuta na porta 8080.

---

## How to use

### Prerequisites

- JDK 21
- Maven 3.9.9 (the `./mvnw` wrapper is in the project)
- PostgreSQL for production (configure in `application.properties`)

### Variables

Set in `src/main/resources/application.properties`:

```properties
kodapay.stripe.secret-key=sk_test_...
kodapay.stripe.webhook-secret=whsec_...
spring.datasource.url=jdbc:postgresql://localhost:5432/kodapay
spring.datasource.username=kodapay
spring.datasource.password=kodapay
```

### Run

```sh
./mvnw spring-boot:run
```

The API listens on port 8080.

---

## Testes

```sh
./mvnw test
```

Resultado: 27 testes, 0 falhas, 0 erros. Os testes usam Mockito, então não precisam de banco nem do Stripe rodando.

---

## Tests

```sh
./mvnw test
```

Result: 27 tests, 0 failures, 0 errors. Tests use Mockito, so they need neither a database nor Stripe running.

---

## Licença

MIT License

Copyright (c) 2026 Kelvin Oliveira

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

---

## Autor / Author

Kelvin Oliveira
