package com.jewelry.jewelryshopbackend.repository;

import com.jewelry.jewelryshopbackend.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findAllByCartIdOrderByCreatedAtAsc(Long cartId);

    Optional<CartItem> findByCartIdAndProductVariantId(Long cartId, Long productVariantId);

    Optional<CartItem> findByIdAndCartId(Long id, Long cartId);

    void deleteByCartId(Long cartId);
}
