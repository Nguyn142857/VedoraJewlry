package com.jewelry.jewelryshopbackend.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoogleLoginRequest {

    @NotBlank(message = "Google ID token is required")
    private String idToken;
}
