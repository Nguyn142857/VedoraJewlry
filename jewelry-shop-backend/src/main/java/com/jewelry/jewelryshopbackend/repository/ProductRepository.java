package com.jewelry.jewelryshopbackend.repository;

import com.jewelry.jewelryshopbackend.enums.CategoryStatus;
import com.jewelry.jewelryshopbackend.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @EntityGraph(attributePaths = "category")
    Optional<Product> findBySlugIgnoreCaseAndStatusTrueAndCategory_Status(String slug, CategoryStatus categoryStatus);

    boolean existsBySlugIgnoreCase(String slug);

    boolean existsBySlugIgnoreCaseAndIdNot(String slug, Long id);

    boolean existsByCategoryIdAndStatusTrue(Long categoryId);

    @EntityGraph(attributePaths = "category")
    Optional<Product> findByIdAndStatusTrue(Long id);

    @EntityGraph(attributePaths = "category")
    Page<Product> findAllByStatusTrueAndCategory_Status(Pageable pageable, CategoryStatus categoryStatus);

    @Override
    @EntityGraph(attributePaths = "category")
    Page<Product> findAll(Pageable pageable);
}
