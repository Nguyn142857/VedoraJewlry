package com.jewelry.jewelryshopbackend.service.cart;

import com.jewelry.jewelryshopbackend.entity.Cart;
import com.jewelry.jewelryshopbackend.entity.CartItem;
import com.jewelry.jewelryshopbackend.entity.User;

import java.util.List;

public interface CartDomainService {

    Cart getOrCreateCart(User user);

    List<CartItem> getItems(Long cartId);
}
