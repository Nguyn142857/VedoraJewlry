package com.jewelry.jewelryshopbackend.repository;

import com.jewelry.jewelryshopbackend.entity.Category;
import com.jewelry.jewelryshopbackend.enums.CategoryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsBySlugIgnoreCase(String slug);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    boolean existsBySlugIgnoreCaseAndIdNot(String slug, Long id);

    Optional<Category> findByIdAndStatusNot(Long id, CategoryStatus status);

    Optional<Category> findByIdAndStatus(Long id, CategoryStatus status);

    Optional<Category> findBySlugIgnoreCaseAndStatus(String slug, CategoryStatus status);

    List<Category> findAllByStatus(CategoryStatus status);

    Page<Category> findAllByStatusNot(CategoryStatus status, Pageable pageable);
}
