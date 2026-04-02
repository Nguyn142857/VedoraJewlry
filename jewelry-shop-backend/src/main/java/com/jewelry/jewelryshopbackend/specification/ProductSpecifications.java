package com.jewelry.jewelryshopbackend.specification;

import com.jewelry.jewelryshopbackend.entity.Product;
import com.jewelry.jewelryshopbackend.enums.CategoryStatus;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.JoinType;
import java.math.BigDecimal;

public final class ProductSpecifications {

    private ProductSpecifications() {
    }

    public static Specification<Product> adminFilter(String q, Long categoryId, Boolean status) {
        return Specification
                .where(withCategoryFetch())
                .and(keywordLike(q))
                .and(hasCategoryId(categoryId))
                .and(hasStatus(status));
    }

    public static Specification<Product> publicFilter(String q, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice) {
        return Specification
                .where(withCategoryFetch())
                .and(isActiveForPublic())
                .and(keywordLike(q))
                .and(hasCategoryId(categoryId))
                .and(basePriceGte(minPrice))
                .and(basePriceLte(maxPrice));
    }

    private static Specification<Product> withCategoryFetch() {
        return (root, query, cb) -> {
            if (query != null && query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("category", JoinType.LEFT);
                query.distinct(true);
            }
            return null;
        };
    }

    private static Specification<Product> isActiveForPublic() {
        return (root, query, cb) -> cb.and(
                cb.isTrue(root.get("status")),
                cb.equal(root.get("category").get("status"), CategoryStatus.ACTIVE)
        );
    }

    private static Specification<Product> keywordLike(String q) {
        return (root, query, cb) -> {
            if (q == null || q.isBlank()) {
                return null;
            }
            String keyword = "%" + q.trim().toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), keyword),
                    cb.like(cb.lower(root.get("slug")), keyword),
                    cb.like(cb.lower(cb.coalesce(root.get("description"), "")), keyword),
                    cb.like(cb.lower(cb.coalesce(root.get("material"), "")), keyword)
            );
        };
    }

    private static Specification<Product> hasCategoryId(Long categoryId) {
        return (root, query, cb) -> categoryId == null ? null : cb.equal(root.get("category").get("id"), categoryId);
    }

    private static Specification<Product> hasStatus(Boolean status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    private static Specification<Product> basePriceGte(BigDecimal minPrice) {
        return (root, query, cb) -> minPrice == null ? null : cb.greaterThanOrEqualTo(root.get("basePrice"), minPrice);
    }

    private static Specification<Product> basePriceLte(BigDecimal maxPrice) {
        return (root, query, cb) -> maxPrice == null ? null : cb.lessThanOrEqualTo(root.get("basePrice"), maxPrice);
    }
}
