package com.jewelry.jewelryshopbackend.validator;

import com.jewelry.jewelryshopbackend.entity.ProductVariant;
import com.jewelry.jewelryshopbackend.exception.BadRequestException;
import com.jewelry.jewelryshopbackend.exception.ResourceNotFoundException;
import com.jewelry.jewelryshopbackend.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductVariantValidator {

    private final ProductVariantRepository productVariantRepository;

    public ProductVariant validateExists(Long id) {
        return productVariantRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product variant not found with id: " + id));
    }

    public void validateCreateDuplicateSku(String sku) {
        if (productVariantRepository.existsBySkuIgnoreCase(sku)) {
            throw new BadRequestException("Product variant SKU already exists");
        }
    }

    public void validateUpdateDuplicateSku(ProductVariant variant, String newSku) {
        if (!variant.getSku().equalsIgnoreCase(newSku)
                && productVariantRepository.existsBySkuIgnoreCaseAndIdNot(newSku, variant.getId())) {
            throw new BadRequestException("Product variant SKU already exists");
        }
    }
}
