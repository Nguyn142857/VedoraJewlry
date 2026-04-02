package com.jewelry.jewelryshopbackend.repository;

import com.jewelry.jewelryshopbackend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    Optional<Order> findByOrderCode(String orderCode);

    Optional<Order> findByOrderCodeIgnoreCase(String orderCode);

    Optional<Order> findByIdAndUserId(Long id, Long userId);

    Page<Order> findAllByUserId(Long userId, Pageable pageable);
}
