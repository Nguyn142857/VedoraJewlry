package com.jewelry.jewelryshopbackend.dto.request.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryUpdateRequest {

    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters")
    private String name;

    @Size(max = 120, message = "Slug must not exceed 120 characters")
    @Pattern(
            regexp = "^[a-zA-Z0-9-]*$",
            message = "Slug can only contain letters, numbers and hyphens"
    )
    private String slug;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;
}