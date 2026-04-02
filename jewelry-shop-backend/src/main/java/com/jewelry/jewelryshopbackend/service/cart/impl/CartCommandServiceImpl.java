package com.jewelry.jewelryshopbackend.service.cart.impl;

import com.jewelry.jewelryshopbackend.dto.request.cart.CartItemUpdateRequest;
import com.jewelry.jewelryshopbackend.dto.request.cart.CartItemUpsertRequest;
import com.jewelry.jewelryshopbackend.dto.response.cart.CartResponse;
import com.jewelry.jewelryshopbackend.entity.Cart;
import com.jewelry.jewelryshopbackend.entity.CartItem;
import com.jewelry.jewelryshopbackend.entity.ProductVariant;
import com.jewelry.jewelryshopbackend.entity.User;
import com.jewelry.jewelryshopbackend.exception.ResourceNotFoundException;
import com.jewelry.jewelryshopbackend.mapper.CartMapper;
import com.jewelry.jewelryshopbackend.repository.CartItemRepository;
import com.jewelry.jewelryshopbackend.repository.ProductVariantRepository;
import com.jewelry.jewelryshopbackend.service.cart.CartCommandService;
import com.jewelry.jewelryshopbackend.service.cart.CartDomainService;
import com.jewelry.jewelryshopbackend.service.user.CurrentUserService;
import com.jewelry.jewelryshopbackend.validator.CartValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartCommandServiceImpl implements CartCommandService {

    private final CurrentUserService currentUserService;
    private final CartDomainService cartDomainService;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CartValidator cartValidator;
    private final CartMapper cartMapper;

    @Override
    public CartResponse addItem(CartItemUpsertRequest request) {
        User user = currentUserService.requireCurrentUser();
        Cart cart = cartDomainService.getOrCreateCart(user);

        ProductVariant variant = getValidVariant(request.getProductVariantId(), request.getQuantity());
        CartItem item = cartItemRepository.findByCartIdAndProductVariantId(cart.getId(), request.getProductVariantId())
                .orElseGet(() -> CartItem.builder()
                        .cart(cart)
                        .productVariant(variant)
                        .quantity(0)
                        .build());

        int newQuantity = item.getQuantity() + request.getQuantity();
        cartValidator.validateVariantCanAdd(variant, newQuantity);
        item.setQuantity(newQuantity);
        item.setUnitPrice(variant.getPrice());
        cartItemRepository.save(item);

        return buildResponse(cart);
    }

    @Override
    public CartResponse updateItem(Long itemId, CartItemUpdateRequest request) {
        User user = currentUserService.requireCurrentUser();
        Cart cart = cartDomainService.getOrCreateCart(user);

        CartItem item = cartItemRepository.findByIdAndCartId(itemId, cart.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + itemId));

        ProductVariant variant = getValidVariant(item.getProductVariant().getId(), request.getQuantity());
        item.setQuantity(request.getQuantity());
        item.setUnitPrice(variant.getPrice());
        cartItemRepository.save(item);

        return buildResponse(cart);
    }

    @Override
    public CartResponse removeItem(Long itemId) {
        User user = currentUserService.requireCurrentUser();
        Cart cart = cartDomainService.getOrCreateCart(user);

        CartItem item = cartItemRepository.findByIdAndCartId(itemId, cart.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + itemId));
        cartItemRepository.delete(item);

        return buildResponse(cart);
    }

    @Override
    public CartResponse clear() {
        User user = currentUserService.requireCurrentUser();
        Cart cart = cartDomainService.getOrCreateCart(user);
        cartItemRepository.deleteByCartId(cart.getId());
        return buildResponse(cart);
    }

    private ProductVariant getValidVariant(Long variantId, int quantity) {
        ProductVariant variant = productVariantRepository.findByIdAndStatusTrueAndDeletedFalse(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Active product variant not found with id: " + variantId));
        cartValidator.validateVariantCanAdd(variant, quantity);
        return variant;
    }

    private CartResponse buildResponse(Cart cart) {
        List<CartItem> items = cartDomainService.getItems(cart.getId());
        return cartMapper.toCartResponse(cart, items);
    }
}
