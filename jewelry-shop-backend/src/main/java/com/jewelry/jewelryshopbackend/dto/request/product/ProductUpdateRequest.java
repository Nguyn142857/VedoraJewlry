package com.jewelry.jewelryshopbackend.dto.request.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductUpdateRequest {

    @NotBlank(message = "Product name is required")
    @Size(max = 150, message = "Product name must not exceed 150 characters")
    private String name;

    @Size(max = 180, message = "Slug must not exceed 180 characters")
    @Pattern(
            regexp = "^[a-zA-Z0-9-]*$",
            message = "Slug can only contain letters, numbers and hyphens"
    )
    private String slug;

    private String description;

    @Size(max = 100, message = "Material must not exceed 100 characters")
    private String material;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Base price must be greater than 0")
    private BigDecimal basePrice;

    @Size(max = 255, message = "Thumbnail must not exceed 255 characters")
    private String thumbnail;

    private Boolean status;

    @NotNull(message = "Category id is required")
    private Long categoryId;
}
