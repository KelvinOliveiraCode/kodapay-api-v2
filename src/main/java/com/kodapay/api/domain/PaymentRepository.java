package com.kodapay.api.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositório de pagamentos.
 *
 * <p>Oferece consultas por chave de idempotência e por payment intent do Stripe,
 * usadas respectivamente pelo controle de idempotência e pelos webhooks.</p>
 */
public interface PaymentRepository extends JpaRepository<Payment, String> {

    Optional<Payment> findByIdempotencyKey(String idempotencyKey);

    Optional<Payment> findByStripePaymentIntentId(String stripePaymentIntentId);

    boolean existsByIdempotencyKey(String idempotencyKey);
}
