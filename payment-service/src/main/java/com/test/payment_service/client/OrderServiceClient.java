package com.test.payment_service.client;

import com.test.payment_service.config.CustomFeignConfig;
import com.test.payment_service.dto.OrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "order-service", url = "${order-service.url}", configuration = CustomFeignConfig.class)
public interface OrderServiceClient {
    @GetMapping("/api/orders/{id}")
    OrderResponse getOrderById(@PathVariable Long id);

    @PutMapping("/api/orders/{id}/status")
    void updateOrderStatus(@PathVariable Long id, @RequestParam String status);
}

