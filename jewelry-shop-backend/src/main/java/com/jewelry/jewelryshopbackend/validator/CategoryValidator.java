package com.jewelry.jewelryshopbackend.validator;

import com.jewelry.jewelryshopbackend.entity.Category;
import com.jewelry.jewelryshopbackend.enums.CategoryStatus;
import com.jewelry.jewelryshopbackend.exception.BadRequestException;
import com.jewelry.jewelryshopbackend.exception.ResourceNotFoundException;
import com.jewelry.jewelryshopbackend.repository.CategoryRepository;
import com.jewelry.jewelryshopbackend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryValidator {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public Category validateExistsAndNotDeleted(Long id) {
        return categoryRepository.findByIdAndStatusNot(id, CategoryStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    public void validateCreateDuplicateName(String name) {
        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new BadRequestException("Category name already exists");
        }
    }

    public void validateCreateDuplicateSlug(String slug) {
        if (categoryRepository.existsBySlugIgnoreCase(slug)) {
            throw new BadRequestException("Category slug already exists");
        }
    }

    public void validateUpdateDuplicateName(Category category, String newName) {
        if (!category.getName().equalsIgnoreCase(newName)
                && categoryRepository.existsByNameIgnoreCaseAndIdNot(newName, category.getId())) {
            throw new BadRequestException("Category name already exists");
        }
    }

    public void validateUpdateDuplicateSlug(Category category, String newSlug) {
        if (!category.getSlug().equalsIgnoreCase(newSlug)
                && categoryRepository.existsBySlugIgnoreCaseAndIdNot(newSlug, category.getId())) {
            throw new BadRequestException("Category slug already exists");
        }
    }

    public CategoryStatus parseStatus(String rawStatus) {
        try {
            return CategoryStatus.valueOf(rawStatus.trim().toUpperCase());
        } catch (Exception e) {
            throw new BadRequestException("Invalid category status: " + rawStatus);
        }
    }

    public void validateStatusChange(Category category, CategoryStatus newStatus) {
        if (category.isDeleted()) {
            throw new BadRequestException("Deleted category cannot be changed");
        }

        if (newStatus == CategoryStatus.DELETED) {
            throw new BadRequestException("Use delete API to delete category");
        }

        if (category.getStatus() == newStatus) {
            throw new BadRequestException("Category is already in status: " + newStatus);
        }

        if (newStatus == CategoryStatus.INACTIVE && hasActiveProducts(category.getId())) {
            throw new BadRequestException("Cannot inactivate category while active products still reference it");
        }
    }

    public void validateCanDelete(Category category) {
        if (hasActiveProducts(category.getId())) {
            throw new BadRequestException("Cannot delete category while active products still reference it");
        }
    }

    private boolean hasActiveProducts(Long categoryId) {
        return productRepository.existsByCategoryIdAndStatusTrue(categoryId);
    }
}
