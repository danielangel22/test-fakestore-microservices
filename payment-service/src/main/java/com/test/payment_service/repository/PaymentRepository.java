package com.test.payment_service.repository;

import com.test.payment_service.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByOrderId(Long orderId);
}

