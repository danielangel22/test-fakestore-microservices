package com.test.payment_service.service;


import com.test.payment_service.client.OrderServiceClient;
import com.test.payment_service.dto.PaymentRequest;
import com.test.payment_service.dto.PaymentResponse;
import com.test.payment_service.dto.PaymentStatus;
import com.test.payment_service.exception.PaymentException;
import com.test.payment_service.model.Payment;
import com.test.payment_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderServiceClient orderServiceClient;

    @Transactional
    public PaymentResponse processPayment(PaymentRequest paymentRequest) {
        // Validate order exists
        var order = orderServiceClient.getOrderById(paymentRequest.getOrderId());

        if (order == null) {
            throw new PaymentException("Order not found with id: " + paymentRequest.getOrderId());
        }

        // Validate amount matches order total
        if (!paymentRequest.getAmount().equals(order.getTotalAmount().doubleValue())) {
            throw new PaymentException("Payment amount does not match order total");
        }

        // Simulate payment processing (in a real app, this would call a payment gateway)
        boolean isPaymentSuccessful = simulatePaymentGateway();

        // Create and save payment
        Payment payment = Payment.builder()
                .orderId(paymentRequest.getOrderId())
                .amount(paymentRequest.getAmount())
                .status(isPaymentSuccessful ? PaymentStatus.SUCCESS.name() : PaymentStatus.FAILED.name())
                .paymentDate(LocalDateTime.now())
                .paymentMethod(paymentRequest.getPaymentMethod())
                .build();

        payment = paymentRepository.save(payment);

        // Update order status
        if (isPaymentSuccessful) {
            orderServiceClient.updateOrderStatus(paymentRequest.getOrderId(), "PAID");
        } else {
            orderServiceClient.updateOrderStatus(paymentRequest.getOrderId(), "PAYMENT_FAILED");
        }

        return mapToPaymentResponse(payment);
    }

    private boolean simulatePaymentGateway() {
        return Math.random() > 0.2;
    }

    private PaymentResponse mapToPaymentResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .paymentDate(payment.getPaymentDate())
                .paymentMethod(payment.getPaymentMethod())
                .build();
    }
}

