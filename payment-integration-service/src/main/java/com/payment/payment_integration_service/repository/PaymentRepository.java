package com.payment.payment_integration_service.repository;

import com.payment.payment_integration_service.model.Payment;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment , Long> {
    Optional<Payment> findByPaymentId(String paymentId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Payment p where p.paymentId = :paymentId")
    Optional<Payment> findByPaymentIdForUpdate(@Param("paymentId") String paymentId);

    List<Payment> findTop20ByOrderByIdDesc();
}
