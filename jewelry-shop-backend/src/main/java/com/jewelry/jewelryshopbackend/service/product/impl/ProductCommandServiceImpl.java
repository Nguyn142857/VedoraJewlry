package com.jewelry.jewelryshopbackend.service.product.impl;

import com.jewelry.jewelryshopbackend.dto.request.product.ProductCreateRequest;
import com.jewelry.jewelryshopbackend.dto.request.product.ProductUpdateRequest;
import com.jewelry.jewelryshopbackend.dto.response.product.ProductResponse;
import com.jewelry.jewelryshopbackend.entity.Category;
import com.jewelry.jewelryshopbackend.entity.Product;
import com.jewelry.jewelryshopbackend.exception.BadRequestException;
import com.jewelry.jewelryshopbackend.mapper.ProductMapper;
import com.jewelry.jewelryshopbackend.repository.ProductRepository;
import com.jewelry.jewelryshopbackend.service.product.ProductCommandService;
import com.jewelry.jewelryshopbackend.util.SlugUtils;
import com.jewelry.jewelryshopbackend.validator.ProductValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductCommandServiceImpl implements ProductCommandService {

    private final ProductRepository productRepository;
    private final ProductValidator productValidator;
    private final ProductMapper productMapper;

    @Override
    public ProductResponse create(ProductCreateRequest request) {
        String normalizedName = normalizeName(request.getName());
        String normalizedSlug = resolveSlug(request.getSlug(), request.getName());
        String normalizedDescription = normalizeText(request.getDescription());
        String normalizedMaterial = normalizeText(request.getMaterial());
        String normalizedThumbnail = normalizeText(request.getThumbnail());

        productValidator.validateCreateDuplicateSlug(normalizedSlug);
        Category category = productValidator.validateActiveCategory(request.getCategoryId());

        Product product = Product.builder()
                .name(normalizedName)
                .slug(normalizedSlug)
                .description(normalizedDescription)
                .material(normalizedMaterial)
                .basePrice(request.getBasePrice())
                .thumbnail(normalizedThumbnail)
                .status(request.getStatus() != null ? request.getStatus() : true)
                .category(category)
                .build();

        Product saved = productRepository.save(product);
        return productMapper.toResponse(saved);
    }

    @Override
    public ProductResponse update(Long id, ProductUpdateRequest request) {
        Product product = productValidator.validateExists(id);

        String normalizedName = normalizeName(request.getName());
        String normalizedSlug = resolveSlug(request.getSlug(), request.getName());
        String normalizedDescription = normalizeText(request.getDescription());
        String normalizedMaterial = normalizeText(request.getMaterial());
        String normalizedThumbnail = normalizeText(request.getThumbnail());

        productValidator.validateUpdateDuplicateSlug(product, normalizedSlug);
        Category category = productValidator.validateActiveCategory(request.getCategoryId());

        product.setName(normalizedName);
        product.setSlug(normalizedSlug);
        product.setDescription(normalizedDescription);
        product.setMaterial(normalizedMaterial);
        product.setBasePrice(request.getBasePrice());
        product.setThumbnail(normalizedThumbnail);
        product.setStatus(request.getStatus() != null ? request.getStatus() : product.getStatus());
        product.setCategory(category);

        Product updated = productRepository.save(product);
        return productMapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        Product product = productValidator.validateExists(id);
        product.setStatus(false);
        productRepository.save(product);
    }

    private String normalizeName(String name) {
        String normalized = name == null ? "" : name.trim().replaceAll("\\s+", " ");
        if (normalized.isBlank()) {
            throw new BadRequestException("Product name cannot be blank");
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

    private String resolveSlug(String requestSlug, String fallbackName) {
        String raw = (requestSlug == null || requestSlug.isBlank())
                ? SlugUtils.toSlug(fallbackName)
                : SlugUtils.toSlug(requestSlug);

        if (raw.isBlank()) {
            throw new BadRequestException("Product slug cannot be blank");
        }

        if (raw.length() > 180) {
            throw new BadRequestException("Product slug must not exceed 180 characters");
        }

        return raw;
    }
}
