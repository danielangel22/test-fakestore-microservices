package com.test.product_service.service;

import com.test.product_service.client.FakeStoreApiClient;
import com.test.product_service.dto.ExternalProductDto;
import com.test.product_service.dto.ProductResponse;
import com.test.product_service.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final FakeStoreApiClient fakeStoreApiClient;

    @Cacheable(value = "products")
    public List<ProductResponse> getAllProducts() {
        return fakeStoreApiClient.getAllProducts().stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "product", key = "#id")
    public ProductResponse getProductById(Long id) {
        ExternalProductDto externalProduct = fakeStoreApiClient.getProductById(id);
        if (externalProduct == null) {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
        return mapToProductResponse(externalProduct);
    }

    @CacheEvict(allEntries = true, value = {"products", "product"})
    @Scheduled(fixedRate = 1800000) // Clear cache every 30 minutes
    public void evictAllCacheValues() {
    }

    private ProductResponse mapToProductResponse(ExternalProductDto externalProduct) {
        return ProductResponse.builder()
                .id(externalProduct.getId())
                .title(externalProduct.getTitle())
                .price(externalProduct.getPrice())
                .description(externalProduct.getDescription())
                .category(externalProduct.getCategory())
                .stock(externalProduct.getStock())
                .build();
    }
}