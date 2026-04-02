package com.jewelry.jewelryshopbackend.service.category.impl;


import com.jewelry.jewelryshopbackend.dto.request.category.CategoryCreateRequest;
import com.jewelry.jewelryshopbackend.dto.request.category.CategoryStatusUpdateRequest;
import com.jewelry.jewelryshopbackend.dto.request.category.CategoryUpdateRequest;
import com.jewelry.jewelryshopbackend.dto.response.category.CategoryResponse;
import com.jewelry.jewelryshopbackend.entity.Category;
import com.jewelry.jewelryshopbackend.enums.CategoryStatus;
import com.jewelry.jewelryshopbackend.exception.BadRequestException;
import com.jewelry.jewelryshopbackend.mapper.CategoryMapper;
import com.jewelry.jewelryshopbackend.repository.CategoryRepository;
import com.jewelry.jewelryshopbackend.service.category.CategoryCommandService;
import com.jewelry.jewelryshopbackend.util.SlugUtils;
import com.jewelry.jewelryshopbackend.validator.CategoryValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryCommandServiceImpl implements CategoryCommandService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final CategoryValidator categoryValidator;

    @Override
    public CategoryResponse create(CategoryCreateRequest request) {
        String normalizedName = normalizeName(request.getName());
        String normalizedSlug = resolveSlug(request.getSlug(), request.getName());
        String normalizedDescription = normalizeDescription(request.getDescription());

        categoryValidator.validateCreateDuplicateName(normalizedName);
        categoryValidator.validateCreateDuplicateSlug(normalizedSlug);

        Category category = Category.builder()
                .name(normalizedName)
                .slug(normalizedSlug)
                .description(normalizedDescription)
                .status(CategoryStatus.ACTIVE)
                .build();

        saveSafely(category);
        return categoryMapper.toResponse(category);
    }

    @Override
    public CategoryResponse update(Long id, CategoryUpdateRequest request) {
        Category category = categoryValidator.validateExistsAndNotDeleted(id);

        String normalizedName = normalizeName(request.getName());
        String normalizedSlug = resolveSlug(request.getSlug(), request.getName());
        String normalizedDescription = normalizeDescription(request.getDescription());

        categoryValidator.validateUpdateDuplicateName(category, normalizedName);
        categoryValidator.validateUpdateDuplicateSlug(category, normalizedSlug);

        category.setName(normalizedName);
        category.setSlug(normalizedSlug);
        category.setDescription(normalizedDescription);

        saveSafely(category);
        return categoryMapper.toResponse(category);
    }

    @Override
    public CategoryResponse changeStatus(Long id, CategoryStatusUpdateRequest request) {
        Category category = categoryValidator.validateExistsAndNotDeleted(id);
        CategoryStatus newStatus = categoryValidator.parseStatus(request.getStatus());

        categoryValidator.validateStatusChange(category, newStatus);

        if (newStatus == CategoryStatus.ACTIVE) {
            category.markActive();
        } else if (newStatus == CategoryStatus.INACTIVE) {
            category.markInactive();
        }

        saveSafely(category);
        return categoryMapper.toResponse(category);
    }

    @Override
    public void delete(Long id) {
        Category category = categoryValidator.validateExistsAndNotDeleted(id);
        categoryValidator.validateCanDelete(category);
        category.markDeleted();
        saveSafely(category);
    }

    private String normalizeName(String name) {
        String normalized = name == null ? "" : name.trim().replaceAll("\\s+", " ");
        if (normalized.isBlank()) {
            throw new BadRequestException("Category name cannot be blank");
        }
        return normalized;
    }

    private String normalizeDescription(String description) {
        if (description == null) {
            return null;
        }

        String normalized = description.trim();
        return normalized.isBlank() ? null : normalized;
    }

    private String resolveSlug(String requestSlug, String fallbackName) {
        String raw = (requestSlug == null || requestSlug.isBlank())
                ? SlugUtils.toSlug(fallbackName)
                : SlugUtils.toSlug(requestSlug);

        if (raw.isBlank()) {
            throw new BadRequestException("Category slug cannot be blank");
        }

        if (raw.length() > 120) {
            throw new BadRequestException("Category slug must not exceed 120 characters");
        }

        return raw;
    }

    private void saveSafely(Category category) {
        try {
            categoryRepository.save(category);
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Category data violates database constraints");
        }
    }
}
