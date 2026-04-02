package com.jewelry.jewelryshopbackend.specification;

import com.jewelry.jewelryshopbackend.entity.Order;
import com.jewelry.jewelryshopbackend.enums.OrderStatus;
import com.jewelry.jewelryshopbackend.enums.PaymentStatus;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.JoinType;

public final class OrderSpecifications {

    private OrderSpecifications() {
    }

    public static Specification<Order> adminFilter(String q, Long userId, OrderStatus orderStatus, PaymentStatus paymentStatus) {
        return Specification
                .where(withUserFetch())
                .and(keywordLike(q))
                .and(hasUserId(userId))
                .and(hasOrderStatus(orderStatus))
                .and(hasPaymentStatus(paymentStatus));
    }

    private static Specification<Order> withUserFetch() {
        return (root, query, cb) -> {
            if (query != null && query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("user", JoinType.LEFT);
                query.distinct(true);
            }
            return null;
        };
    }

    private static Specification<Order> keywordLike(String q) {
        return (root, query, cb) -> {
            if (q == null || q.isBlank()) {
                return null;
            }
            String keyword = "%" + q.trim().toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("orderCode")), keyword),
                    cb.like(cb.lower(root.get("receiverName")), keyword),
                    cb.like(cb.lower(root.get("receiverPhone")), keyword),
                    cb.like(cb.lower(root.get("user").get("email")), keyword)
            );
        };
    }

    private static Specification<Order> hasUserId(Long userId) {
        return (root, query, cb) -> userId == null ? null : cb.equal(root.get("user").get("id"), userId);
    }

    private static Specification<Order> hasOrderStatus(OrderStatus status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("orderStatus"), status);
    }

    private static Specification<Order> hasPaymentStatus(PaymentStatus status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("paymentStatus"), status);
    }
}
