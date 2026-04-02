package com.jewelry.jewelryshopbackend.service.category;


import com.jewelry.jewelryshopbackend.dto.response.category.CategoryResponse;
import com.jewelry.jewelryshopbackend.dto.response.category.CategorySummaryResponse;
import com.jewelry.jewelryshopbackend.payload.PageResponse;

public interface CategoryQueryService {

    CategoryResponse getByIdForAdmin(Long id);

    CategoryResponse getBySlugForPublic(String slug);

    PageResponse<CategoryResponse> getAllForAdmin(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String q,
            String status
    );

    PageResponse<CategorySummaryResponse> getAllActive(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String q
    );
}
