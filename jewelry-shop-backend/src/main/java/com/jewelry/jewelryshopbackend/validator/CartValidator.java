package com.jewelry.jewelryshopbackend.validator;

import com.jewelry.jewelryshopbackend.entity.ProductVariant;
import com.jewelry.jewelryshopbackend.exception.BadRequestException;
import org.springframework.stereotype.Component;

@Component
public class CartValidator {

    public void validateVariantCanAdd(ProductVariant variant, int quantity) {
        if (Boolean.FALSE.equals(variant.getStatus()) || Boolean.TRUE.equals(variant.getDeleted())) {
            throw new BadRequestException("Product variant is not available");
        }

        if (Boolean.FALSE.equals(variant.getProduct().getStatus())) {
            throw new BadRequestException("Product is not available");
        }

        if (variant.getStockQuantity() == null || variant.getStockQuantity() <= 0) {
            throw new BadRequestException("Product variant is out of stock");
        }

        if (quantity > variant.getStockQuantity()) {
            throw new BadRequestException("Requested quantity exceeds available stock");
        }
    }
}
