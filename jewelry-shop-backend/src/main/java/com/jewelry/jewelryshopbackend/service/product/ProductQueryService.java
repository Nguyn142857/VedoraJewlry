package com.jewelry.jewelryshopbackend.service.product;

import com.jewelry.jewelryshopbackend.dto.response.product.ProductResponse;
import com.jewelry.jewelryshopbackend.dto.response.product.ProductSummaryResponse;
import com.jewelry.jewelryshopbackend.payload.PageResponse;

import java.math.BigDecimal;

public interface ProductQueryService {

    ProductResponse getBySlugForPublic(String slug);

    PageResponse<ProductSummaryResponse> getAllActive(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String q,
            Long categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice
    );

    ProductResponse getByIdForAdmin(Long id);

    PageResponse<ProductResponse> getAllForAdmin(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String q,
            Long categoryId,
            Boolean status
    );
}
