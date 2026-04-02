package com.jewelry.jewelryshopbackend.entity;

import com.jewelry.jewelryshopbackend.enums.CategoryStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "categories",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_category_name", columnNames = "name"),
                @UniqueConstraint(name = "uk_category_slug", columnNames = "slug")
        },
        indexes = {
                @Index(name = "idx_category_slug", columnList = "slug"),
                @Index(name = "idx_category_status", columnList = "status")
        }
)
public class Category extends BaseEntity {

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "slug", nullable = false, length = 120)
    private String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CategoryStatus status = CategoryStatus.ACTIVE;

    @Version
    @Column(name = "version")
    private Long version;

    public boolean isActive() {
        return this.status == CategoryStatus.ACTIVE;
    }

    public boolean isDeleted() {
        return this.status == CategoryStatus.DELETED;
    }

    public void markDeleted() {
        this.status = CategoryStatus.DELETED;
    }

    public void markInactive() {
        this.status = CategoryStatus.INACTIVE;
    }

    public void markActive() {
        this.status = CategoryStatus.ACTIVE;
    }
}
