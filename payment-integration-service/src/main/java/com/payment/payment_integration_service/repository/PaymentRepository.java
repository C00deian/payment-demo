package com.payment.payment_integration_service.repository;

import com.payment.payment_integration_service.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment , Long> {
    Optional<Payment> findByPaymentId(String paymentId);

    List<Payment> findTop20ByOrderByIdDesc();
}
