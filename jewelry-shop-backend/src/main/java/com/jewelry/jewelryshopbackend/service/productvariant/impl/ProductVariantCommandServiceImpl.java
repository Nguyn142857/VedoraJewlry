package com.jewelry.jewelryshopbackend.service.productvariant.impl;

import com.jewelry.jewelryshopbackend.dto.request.productvariant.ProductVariantCreateRequest;
import com.jewelry.jewelryshopbackend.dto.request.productvariant.ProductVariantStatusUpdateRequest;
import com.jewelry.jewelryshopbackend.dto.request.productvariant.ProductVariantUpdateRequest;
import com.jewelry.jewelryshopbackend.dto.response.productvariant.ProductVariantResponse;
import com.jewelry.jewelryshopbackend.entity.Product;
import com.jewelry.jewelryshopbackend.entity.ProductVariant;
import com.jewelry.jewelryshopbackend.exception.BadRequestException;
import com.jewelry.jewelryshopbackend.mapper.ProductVariantMapper;
import com.jewelry.jewelryshopbackend.repository.ProductVariantRepository;
import com.jewelry.jewelryshopbackend.service.productvariant.ProductVariantCommandService;
import com.jewelry.jewelryshopbackend.validator.ProductValidator;
import com.jewelry.jewelryshopbackend.validator.ProductVariantValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductVariantCommandServiceImpl implements ProductVariantCommandService {

    private final ProductVariantRepository productVariantRepository;
    private final ProductVariantValidator productVariantValidator;
    private final ProductVariantMapper productVariantMapper;
    private final ProductValidator productValidator;

    @Override
    public ProductVariantResponse create(ProductVariantCreateRequest request) {
        Product product = productValidator.validateExistsAndActive(request.getProductId());
        String normalizedSku = normalizeSku(request.getSku());

        productVariantValidator.validateCreateDuplicateSku(normalizedSku);

        ProductVariant variant = ProductVariant.builder()
                .product(product)
                .sku(normalizedSku)
                .size(normalizeText(request.getSize()))
                .color(normalizeText(request.getColor()))
                .gemstone(normalizeText(request.getGemstone()))
                .price(request.getPrice())
                .stockQuantity(validateStockQuantity(request.getStockQuantity()))
                .status(request.getStatus() != null ? request.getStatus() : true)
                .deleted(false)
                .build();

        saveSafely(variant);
        return productVariantMapper.toResponse(variant);
    }

    @Override
    public ProductVariantResponse update(Long id, ProductVariantUpdateRequest request) {
        ProductVariant variant = productVariantValidator.validateExists(id);
        String normalizedSku = normalizeSku(request.getSku());

        productVariantValidator.validateUpdateDuplicateSku(variant, normalizedSku);

        variant.setSku(normalizedSku);
        variant.setSize(normalizeText(request.getSize()));
        variant.setColor(normalizeText(request.getColor()));
        variant.setGemstone(normalizeText(request.getGemstone()));
        variant.setPrice(request.getPrice());
        variant.setStockQuantity(validateStockQuantity(request.getStockQuantity()));
        variant.setStatus(request.getStatus() != null ? request.getStatus() : variant.getStatus());

        saveSafely(variant);
        return productVariantMapper.toResponse(variant);
    }

    @Override
    public ProductVariantResponse changeStatus(Long id, ProductVariantStatusUpdateRequest request) {
        ProductVariant variant = productVariantValidator.validateExists(id);
        variant.setStatus(request.getStatus());
        saveSafely(variant);
        return productVariantMapper.toResponse(variant);
    }

    @Override
    public void delete(Long id) {
        ProductVariant variant = productVariantValidator.validateExists(id);
        variant.setDeleted(true);
        variant.setStatus(false);
        saveSafely(variant);
    }

    private String normalizeSku(String sku) {
        String normalized = sku == null ? "" : sku.trim().toUpperCase(Locale.ROOT);
        if (normalized.isBlank()) {
            throw new BadRequestException("SKU cannot be blank");
        }
        if (normalized.length() > 100) {
            throw new BadRequestException("SKU must not exceed 100 characters");
        }
        return normalized;
    }

    private String normalizeText(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isBlank() ? null : normalized;
    }

    private int validateStockQuantity(Integer stockQuantity) {
        if (stockQuantity == null || stockQuantity < 0) {
            throw new BadRequestException("Stock quantity must be greater than or equal to 0");
        }
        return stockQuantity;
    }

    private void saveSafely(ProductVariant variant) {
        try {
            productVariantRepository.save(variant);
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Product variant data violates database constraints");
        }
    }
}
