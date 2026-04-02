package com.jewelry.jewelryshopbackend.validator;

import com.jewelry.jewelryshopbackend.entity.ProductImage;
import com.jewelry.jewelryshopbackend.exception.BadRequestException;
import com.jewelry.jewelryshopbackend.exception.ResourceNotFoundException;
import com.jewelry.jewelryshopbackend.repository.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductImageValidator {

    private final ProductImageRepository productImageRepository;

    public ProductImage validateExists(Long id) {
        return productImageRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product image not found with id: " + id));
    }

    public int validateSortOrder(Integer sortOrder) {
        if (sortOrder == null) {
            return 0;
        }
        if (sortOrder < 0) {
            throw new BadRequestException("Sort order must be greater than or equal to 0");
        }
        return sortOrder;
    }
}
