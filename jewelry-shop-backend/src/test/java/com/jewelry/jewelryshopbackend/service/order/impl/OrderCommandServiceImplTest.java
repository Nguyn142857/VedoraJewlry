package com.jewelry.jewelryshopbackend.service.order.impl;

import com.jewelry.jewelryshopbackend.dto.request.order.OrderCreateRequest;
import com.jewelry.jewelryshopbackend.dto.request.order.OrderStatusUpdateRequest;
import com.jewelry.jewelryshopbackend.dto.response.order.OrderResponse;
import com.jewelry.jewelryshopbackend.entity.Cart;
import com.jewelry.jewelryshopbackend.entity.CartItem;
import com.jewelry.jewelryshopbackend.entity.Order;
import com.jewelry.jewelryshopbackend.entity.Product;
import com.jewelry.jewelryshopbackend.entity.ProductVariant;
import com.jewelry.jewelryshopbackend.entity.User;
import com.jewelry.jewelryshopbackend.enums.OrderStatus;
import com.jewelry.jewelryshopbackend.enums.PaymentMethod;
import com.jewelry.jewelryshopbackend.enums.UserStatus;
import com.jewelry.jewelryshopbackend.exception.BadRequestException;
import com.jewelry.jewelryshopbackend.mapper.OrderMapper;
import com.jewelry.jewelryshopbackend.repository.CartItemRepository;
import com.jewelry.jewelryshopbackend.repository.OrderItemRepository;
import com.jewelry.jewelryshopbackend.repository.OrderRepository;
import com.jewelry.jewelryshopbackend.repository.ProductVariantRepository;
import com.jewelry.jewelryshopbackend.service.cart.CartDomainService;
import com.jewelry.jewelryshopbackend.service.user.CurrentUserService;
import com.jewelry.jewelryshopbackend.validator.OrderValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderCommandServiceImplTest {

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private CartDomainService cartDomainService;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductVariantRepository productVariantRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderValidator orderValidator;

    @InjectMocks
    private OrderCommandServiceImpl orderCommandService;

    @Test
    void createFromCart_shouldCreateOrderAndReduceStock() {
        User user = User.builder().email("u@example.com").status(UserStatus.ACTIVE).build();
        user.setId(1L);
        Cart cart = Cart.builder().user(user).build();
        cart.setId(10L);

        ProductVariant variant = buildVariant();
        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .productVariant(variant)
                .quantity(2)
                .unitPrice(BigDecimal.valueOf(100))
                .build();

        OrderCreateRequest request = new OrderCreateRequest();
        request.setPaymentMethod(PaymentMethod.COD);
        request.setReceiverName("Receiver");
        request.setReceiverPhone("0123");
        request.setReceiverAddress("Address");

        when(currentUserService.requireCurrentUser()).thenReturn(user);
        when(cartDomainService.getOrCreateCart(user)).thenReturn(cart);
        when(cartDomainService.getItems(cart.getId())).thenReturn(List.of(cartItem));
        when(productVariantRepository.findByIdAndStatusTrueAndDeletedFalseForUpdate(variant.getId()))
                .thenReturn(Optional.of(variant));
        when(orderRepository.findByOrderCodeIgnoreCase(any())).thenReturn(Optional.empty());
        when(orderMapper.toOrderResponse(any(), any())).thenReturn(OrderResponse.builder().orderCode("ok").build());

        OrderResponse response = orderCommandService.createFromCart(request);

        verify(orderRepository).save(any(Order.class));
        verify(orderItemRepository).saveAll(any());
        verify(cartItemRepository).deleteByCartId(cart.getId());
        assertEquals(3, variant.getStockQuantity());
        assertEquals("ok", response.getOrderCode());
    }

    @Test
    void updateStatus_shouldThrowWhenNoStatusProvided() {
        Order order = Order.builder().orderStatus(OrderStatus.PENDING).build();
        order.setId(1L);
        OrderStatusUpdateRequest request = new OrderStatusUpdateRequest();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderValidator.parseOrderStatus(null)).thenReturn(null);
        when(orderValidator.parsePaymentStatus(null)).thenReturn(null);

        assertThrows(BadRequestException.class, () -> orderCommandService.updateStatus(1L, request));
    }

    private ProductVariant buildVariant() {
        Product product = Product.builder()
                .name("Ring")
                .slug("ring")
                .basePrice(BigDecimal.valueOf(100))
                .status(true)
                .build();
        ProductVariant variant = ProductVariant.builder()
                .product(product)
                .sku("SKU-1")
                .price(BigDecimal.valueOf(100))
                .stockQuantity(5)
                .status(true)
                .deleted(false)
                .build();
        variant.setId(5L);
        return variant;
    }
}
