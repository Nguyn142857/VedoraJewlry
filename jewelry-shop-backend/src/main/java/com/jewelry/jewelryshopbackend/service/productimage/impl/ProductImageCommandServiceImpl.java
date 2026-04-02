package com.jewelry.jewelryshopbackend.service.productimage.impl;

import com.jewelry.jewelryshopbackend.dto.request.productimage.ProductImageCreateRequest;
import com.jewelry.jewelryshopbackend.dto.request.productimage.ProductImageUpdateRequest;
import com.jewelry.jewelryshopbackend.dto.response.productimage.ProductImageResponse;
import com.jewelry.jewelryshopbackend.entity.Product;
import com.jewelry.jewelryshopbackend.entity.ProductImage;
import com.jewelry.jewelryshopbackend.exception.BadRequestException;
import com.jewelry.jewelryshopbackend.mapper.ProductImageMapper;
import com.jewelry.jewelryshopbackend.repository.ProductImageRepository;
import com.jewelry.jewelryshopbackend.service.productimage.ProductImageCommandService;
import com.jewelry.jewelryshopbackend.validator.ProductImageValidator;
import com.jewelry.jewelryshopbackend.validator.ProductValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductImageCommandServiceImpl implements ProductImageCommandService {

    private final ProductImageRepository productImageRepository;
    private final ProductImageValidator productImageValidator;
    private final ProductImageMapper productImageMapper;
    private final ProductValidator productValidator;

    @Override
    public ProductImageResponse create(ProductImageCreateRequest request) {
        Product product = productValidator.validateExistsAndActive(request.getProductId());
        String normalizedImageUrl = normalizeImageUrl(request.getImageUrl());
        int normalizedSortOrder = productImageValidator.validateSortOrder(request.getSortOrder());

        boolean hasMain = productImageRepository.existsByProduct_IdAndIsMainTrueAndDeletedFalse(product.getId());
        boolean shouldBeMain = request.getIsMain() != null ? request.getIsMain() : !hasMain;

        if (shouldBeMain) {
            clearMainImage(product.getId());
        }

        ProductImage image = ProductImage.builder()
                .product(product)
                .imageUrl(normalizedImageUrl)
                .isMain(shouldBeMain)
                .sortOrder(normalizedSortOrder)
                .deleted(false)
                .build();
        productImageRepository.save(image);

        if (shouldBeMain) {
            product.setThumbnail(normalizedImageUrl);
        }

        return productImageMapper.toResponse(image);
    }

    @Override
    public ProductImageResponse update(Long id, ProductImageUpdateRequest request) {
        ProductImage image = productImageValidator.validateExists(id);

        String normalizedImageUrl = normalizeImageUrl(request.getImageUrl());
        int normalizedSortOrder = productImageValidator.validateSortOrder(request.getSortOrder());
        boolean wasMain = Boolean.TRUE.equals(image.getIsMain());
        boolean shouldBeMain = request.getIsMain() != null ? request.getIsMain() : image.getIsMain();

        if (shouldBeMain && !wasMain) {
            clearMainImage(image.getProduct().getId());
        }

        image.setImageUrl(normalizedImageUrl);
        image.setSortOrder(normalizedSortOrder);
        image.setIsMain(shouldBeMain);

        if (!shouldBeMain && wasMain) {
            image.getProduct().setThumbnail(null);
        }

        productImageRepository.save(image);

        if (shouldBeMain) {
            image.getProduct().setThumbnail(normalizedImageUrl);
        } else if (wasMain) {
            assignFallbackMainImage(image.getProduct().getId());
        }

        return productImageMapper.toResponse(image);
    }

    @Override
    public void delete(Long id) {
        ProductImage image = productImageValidator.validateExists(id);
        Long productId = image.getProduct().getId();
        boolean wasMain = Boolean.TRUE.equals(image.getIsMain());

        image.setDeleted(true);
        image.setIsMain(false);
        productImageRepository.save(image);

        if (wasMain) {
            image.getProduct().setThumbnail(null);
            assignFallbackMainImage(productId);
        }
    }

    private void clearMainImage(Long productId) {
        List<ProductImage> images = productImageRepository.findAllByProduct_IdAndDeletedFalseOrderByIsMainDescSortOrderAscCreatedAtAsc(productId);
        for (ProductImage image : images) {
            if (Boolean.TRUE.equals(image.getIsMain())) {
                image.setIsMain(false);
            }
        }
    }

    private void assignFallbackMainImage(Long productId) {
        List<ProductImage> images = productImageRepository.findAllByProduct_IdAndDeletedFalseOrderByIsMainDescSortOrderAscCreatedAtAsc(productId);
        if (images.isEmpty()) {
            return;
        }

        ProductImage first = images.get(0);
        first.setIsMain(true);
        first.getProduct().setThumbnail(first.getImageUrl());
    }

    private String normalizeImageUrl(String imageUrl) {
        String normalized = imageUrl == null ? "" : imageUrl.trim();
        if (normalized.isBlank()) {
            throw new BadRequestException("Image URL cannot be blank");
        }
        if (normalized.length() > 255) {
            throw new BadRequestException("Image URL must not exceed 255 characters");
        }
        return normalized;
    }
}
