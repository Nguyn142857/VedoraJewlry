package com.jewelry.jewelryshopbackend.specification;

import com.jewelry.jewelryshopbackend.entity.Category;
import com.jewelry.jewelryshopbackend.enums.CategoryStatus;
import org.springframework.data.jpa.domain.Specification;

public final class CategorySpecifications {

    private CategorySpecifications() {
    }

    public static Specification<Category> adminFilter(String q, CategoryStatus status) {
        return Specification
                .where(statusNot(CategoryStatus.DELETED))
                .and(keywordLike(q))
                .and(hasStatus(status));
    }

    public static Specification<Category> publicFilter(String q) {
        return Specification
                .where(hasStatus(CategoryStatus.ACTIVE))
                .and(keywordLike(q));
    }

    private static Specification<Category> keywordLike(String q) {
        return (root, query, cb) -> {
            if (q == null || q.isBlank()) {
                return null;
            }
            String keyword = "%" + q.trim().toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), keyword),
                    cb.like(cb.lower(root.get("slug")), keyword),
                    cb.like(cb.lower(cb.coalesce(root.get("description"), "")), keyword)
            );
        };
    }

    private static Specification<Category> hasStatus(CategoryStatus status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    private static Specification<Category> statusNot(CategoryStatus status) {
        return (root, query, cb) -> cb.notEqual(root.get("status"), status);
    }
}
