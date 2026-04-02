package com.jewelry.jewelryshopbackend.specification;

import com.jewelry.jewelryshopbackend.entity.ProductVariant;
import com.jewelry.jewelryshopbackend.enums.CategoryStatus;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.JoinType;
import java.math.BigDecimal;

public final class ProductVariantSpecifications {

    private ProductVariantSpecifications() {
    }

    public static Specification<ProductVariant> adminFilter(
            String q,
            Long productId,
            Boolean status,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Integer minStock,
            Integer maxStock
    ) {
        return Specification
                .where(withProductAndCategoryFetch())
                .and(notDeleted())
                .and(keywordLike(q))
                .and(hasProductId(productId))
                .and(hasStatus(status))
                .and(priceGte(minPrice))
                .and(priceLte(maxPrice))
                .and(stockGte(minStock))
                .and(stockLte(maxStock));
    }

    public static Specification<ProductVariant> publicFilter(
            String productSlug,
            String q,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Integer minStock
    ) {
        return Specification
                .where(withProductAndCategoryFetch())
                .and(notDeleted())
                .and(activeForPublic())
                .and(hasProductSlug(productSlug))
                .and(keywordLike(q))
                .and(priceGte(minPrice))
                .and(priceLte(maxPrice))
                .and(stockGte(minStock));
    }

    private static Specification<ProductVariant> notDeleted() {
        return (root, query, cb) -> cb.isFalse(root.get("deleted"));
    }

    private static Specification<ProductVariant> withProductAndCategoryFetch() {
        return (root, query, cb) -> {
            if (query != null && query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("product", JoinType.LEFT).fetch("category", JoinType.LEFT);
                query.distinct(true);
            }
            return null;
        };
    }

    private static Specification<ProductVariant> activeForPublic() {
        return (root, query, cb) -> cb.and(
                cb.isTrue(root.get("status")),
                cb.isTrue(root.get("product").get("status")),
                cb.equal(root.get("product").get("category").get("status"), CategoryStatus.ACTIVE)
        );
    }

    private static Specification<ProductVariant> keywordLike(String q) {
        return (root, query, cb) -> {
            if (q == null || q.isBlank()) {
                return null;
            }
            String keyword = "%" + q.trim().toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("sku")), keyword),
                    cb.like(cb.lower(cb.coalesce(root.get("size"), "")), keyword),
                    cb.like(cb.lower(cb.coalesce(root.get("color"), "")), keyword),
                    cb.like(cb.lower(cb.coalesce(root.get("gemstone"), "")), keyword)
            );
        };
    }

    private static Specification<ProductVariant> hasProductId(Long productId) {
        return (root, query, cb) -> productId == null ? null : cb.equal(root.get("product").get("id"), productId);
    }

    private static Specification<ProductVariant> hasProductSlug(String productSlug) {
        return (root, query, cb) -> {
            if (productSlug == null || productSlug.isBlank()) {
                return null;
            }
            return cb.equal(cb.lower(root.get("product").get("slug")), productSlug.trim().toLowerCase());
        };
    }

    private static Specification<ProductVariant> hasStatus(Boolean status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    private static Specification<ProductVariant> priceGte(BigDecimal minPrice) {
        return (root, query, cb) -> minPrice == null ? null : cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    private static Specification<ProductVariant> priceLte(BigDecimal maxPrice) {
        return (root, query, cb) -> maxPrice == null ? null : cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    private static Specification<ProductVariant> stockGte(Integer minStock) {
        return (root, query, cb) -> minStock == null ? null : cb.greaterThanOrEqualTo(root.get("stockQuantity"), minStock);
    }

    private static Specification<ProductVariant> stockLte(Integer maxStock) {
        return (root, query, cb) -> maxStock == null ? null : cb.lessThanOrEqualTo(root.get("stockQuantity"), maxStock);
    }
}
