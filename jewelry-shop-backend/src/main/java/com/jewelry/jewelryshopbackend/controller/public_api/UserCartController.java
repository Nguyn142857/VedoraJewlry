package com.jewelry.jewelryshopbackend.controller.public_api;

import com.jewelry.jewelryshopbackend.dto.request.cart.CartItemUpdateRequest;
import com.jewelry.jewelryshopbackend.dto.request.cart.CartItemUpsertRequest;
import com.jewelry.jewelryshopbackend.dto.response.cart.CartResponse;
import com.jewelry.jewelryshopbackend.payload.ApiResponse;
import com.jewelry.jewelryshopbackend.service.cart.CartCommandService;
import com.jewelry.jewelryshopbackend.service.cart.CartQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/cart")
@RequiredArgsConstructor
public class UserCartController {

    private final CartQueryService cartQueryService;
    private final CartCommandService cartCommandService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getMyCart() {
        return ResponseEntity.ok(ApiResponse.success(cartQueryService.getMyCart()));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> addItem(@Valid @RequestBody CartItemUpsertRequest request) {
        return ResponseEntity.ok(ApiResponse.success(cartCommandService.addItem(request), "Item added to cart"));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateItem(
            @PathVariable Long itemId,
            @Valid @RequestBody CartItemUpdateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(cartCommandService.updateItem(itemId, request), "Cart item updated"));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(ApiResponse.success(cartCommandService.removeItem(itemId), "Cart item removed"));
    }

    @DeleteMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> clearCart() {
        return ResponseEntity.ok(ApiResponse.success(cartCommandService.clear(), "Cart cleared"));
    }
}
