package com.jewelry.jewelryshopbackend.service.product;

import com.jewelry.jewelryshopbackend.dto.request.product.ProductCreateRequest;
import com.jewelry.jewelryshopbackend.dto.request.product.ProductUpdateRequest;
import com.jewelry.jewelryshopbackend.dto.response.product.ProductResponse;

public interface ProductCommandService {

    ProductResponse create(ProductCreateRequest request);

    ProductResponse update(Long id, ProductUpdateRequest request);

    void delete(Long id);
}
