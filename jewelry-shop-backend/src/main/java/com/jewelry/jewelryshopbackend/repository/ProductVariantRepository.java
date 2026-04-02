package com.jewelry.jewelryshopbackend.repository;

import com.jewelry.jewelryshopbackend.entity.ProductVariant;
import com.jewelry.jewelryshopbackend.enums.CategoryStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long>, JpaSpecificationExecutor<ProductVariant> {

    boolean existsBySkuIgnoreCase(String sku);

    boolean existsBySkuIgnoreCaseAndIdNot(String sku, Long id);

    @EntityGraph(attributePaths = {"product", "product.category"})
    Optional<ProductVariant> findByIdAndDeletedFalse(Long id);

    @EntityGraph(attributePaths = {"product", "product.category"})
    Optional<ProductVariant> findByIdAndStatusTrueAndDeletedFalse(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select pv
            from ProductVariant pv
            left join fetch pv.product p
            left join fetch p.category c
            where pv.id = :id
              and pv.status = true
              and pv.deleted = false
            """)
    Optional<ProductVariant> findByIdAndStatusTrueAndDeletedFalseForUpdate(@Param("id") Long id);

    @EntityGraph(attributePaths = {"product", "product.category"})
    Page<ProductVariant> findAllByDeletedFalse(Pageable pageable);

    @EntityGraph(attributePaths = {"product", "product.category"})
    Page<ProductVariant> findAllByDeletedFalseAndProduct_Id(Long productId, Pageable pageable);

    @EntityGraph(attributePaths = {"product", "product.category"})
    List<ProductVariant> findAllByProduct_SlugIgnoreCaseAndProduct_StatusTrueAndProduct_Category_StatusAndStatusTrueAndDeletedFalse(
            String productSlug,
            CategoryStatus categoryStatus
    );

    @EntityGraph(attributePaths = {"product", "product.category"})
    Optional<ProductVariant> findByIdAndProduct_SlugIgnoreCaseAndProduct_StatusTrueAndProduct_Category_StatusAndStatusTrueAndDeletedFalse(
            Long id,
            String productSlug,
            CategoryStatus categoryStatus
    );
}
