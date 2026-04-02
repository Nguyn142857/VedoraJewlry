package com.jewelry.jewelryshopbackend.dto.request.productimage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductImageUpdateRequest {

    @NotBlank(message = "Image URL is required")
    @Size(max = 255, message = "Image URL must not exceed 255 characters")
    private String imageUrl;

    private Boolean isMain;

    private Integer sortOrder;
}
