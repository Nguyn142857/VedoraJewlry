package com.jewelry.jewelryshopbackend.dto.request.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryStatusUpdateRequest {

    @NotBlank(message = "Status is required")
    private String status;
}