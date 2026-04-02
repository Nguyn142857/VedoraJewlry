package com.jewelry.jewelryshopbackend.service.cart;

import com.jewelry.jewelryshopbackend.dto.request.cart.CartItemUpdateRequest;
import com.jewelry.jewelryshopbackend.dto.request.cart.CartItemUpsertRequest;
import com.jewelry.jewelryshopbackend.dto.response.cart.CartResponse;

public interface CartCommandService {

    CartResponse addItem(CartItemUpsertRequest request);

    CartResponse updateItem(Long itemId, CartItemUpdateRequest request);

    CartResponse removeItem(Long itemId);

    CartResponse clear();
}
