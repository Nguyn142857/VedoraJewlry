package com.jewelry.jewelryshopbackend.dto.request.productvariant;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductVariantUpdateRequest {

    @NotBlank(message = "SKU is required")
    @Size(max = 100, message = "SKU must not exceed 100 characters")
    @Pattern(
            regexp = "^[a-zA-Z0-9_-]+$",
            message = "SKU can only contain letters, numbers, underscore and hyphen"
    )
    private String sku;

    @Size(max = 50, message = "Size must not exceed 50 characters")
    private String size;

    @Size(max = 50, message = "Color must not exceed 50 characters")
    private String color;

    @Size(max = 100, message = "Gemstone must not exceed 100 characters")
    private String gemstone;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Stock quantity is required")
    private Integer stockQuantity;

    private Boolean status;
}
