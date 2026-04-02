package com.jewelry.jewelryshopbackend.service.productimage;

import com.jewelry.jewelryshopbackend.dto.request.productimage.ProductImageCreateRequest;
import com.jewelry.jewelryshopbackend.dto.request.productimage.ProductImageUpdateRequest;
import com.jewelry.jewelryshopbackend.dto.response.productimage.ProductImageResponse;

public interface ProductImageCommandService {

    ProductImageResponse create(ProductImageCreateRequest request);

    ProductImageResponse update(Long id, ProductImageUpdateRequest request);

    void delete(Long id);
}
