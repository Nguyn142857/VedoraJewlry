package com.jewelry.jewelryshopbackend.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileUpdateRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    @Pattern(
            regexp = "^[0-9+()\\-\\s]*$",
            message = "Phone contains invalid characters"
    )
    private String phone;

    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;
}
