package com.jewelry.jewelryshopbackend.dto.request.productvariant;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductVariantStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private Boolean status;
}
