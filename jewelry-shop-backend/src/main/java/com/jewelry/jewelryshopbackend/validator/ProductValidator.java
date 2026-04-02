package com.jewelry.jewelryshopbackend.validator;

import com.jewelry.jewelryshopbackend.entity.Category;
import com.jewelry.jewelryshopbackend.entity.Product;
import com.jewelry.jewelryshopbackend.enums.CategoryStatus;
import com.jewelry.jewelryshopbackend.exception.BadRequestException;
import com.jewelry.jewelryshopbackend.exception.ResourceNotFoundException;
import com.jewelry.jewelryshopbackend.repository.CategoryRepository;
import com.jewelry.jewelryshopbackend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductValidator {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public Product validateExists(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    public Product validateExistsAndActive(Long id) {
        return productRepository.findByIdAndStatusTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Active product not found with id: " + id));
    }

    public Category validateActiveCategory(Long categoryId) {
        return categoryRepository.findByIdAndStatus(categoryId, CategoryStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Active category not found with id: " + categoryId));
    }

    public void validateCreateDuplicateSlug(String slug) {
        if (productRepository.existsBySlugIgnoreCase(slug)) {
            throw new BadRequestException("Product slug already exists");
        }
    }

    public void validateUpdateDuplicateSlug(Product product, String newSlug) {
        if (!product.getSlug().equalsIgnoreCase(newSlug)
                && productRepository.existsBySlugIgnoreCaseAndIdNot(newSlug, product.getId())) {
            throw new BadRequestException("Product slug already exists");
        }
    }
}
