package com.jewelry.jewelryshopbackend.service.productvariant.impl;

import com.jewelry.jewelryshopbackend.dto.response.productvariant.ProductVariantResponse;
import com.jewelry.jewelryshopbackend.dto.response.productvariant.ProductVariantSummaryResponse;
import com.jewelry.jewelryshopbackend.entity.ProductVariant;
import com.jewelry.jewelryshopbackend.enums.CategoryStatus;
import com.jewelry.jewelryshopbackend.exception.BadRequestException;
import com.jewelry.jewelryshopbackend.exception.ResourceNotFoundException;
import com.jewelry.jewelryshopbackend.mapper.ProductVariantMapper;
import com.jewelry.jewelryshopbackend.payload.PageResponse;
import com.jewelry.jewelryshopbackend.repository.ProductVariantRepository;
import com.jewelry.jewelryshopbackend.service.productvariant.ProductVariantQueryService;
import com.jewelry.jewelryshopbackend.specification.ProductVariantSpecifications;
import com.jewelry.jewelryshopbackend.util.PageableUtils;
import com.jewelry.jewelryshopbackend.validator.ProductVariantValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductVariantQueryServiceImpl implements ProductVariantQueryService {

    private final ProductVariantRepository productVariantRepository;
    private final ProductVariantValidator productVariantValidator;
    private final ProductVariantMapper productVariantMapper;

    @Override
    public ProductVariantResponse getByIdForAdmin(Long id) {
        ProductVariant variant = productVariantValidator.validateExists(id);
        return productVariantMapper.toResponse(variant);
    }

    @Override
    public PageResponse<ProductVariantResponse> getAllForAdmin(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String q,
            Long productId,
            Boolean status,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Integer minStock,
            Integer maxStock
    ) {
        validateRange(minPrice, maxPrice, "minPrice", "maxPrice");
        validateRange(minStock, maxStock, "minStock", "maxStock");
        Pageable pageable = PageableUtils.build(
                page,
                size,
                sortBy,
                sortDir,
                Set.of("id", "sku", "price", "stockQuantity", "status", "createdAt", "updatedAt"),
                "createdAt"
        );
        Specification<ProductVariant> spec = ProductVariantSpecifications.adminFilter(
                q, productId, status, minPrice, maxPrice, minStock, maxStock
        );
        Page<ProductVariant> result = productVariantRepository.findAll(spec, pageable);

        return PageResponse.<ProductVariantResponse>builder()
                .content(result.getContent().stream().map(productVariantMapper::toResponse).toList())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .last(result.isLast())
                .build();
    }

    @Override
    public PageResponse<ProductVariantSummaryResponse> getAllActiveByProductSlug(
            String productSlug,
            int page,
            int size,
            String sortBy,
            String sortDir,
            String q,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Integer minStock
    ) {
        String normalizedSlug = normalizeProductSlug(productSlug);
        validateRange(minPrice, maxPrice, "minPrice", "maxPrice");
        Pageable pageable = PageableUtils.build(
                page,
                size,
                sortBy,
                sortDir,
                Set.of("id", "sku", "price", "stockQuantity", "createdAt", "updatedAt"),
                "createdAt"
        );
        Specification<ProductVariant> spec = ProductVariantSpecifications.publicFilter(
                normalizedSlug, q, minPrice, maxPrice, minStock
        );
        Page<ProductVariant> result = productVariantRepository.findAll(spec, pageable);
        return PageResponse.<ProductVariantSummaryResponse>builder()
                .content(result.getContent().stream().map(productVariantMapper::toSummary).toList())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .last(result.isLast())
                .build();
    }

    @Override
    public ProductVariantResponse getByIdForPublic(String productSlug, Long variantId) {
        String normalizedSlug = normalizeProductSlug(productSlug);
        ProductVariant variant = productVariantRepository
                .findByIdAndProduct_SlugIgnoreCaseAndProduct_StatusTrueAndProduct_Category_StatusAndStatusTrueAndDeletedFalse(
                        variantId,
                        normalizedSlug,
                        CategoryStatus.ACTIVE
                )
                .orElseThrow(() -> new ResourceNotFoundException("Active product variant not found with id: " + variantId));
        return productVariantMapper.toResponse(variant);
    }

    private String normalizeProductSlug(String productSlug) {
        String normalized = productSlug == null ? "" : productSlug.trim().toLowerCase();
        if (normalized.isBlank()) {
            throw new BadRequestException("Product slug is required");
        }
        return normalized;
    }

    private <T extends Comparable<T>> void validateRange(T min, T max, String minLabel, String maxLabel) {
        if (min != null && max != null && min.compareTo(max) > 0) {
            throw new BadRequestException(minLabel + " must be less than or equal to " + maxLabel);
        }
    }
}
