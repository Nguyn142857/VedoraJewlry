package com.jewelry.jewelryshopbackend.service.category;

import com.jewelry.jewelryshopbackend.dto.request.category.CategoryCreateRequest;
import com.jewelry.jewelryshopbackend.dto.request.category.CategoryStatusUpdateRequest;
import com.jewelry.jewelryshopbackend.dto.request.category.CategoryUpdateRequest;
import com.jewelry.jewelryshopbackend.dto.response.category.CategoryResponse;

public interface CategoryCommandService {

    CategoryResponse create(CategoryCreateRequest request);

    CategoryResponse update(Long id, CategoryUpdateRequest request);

    CategoryResponse changeStatus(Long id, CategoryStatusUpdateRequest request);

    void delete(Long id);
}