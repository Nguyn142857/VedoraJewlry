package com.jewelry.jewelryshopbackend.service.cart.impl;

import com.jewelry.jewelryshopbackend.dto.response.cart.CartResponse;
import com.jewelry.jewelryshopbackend.entity.Cart;
import com.jewelry.jewelryshopbackend.entity.CartItem;
import com.jewelry.jewelryshopbackend.entity.User;
import com.jewelry.jewelryshopbackend.mapper.CartMapper;
import com.jewelry.jewelryshopbackend.service.cart.CartDomainService;
import com.jewelry.jewelryshopbackend.service.cart.CartQueryService;
import com.jewelry.jewelryshopbackend.service.user.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartQueryServiceImpl implements CartQueryService {

    private final CurrentUserService currentUserService;
    private final CartDomainService cartDomainService;
    private final CartMapper cartMapper;

    @Override
    public CartResponse getMyCart() {
        User user = currentUserService.requireCurrentUser();
        Cart cart = cartDomainService.getOrCreateCart(user);
        List<CartItem> items = cartDomainService.getItems(cart.getId());
        return cartMapper.toCartResponse(cart, items);
    }
}
