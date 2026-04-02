package com.jewelry.jewelryshopbackend.service.productimage.impl;

import com.jewelry.jewelryshopbackend.dto.response.productimage.ProductImageResponse;
import com.jewelry.jewelryshopbackend.dto.response.productimage.ProductImageSummaryResponse;
import com.jewelry.jewelryshopbackend.entity.ProductImage;
import com.jewelry.jewelryshopbackend.enums.CategoryStatus;
import com.jewelry.jewelryshopbackend.exception.BadRequestException;
import com.jewelry.jewelryshopbackend.exception.ResourceNotFoundException;
import com.jewelry.jewelryshopbackend.mapper.ProductImageMapper;
import com.jewelry.jewelryshopbackend.payload.PageResponse;
import com.jewelry.jewelryshopbackend.repository.ProductImageRepository;
import com.jewelry.jewelryshopbackend.service.productimage.ProductImageQueryService;
import com.jewelry.jewelryshopbackend.specification.ProductImageSpecifications;
import com.jewelry.jewelryshopbackend.util.PageableUtils;
import com.jewelry.jewelryshopbackend.validator.ProductImageValidator;
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
public class ProductImageQueryServiceImpl implements ProductImageQueryService {

    private final ProductImageRepository productImageRepository;
    private final ProductImageValidator productImageValidator;
    private final ProductImageMapper productImageMapper;

    @Override
    public ProductImageResponse getByIdForAdmin(Long id) {
        ProductImage image = productImageValidator.validateExists(id);
        return productImageMapper.toResponse(image);
    }

    @Override
    public PageResponse<ProductImageResponse> getAllForAdmin(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String q,
            Long productId,
            Boolean isMain
    ) {
        Pageable pageable = PageableUtils.build(
                page,
                size,
                sortBy,
                sortDir,
                Set.of("id", "imageUrl", "isMain", "sortOrder", "createdAt", "updatedAt"),
                "createdAt"
        );
        Specification<ProductImage> spec = ProductImageSpecifications.adminFilter(q, productId, isMain);
        Page<ProductImage> result = productImageRepository.findAll(spec, pageable);

        return PageResponse.<ProductImageResponse>builder()
                .content(result.getContent().stream().map(productImageMapper::toResponse).toList())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .last(result.isLast())
                .build();
    }

    @Override
    public PageResponse<ProductImageSummaryResponse> getAllByProductSlugForPublic(
            String productSlug,
            int page,
            int size,
            String sortBy,
            String sortDir,
            String q,
            Boolean isMain
    ) {
        String normalizedSlug = normalizeProductSlug(productSlug);
        Pageable pageable = PageableUtils.build(
                page,
                size,
                sortBy,
                sortDir,
                Set.of("id", "imageUrl", "isMain", "sortOrder", "createdAt", "updatedAt"),
                "createdAt"
        );
        Specification<ProductImage> spec = ProductImageSpecifications.publicFilter(normalizedSlug, q, isMain);
        Page<ProductImage> result = productImageRepository.findAll(spec, pageable);
        return PageResponse.<ProductImageSummaryResponse>builder()
                .content(result.getContent().stream().map(productImageMapper::toSummary).toList())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .last(result.isLast())
                .build();
    }

    @Override
    public ProductImageResponse getByIdForPublic(String productSlug, Long imageId) {
        String normalizedSlug = normalizeProductSlug(productSlug);
        ProductImage image = productImageRepository
                .findByIdAndProduct_SlugIgnoreCaseAndProduct_StatusTrueAndProduct_Category_StatusAndDeletedFalse(
                        imageId,
                        normalizedSlug,
                        CategoryStatus.ACTIVE
                )
                .orElseThrow(() -> new ResourceNotFoundException("Product image not found with id: " + imageId));

        return productImageMapper.toResponse(image);
    }

    private String normalizeProductSlug(String productSlug) {
        String normalized = productSlug == null ? "" : productSlug.trim().toLowerCase();
        if (normalized.isBlank()) {
            throw new BadRequestException("Product slug is required");
        }
        return normalized;
    }
}
