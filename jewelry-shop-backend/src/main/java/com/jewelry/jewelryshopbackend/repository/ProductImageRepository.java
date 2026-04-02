package com.jewelry.jewelryshopbackend.repository;

import com.jewelry.jewelryshopbackend.entity.ProductImage;
import com.jewelry.jewelryshopbackend.enums.CategoryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long>, JpaSpecificationExecutor<ProductImage> {

    boolean existsByProduct_IdAndIsMainTrueAndDeletedFalse(Long productId);

    @EntityGraph(attributePaths = {"product", "product.category"})
    Optional<ProductImage> findByIdAndDeletedFalse(Long id);

    @EntityGraph(attributePaths = {"product", "product.category"})
    Page<ProductImage> findAllByDeletedFalse(Pageable pageable);

    @EntityGraph(attributePaths = {"product", "product.category"})
    Page<ProductImage> findAllByDeletedFalseAndProduct_Id(Long productId, Pageable pageable);

    @EntityGraph(attributePaths = {"product", "product.category"})
    List<ProductImage> findAllByProduct_IdAndDeletedFalseOrderByIsMainDescSortOrderAscCreatedAtAsc(Long productId);

    @EntityGraph(attributePaths = {"product", "product.category"})
    List<ProductImage> findAllByProduct_SlugIgnoreCaseAndProduct_StatusTrueAndProduct_Category_StatusAndDeletedFalseOrderByIsMainDescSortOrderAscCreatedAtAsc(
            String productSlug,
            CategoryStatus categoryStatus
    );

    @EntityGraph(attributePaths = {"product", "product.category"})
    Optional<ProductImage> findByIdAndProduct_SlugIgnoreCaseAndProduct_StatusTrueAndProduct_Category_StatusAndDeletedFalse(
            Long id,
            String productSlug,
            CategoryStatus categoryStatus
    );
}
