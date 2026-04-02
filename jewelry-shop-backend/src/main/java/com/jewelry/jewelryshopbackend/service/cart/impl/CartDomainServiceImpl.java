package com.jewelry.jewelryshopbackend.service.cart.impl;

import com.jewelry.jewelryshopbackend.entity.Cart;
import com.jewelry.jewelryshopbackend.entity.CartItem;
import com.jewelry.jewelryshopbackend.entity.User;
import com.jewelry.jewelryshopbackend.repository.CartItemRepository;
import com.jewelry.jewelryshopbackend.repository.CartRepository;
import com.jewelry.jewelryshopbackend.service.cart.CartDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartDomainServiceImpl implements CartDomainService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    @Transactional
    public Cart getOrCreateCart(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> cartRepository.save(
                        Cart.builder()
                                .user(user)
                                .build()
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartItem> getItems(Long cartId) {
        return cartItemRepository.findAllByCartIdOrderByCreatedAtAsc(cartId);
    }
}
