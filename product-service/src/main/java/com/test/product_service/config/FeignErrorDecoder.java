package com.test.product_service.config;

import com.test.product_service.exception.ProductNotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class FeignErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        return switch (response.status()) {
            case 400 -> new RuntimeException("Bad request to external API");
            case 404 -> new ProductNotFoundException("Product not found in external API");
            case 500 -> new RuntimeException("External API server error");
            default -> new RuntimeException("Error calling external API");
        };
    }
}