package com.jewelry.jewelryshopbackend.service.order.impl;

import com.jewelry.jewelryshopbackend.dto.request.order.OrderCreateRequest;
import com.jewelry.jewelryshopbackend.dto.request.order.OrderStatusUpdateRequest;
import com.jewelry.jewelryshopbackend.dto.response.order.OrderResponse;
import com.jewelry.jewelryshopbackend.entity.Cart;
import com.jewelry.jewelryshopbackend.entity.CartItem;
import com.jewelry.jewelryshopbackend.entity.Order;
import com.jewelry.jewelryshopbackend.entity.OrderItem;
import com.jewelry.jewelryshopbackend.entity.ProductVariant;
import com.jewelry.jewelryshopbackend.entity.User;
import com.jewelry.jewelryshopbackend.enums.OrderStatus;
import com.jewelry.jewelryshopbackend.enums.PaymentStatus;
import com.jewelry.jewelryshopbackend.exception.BadRequestException;
import com.jewelry.jewelryshopbackend.exception.ResourceNotFoundException;
import com.jewelry.jewelryshopbackend.mapper.OrderMapper;
import com.jewelry.jewelryshopbackend.repository.CartItemRepository;
import com.jewelry.jewelryshopbackend.repository.OrderItemRepository;
import com.jewelry.jewelryshopbackend.repository.OrderRepository;
import com.jewelry.jewelryshopbackend.repository.ProductVariantRepository;
import com.jewelry.jewelryshopbackend.service.cart.CartDomainService;
import com.jewelry.jewelryshopbackend.service.order.OrderCommandService;
import com.jewelry.jewelryshopbackend.service.user.CurrentUserService;
import com.jewelry.jewelryshopbackend.util.OrderCodeUtils;
import com.jewelry.jewelryshopbackend.validator.OrderValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderCommandServiceImpl implements OrderCommandService {

    private static final BigDecimal STANDARD_SHIPPING_FEE = new BigDecimal("30000");
    private static final BigDecimal DEFAULT_DISCOUNT_AMOUNT = BigDecimal.ZERO;

    private final CurrentUserService currentUserService;
    private final CartDomainService cartDomainService;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final OrderValidator orderValidator;

    @Override
    public OrderResponse createFromCart(OrderCreateRequest request) {
        User user = currentUserService.requireCurrentUser();
        Cart cart = cartDomainService.getOrCreateCart(user);
        List<CartItem> cartItems = cartDomainService.getItems(cart.getId());
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            ProductVariant variant = productVariantRepository.findByIdAndStatusTrueAndDeletedFalseForUpdate(
                            cartItem.getProductVariant().getId()
                    )
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Active product variant not found with id: " + cartItem.getProductVariant().getId()
                    ));

            int quantity = cartItem.getQuantity();
            if (variant.getStockQuantity() < quantity) {
                throw new BadRequestException("Insufficient stock for SKU: " + variant.getSku());
            }

            BigDecimal unitPrice = variant.getPrice();
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
            totalAmount = totalAmount.add(subtotal);

            OrderItem item = OrderItem.builder()
                    .productVariant(variant)
                    .productName(variant.getProduct().getName())
                    .variantInfo(buildVariantInfo(variant))
                    .quantity(quantity)
                    .unitPrice(unitPrice)
                    .subtotal(subtotal)
                    .build();
            orderItems.add(item);

            variant.setStockQuantity(variant.getStockQuantity() - quantity);
        }

        BigDecimal shippingFee = calculateShippingFee(totalAmount);
        BigDecimal discountAmount = calculateDiscountAmount(totalAmount);
        orderValidator.validateAmount(shippingFee, discountAmount, totalAmount);

        Order order = Order.builder()
                .user(user)
                .orderCode(generateOrderCode())
                .totalAmount(totalAmount)
                .shippingFee(shippingFee)
                .discountAmount(discountAmount)
                .finalAmount(totalAmount.add(shippingFee).subtract(discountAmount))
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(PaymentStatus.UNPAID)
                .orderStatus(OrderStatus.PENDING)
                .receiverName(normalizeText(request.getReceiverName()))
                .receiverPhone(normalizeText(request.getReceiverPhone()))
                .receiverAddress(normalizeText(request.getReceiverAddress()))
                .note(normalizeOptionalText(request.getNote()))
                .build();
        orderRepository.save(order);

        for (OrderItem item : orderItems) {
            item.setOrder(order);
        }
        orderItemRepository.saveAll(orderItems);
        cartItemRepository.deleteByCartId(cart.getId());

        return orderMapper.toOrderResponse(order, orderItems);
    }

    @Override
    public OrderResponse updateStatus(Long orderId, OrderStatusUpdateRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        OrderStatus newOrderStatus = orderValidator.parseOrderStatus(request.getOrderStatus());
        PaymentStatus newPaymentStatus = orderValidator.parsePaymentStatus(request.getPaymentStatus());

        if (newOrderStatus == null && newPaymentStatus == null) {
            throw new BadRequestException("At least one status field must be provided");
        }

        orderValidator.validateStatusTransition(order, newOrderStatus);

        if (newOrderStatus != null) {
            order.setOrderStatus(newOrderStatus);
        }
        if (newPaymentStatus != null) {
            order.setPaymentStatus(newPaymentStatus);
        }
        orderRepository.save(order);

        List<OrderItem> orderItems = orderItemRepository.findAllByOrderIdOrderByIdAsc(order.getId());
        return orderMapper.toOrderResponse(order, orderItems);
    }

    private String buildVariantInfo(ProductVariant variant) {
        List<String> tokens = new ArrayList<>();
        if (variant.getSize() != null && !variant.getSize().isBlank()) {
            tokens.add("Size: " + variant.getSize());
        }
        if (variant.getColor() != null && !variant.getColor().isBlank()) {
            tokens.add("Color: " + variant.getColor());
        }
        if (variant.getGemstone() != null && !variant.getGemstone().isBlank()) {
            tokens.add("Gemstone: " + variant.getGemstone());
        }
        return String.join(", ", tokens);
    }

    private String generateOrderCode() {
        for (int i = 0; i < 5; i++) {
            String candidate = OrderCodeUtils.generate();
            if (orderRepository.findByOrderCodeIgnoreCase(candidate).isEmpty()) {
                return candidate;
            }
        }
        throw new BadRequestException("Unable to generate unique order code");
    }

    private String normalizeText(String value) {
        String normalized = value == null ? "" : value.trim().replaceAll("\\s+", " ");
        if (normalized.isBlank()) {
            throw new BadRequestException("Required text field cannot be blank");
        }
        return normalized;
    }

    private String normalizeOptionalText(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isBlank() ? null : normalized;
    }

    private BigDecimal calculateShippingFee(BigDecimal totalAmount) {
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return STANDARD_SHIPPING_FEE;
    }

    private BigDecimal calculateDiscountAmount(BigDecimal totalAmount) {
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return DEFAULT_DISCOUNT_AMOUNT;
    }
}
