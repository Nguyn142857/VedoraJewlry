package com.jewelry.jewelryshopbackend.mapper;

import com.jewelry.jewelryshopbackend.dto.response.product.ProductResponse;
import com.jewelry.jewelryshopbackend.dto.response.product.ProductSummaryResponse;
import com.jewelry.jewelryshopbackend.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .description(product.getDescription())
                .material(product.getMaterial())
                .basePrice(product.getBasePrice())
                .thumbnail(product.getThumbnail())
                .status(product.getStatus())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public ProductSummaryResponse toSummary(Product product) {
        ProductSummaryResponse response = new ProductSummaryResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setSlug(product.getSlug());
        response.setBasePrice(product.getBasePrice());
        response.setThumbnail(product.getThumbnail());
        response.setStatus(product.getStatus());
        response.setCategoryId(product.getCategory().getId());
        response.setCategoryName(product.getCategory().getName());
        return response;
    }
}
