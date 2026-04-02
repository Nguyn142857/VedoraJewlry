package com.jewelry.jewelryshopbackend.service.productimage;

import com.jewelry.jewelryshopbackend.dto.response.productimage.ProductImageResponse;
import com.jewelry.jewelryshopbackend.dto.response.productimage.ProductImageSummaryResponse;
import com.jewelry.jewelryshopbackend.payload.PageResponse;

public interface ProductImageQueryService {

    ProductImageResponse getByIdForAdmin(Long id);

    PageResponse<ProductImageResponse> getAllForAdmin(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String q,
            Long productId,
            Boolean isMain
    );

    PageResponse<ProductImageSummaryResponse> getAllByProductSlugForPublic(
            String productSlug,
            int page,
            int size,
            String sortBy,
            String sortDir,
            String q,
            Boolean isMain
    );

    ProductImageResponse getByIdForPublic(String productSlug, Long imageId);
}
