package com.jewelry.jewelryshopbackend.service.productvariant;

import com.jewelry.jewelryshopbackend.dto.request.productvariant.ProductVariantCreateRequest;
import com.jewelry.jewelryshopbackend.dto.request.productvariant.ProductVariantStatusUpdateRequest;
import com.jewelry.jewelryshopbackend.dto.request.productvariant.ProductVariantUpdateRequest;
import com.jewelry.jewelryshopbackend.dto.response.productvariant.ProductVariantResponse;

public interface ProductVariantCommandService {

    ProductVariantResponse create(ProductVariantCreateRequest request);

    ProductVariantResponse update(Long id, ProductVariantUpdateRequest request);

    ProductVariantResponse changeStatus(Long id, ProductVariantStatusUpdateRequest request);

    void delete(Long id);
}
