package com.jewelry.jewelryshopbackend.service.product.impl;

import com.jewelry.jewelryshopbackend.dto.response.product.ProductResponse;
import com.jewelry.jewelryshopbackend.dto.response.product.ProductSummaryResponse;
import com.jewelry.jewelryshopbackend.entity.Product;
import com.jewelry.jewelryshopbackend.enums.CategoryStatus;
import com.jewelry.jewelryshopbackend.exception.ResourceNotFoundException;
import com.jewelry.jewelryshopbackend.mapper.ProductMapper;
import com.jewelry.jewelryshopbackend.payload.PageResponse;
import com.jewelry.jewelryshopbackend.repository.ProductRepository;
import com.jewelry.jewelryshopbackend.service.product.ProductQueryService;
import com.jewelry.jewelryshopbackend.specification.ProductSpecifications;
import com.jewelry.jewelryshopbackend.util.PageableUtils;
import com.jewelry.jewelryshopbackend.validator.ProductValidator;
import com.jewelry.jewelryshopbackend.exception.BadRequestException;
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
public class ProductQueryServiceImpl implements ProductQueryService {

    private final ProductRepository productRepository;
    private final ProductValidator productValidator;
    private final ProductMapper productMapper;

    @Override
    public ProductResponse getBySlugForPublic(String slug) {
        String normalizedSlug = slug == null ? "" : slug.trim().toLowerCase();

        Product product = productRepository.findBySlugIgnoreCaseAndStatusTrueAndCategory_Status(
                        normalizedSlug,
                        CategoryStatus.ACTIVE
                )
                .orElseThrow(() -> new ResourceNotFoundException("Active product not found with slug: " + slug));
        return productMapper.toResponse(product);
    }

    @Override
    public PageResponse<ProductSummaryResponse> getAllActive(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String q,
            Long categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice
    ) {
        validatePriceRange(minPrice, maxPrice);
        Pageable pageable = PageableUtils.build(
                page,
                size,
                sortBy,
                sortDir,
                Set.of("id", "name", "slug", "basePrice", "status", "createdAt", "updatedAt"),
                "createdAt"
        );
        Specification<Product> spec = ProductSpecifications.publicFilter(q, categoryId, minPrice, maxPrice);
        Page<Product> result = productRepository.findAll(spec, pageable);
        return PageResponse.<ProductSummaryResponse>builder()
                .content(result.getContent().stream().map(productMapper::toSummary).toList())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .last(result.isLast())
                .build();
    }

    @Override
    public ProductResponse getByIdForAdmin(Long id) {
        Product product = productValidator.validateExists(id);
        return productMapper.toResponse(product);
    }

    @Override
    public PageResponse<ProductResponse> getAllForAdmin(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String q,
            Long categoryId,
            Boolean status
    ) {
        Pageable pageable = PageableUtils.build(
                page,
                size,
                sortBy,
                sortDir,
                Set.of("id", "name", "slug", "basePrice", "status", "createdAt", "updatedAt"),
                "createdAt"
        );
        Specification<Product> spec = ProductSpecifications.adminFilter(q, categoryId, status);
        Page<Product> result = productRepository.findAll(spec, pageable);
        return toPageResponse(result);
    }

    private void validatePriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new BadRequestException("minPrice must be less than or equal to maxPrice");
        }
    }

    private PageResponse<ProductResponse> toPageResponse(Page<Product> result) {
        return PageResponse.<ProductResponse>builder()
                .content(result.getContent().stream().map(productMapper::toResponse).toList())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .last(result.isLast())
                .build();
    }
}
