package com.jewelry.jewelryshopbackend.specification;

import com.jewelry.jewelryshopbackend.entity.ProductImage;
import com.jewelry.jewelryshopbackend.enums.CategoryStatus;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.JoinType;

public final class ProductImageSpecifications {

    private ProductImageSpecifications() {
    }

    public static Specification<ProductImage> adminFilter(String q, Long productId, Boolean isMain) {
        return Specification
                .where(withProductAndCategoryFetch())
                .and(notDeleted())
                .and(keywordLike(q))
                .and(hasProductId(productId))
                .and(hasIsMain(isMain));
    }

    public static Specification<ProductImage> publicFilter(String productSlug, String q, Boolean isMain) {
        return Specification
                .where(withProductAndCategoryFetch())
                .and(notDeleted())
                .and(activeForPublic())
                .and(hasProductSlug(productSlug))
                .and(keywordLike(q))
                .and(hasIsMain(isMain));
    }

    private static Specification<ProductImage> withProductAndCategoryFetch() {
        return (root, query, cb) -> {
            if (query != null && query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("product", JoinType.LEFT).fetch("category", JoinType.LEFT);
                query.distinct(true);
            }
            return null;
        };
    }

    private static Specification<ProductImage> notDeleted() {
        return (root, query, cb) -> cb.isFalse(root.get("deleted"));
    }

    private static Specification<ProductImage> activeForPublic() {
        return (root, query, cb) -> cb.and(
                cb.isTrue(root.get("product").get("status")),
                cb.equal(root.get("product").get("category").get("status"), CategoryStatus.ACTIVE)
        );
    }

    private static Specification<ProductImage> keywordLike(String q) {
        return (root, query, cb) -> {
            if (q == null || q.isBlank()) {
                return null;
            }
            String keyword = "%" + q.trim().toLowerCase() + "%";
            return cb.like(cb.lower(root.get("imageUrl")), keyword);
        };
    }

    private static Specification<ProductImage> hasProductId(Long productId) {
        return (root, query, cb) -> productId == null ? null : cb.equal(root.get("product").get("id"), productId);
    }

    private static Specification<ProductImage> hasProductSlug(String productSlug) {
        return (root, query, cb) -> {
            if (productSlug == null || productSlug.isBlank()) {
                return null;
            }
            return cb.equal(cb.lower(root.get("product").get("slug")), productSlug.trim().toLowerCase());
        };
    }

    private static Specification<ProductImage> hasIsMain(Boolean isMain) {
        return (root, query, cb) -> isMain == null ? null : cb.equal(root.get("isMain"), isMain);
    }
}
