package com.test.product_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExternalProductDto {
    private Long id;
    private String title;
    private BigDecimal price;
    private String description;
    private String category;

    @JsonProperty("rating")
    private Rating rating;

    @Data
    public static class Rating {
        private Integer count;
    }

    public Integer getStock() {
        return rating != null ? rating.getCount() : 0;
    }
}
