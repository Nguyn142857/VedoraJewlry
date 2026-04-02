package com.jewelry.jewelryshopbackend.service.category.impl;


import com.jewelry.jewelryshopbackend.dto.response.category.CategoryResponse;
import com.jewelry.jewelryshopbackend.dto.response.category.CategorySummaryResponse;
import com.jewelry.jewelryshopbackend.entity.Category;
import com.jewelry.jewelryshopbackend.enums.CategoryStatus;
import com.jewelry.jewelryshopbackend.exception.BadRequestException;
import com.jewelry.jewelryshopbackend.exception.ResourceNotFoundException;
import com.jewelry.jewelryshopbackend.mapper.CategoryMapper;
import com.jewelry.jewelryshopbackend.payload.PageResponse;
import com.jewelry.jewelryshopbackend.repository.CategoryRepository;
import com.jewelry.jewelryshopbackend.service.category.CategoryQueryService;
import com.jewelry.jewelryshopbackend.specification.CategorySpecifications;
import com.jewelry.jewelryshopbackend.util.PageableUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryQueryServiceImpl implements CategoryQueryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponse getByIdForAdmin(Long id) {
        Category category = categoryRepository.findByIdAndStatusNot(id, CategoryStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        return categoryMapper.toResponse(category);
    }

    @Override
    public CategoryResponse getBySlugForPublic(String slug) {
        String normalizedSlug = slug == null ? "" : slug.trim().toLowerCase();

        Category category = categoryRepository.findBySlugIgnoreCaseAndStatus(normalizedSlug, CategoryStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with slug: " + slug));

        return categoryMapper.toResponse(category);
    }

    @Override
    public PageResponse<CategoryResponse> getAllForAdmin(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String q,
            String status
    ) {
        Pageable pageable = PageableUtils.build(
                page,
                size,
                sortBy,
                sortDir,
                Set.of("id", "name", "slug", "status", "createdAt", "updatedAt"),
                "createdAt"
        );
        Specification<Category> spec = CategorySpecifications.adminFilter(q, parseStatus(status));
        Page<Category> result = categoryRepository.findAll(spec, pageable);

        return PageResponse.<CategoryResponse>builder()
                .content(result.getContent().stream().map(categoryMapper::toResponse).toList())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .last(result.isLast())
                .build();
    }

    @Override
    public PageResponse<CategorySummaryResponse> getAllActive(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String q
    ) {
        Pageable pageable = PageableUtils.build(
                page,
                size,
                sortBy,
                sortDir,
                Set.of("id", "name", "slug", "createdAt", "updatedAt"),
                "createdAt"
        );
        Specification<Category> spec = CategorySpecifications.publicFilter(q);
        Page<Category> result = categoryRepository.findAll(spec, pageable);

        return PageResponse.<CategorySummaryResponse>builder()
                .content(result.getContent().stream().map(categoryMapper::toSummary).toList())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .last(result.isLast())
                .build();
    }

    private CategoryStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            CategoryStatus parsed = CategoryStatus.valueOf(status.trim().toUpperCase());
            if (parsed == CategoryStatus.DELETED) {
                throw new BadRequestException("Invalid category status. Allowed values: ACTIVE, INACTIVE");
            }
            return parsed;
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid category status. Allowed values: ACTIVE, INACTIVE");
        }
    }
}
