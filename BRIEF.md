# BRIEF — kodapay-api-v2

## Projeto
**kodapay-api-v2** — API REST de pagamentos com webhooks do Stripe, controle de idempotência e consulta de pagamento. Área: Full-Stack.

## Caminho do projeto
`C:\Users\Kelvin\AppData\Local\Temp\opencode\github-staging\projects\kodapay-api-v2\`

## Stack
- Java 21
- Spring Boot 3.5.16 (Spring Web, Spring Data JPA, Validation)
- Stripe Java 34.0.0
- PostgreSQL (produção; configurado em `application.properties`)
- JUnit 5 + Mockito + AssertJ (testes)
- Maven 3.9.9 (wrapper `mvnw` / `mvnw.cmd` gerado no projeto)

## Escopo do MVP (entregue)
- `POST /payments` — criar pagamento (requer `currency`, `amount`, `idempotencyKey`)
- `GET /payments/{id}` — consultar pagamento por ID
- `POST /webhooks/stripe` — receber webhook do Stripe (`payment_intent.succeeded`, `payment_intent.payment_failed`)
- Controle de idempotência em memória (MVP): mesma chave não gera dois pagamentos
- Tratador global de exceções (404, 409, 400, 400 de assinatura inválida)

## Critérios de aceitação (PLANO_50_PROJETOS.md) — STATUS
- 26+ testes CI verde (JUnit): **27 testes, 0 falhas, 0 erros** ✅
- README PT-BR + EN completo (título, descrição, como usar, testes, MIT, autor Kelvin Oliveira): **entregue** ✅

## Comando de teste
```sh
./mvnw test
```
No Windows (PowerShell/CMD):
```sh
.\mvnw.cmd test
```

## Resultado dos testes
`Tests run: 27, Failures: 0, Errors: 0, Skipped: 0` — **BUILD SUCCESS**.
Os testes usam Mockito; não precisam de PostgreSQL nem do Stripe rodando.

## Como verificar
1. Abra o terminal na pasta do projeto:
   ```sh
   cd C:\Users\Kelvin\AppData\Local\Temp\opencode\github-staging\projects\kodapay-api-v2
   ```
2. Rode:
   ```sh
   .\mvnw.cmd test
   ```
   (ou `./mvnw test` em Linux/macOS)
3. Confira `BUILD SUCCESS` e `Tests run: 27, Failures: 0, Errors: 0`.

## Arquivos principais criados
- `pom.xml`
- `mvnw`, `mvnw.cmd`, `.mvn/wrapper/maven-wrapper.properties`
- `src/main/resources/application.properties`
- `src/main/java/com/kodapay/api/KodapayApiApplication.java`
- `src/main/java/com/kodapay/api/controller/` (`PaymentController`, `StripeWebhookController`)
- `src/main/java/com/kodapay/api/controller/dto/` (`PaymentRequest`, `PaymentResponse`)
- `src/main/java/com/kodapay/api/service/` (`PaymentService`, `IdempotencyService`, `StripeService`, `StripeWebhookService`, `EventParser`, `StripeEventParser`)
- `src/main/java/com/kodapay/api/domain/` (`Payment`, `PaymentStatus`, `PaymentRepository`)
- `src/main/java/com/kodapay/api/exception/` (`PaymentNotFoundException`, `IdempotencyConflictException`, `InvalidWebhookSignatureException`, `GlobalExceptionHandler`)
- `src/test/java/com/kodapay/api/` (6 classes de teste, 27 testes)
- `README.md`
- `BRIEF.md`

## Autor
Kelvin Oliveira
