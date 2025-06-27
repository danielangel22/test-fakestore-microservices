package com.test.order_service.service;


import com.test.order_service.client.ProductServiceClient;
import com.test.order_service.dto.OrderItemResponse;
import com.test.order_service.dto.OrderRequest;
import com.test.order_service.dto.OrderResponse;
import com.test.order_service.dto.ProductResponse;
import com.test.order_service.exception.OrderException;
import com.test.order_service.model.Order;
import com.test.order_service.model.OrderItem;
import com.test.order_service.respository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductServiceClient productServiceClient;

    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {
        // Validate products and calculate total
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (var item : orderRequest.getItems()) {
            ProductResponse product = productServiceClient.getProductById(item.getProductId());

            if (product == null) {
                throw new OrderException("Product not found with id: " + item.getProductId());
            }

            if (product.getStock() < item.getQuantity()) {
                throw new OrderException("Insufficient stock for product: " + product.getTitle());
            }

            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }

        // Create order
        Order order = Order.builder()
                .userId(orderRequest.getUserId())
                .orderDate(LocalDateTime.now())
                .totalAmount(totalAmount)
                .status("CREATED")
                .build();

        // Create order items
        List<OrderItem> orderItems = orderRequest.getItems().stream()
                .map(item -> {
                    ProductResponse product = productServiceClient.getProductById(item.getProductId());
                    return OrderItem.builder()
                            .productId(item.getProductId())
                            .quantity(item.getQuantity())
                            .price(product.getPrice())
                            .order(order)
                            .build();
                })
                .collect(Collectors.toList());

        order.setItems(orderItems);

        // Save order
        Order savedOrder = orderRepository.save(order);

        return mapToOrderResponse(savedOrder);
    }

    @Cacheable(value = "order", key = "#id")
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderException("Order not found with id: " + id));

        return mapToOrderResponse(order);
    }

    @CachePut(value = "order", key = "#id")
    public OrderResponse updateOrder(Long id, OrderRequest orderRequest) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new OrderException("Order not found with id: " + id));

        Order updatedOrder = orderRepository.save(existingOrder);
        return mapToOrderResponse(updatedOrder);
    }

    @CacheEvict(value = "order", key = "#id")
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderException("Order not found with id: " + id));

        orderRepository.delete(order);
    }

    public void updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderException("Order not found with id: " + id));

        order.setStatus(status);
        orderRepository.save(order);
    }

    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .items(itemResponses)
                .build();
    }
}
