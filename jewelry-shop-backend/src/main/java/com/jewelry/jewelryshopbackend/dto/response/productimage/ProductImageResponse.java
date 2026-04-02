package com.jewelry.jewelryshopbackend.dto.response.productimage;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ProductImageResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String productSlug;
    private String imageUrl;
    private Boolean isMain;
    private Boolean deleted;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
