package com.test.product_service.client;

import com.test.product_service.dto.ExternalProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


@FeignClient(name = "fake-store-api", url = "${external.api.url}")
public interface FakeStoreApiClient {
    @GetMapping("/products")
    List<ExternalProductDto> getAllProducts();

    @GetMapping("/products/{id}")
    ExternalProductDto getProductById(@PathVariable Long id);
}