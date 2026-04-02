package com.jewelry.jewelryshopbackend.service.order.impl;

import com.jewelry.jewelryshopbackend.dto.response.order.OrderResponse;
import com.jewelry.jewelryshopbackend.dto.response.order.OrderSummaryResponse;
import com.jewelry.jewelryshopbackend.entity.Order;
import com.jewelry.jewelryshopbackend.entity.OrderItem;
import com.jewelry.jewelryshopbackend.payload.PageResponse;
import com.jewelry.jewelryshopbackend.exception.ResourceNotFoundException;
import com.jewelry.jewelryshopbackend.mapper.OrderMapper;
import com.jewelry.jewelryshopbackend.repository.OrderItemRepository;
import com.jewelry.jewelryshopbackend.repository.OrderRepository;
import com.jewelry.jewelryshopbackend.service.order.OrderQueryService;
import com.jewelry.jewelryshopbackend.service.user.CurrentUserService;
import com.jewelry.jewelryshopbackend.specification.OrderSpecifications;
import com.jewelry.jewelryshopbackend.util.PageableUtils;
import com.jewelry.jewelryshopbackend.validator.OrderValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryServiceImpl implements OrderQueryService {

    private final CurrentUserService currentUserService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final OrderValidator orderValidator;

    @Override
    public OrderResponse getMyOrderById(Long orderId) {
        Long userId = currentUserService.requireCurrentUser().getId();
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        List<OrderItem> items = orderItemRepository.findAllByOrderIdOrderByIdAsc(order.getId());
        return orderMapper.toOrderResponse(order, items);
    }

    @Override
    public PageResponse<OrderSummaryResponse> getMyOrders(int page, int size, String sortBy, String sortDir) {
        Long userId = currentUserService.requireCurrentUser().getId();
        Pageable pageable = PageableUtils.build(
                page,
                size,
                sortBy,
                sortDir,
                Set.of("id", "orderCode", "finalAmount", "paymentStatus", "orderStatus", "createdAt", "updatedAt"),
                "createdAt"
        );
        Page<Order> result = orderRepository.findAllByUserId(userId, pageable);
        return toSummaryPage(result);
    }

    @Override
    public OrderResponse getByIdForAdmin(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        List<OrderItem> items = orderItemRepository.findAllByOrderIdOrderByIdAsc(order.getId());
        return orderMapper.toOrderResponse(order, items);
    }

    @Override
    public PageResponse<OrderSummaryResponse> getAllForAdmin(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String q,
            Long userId,
            String orderStatus,
            String paymentStatus
    ) {
        Pageable pageable = PageableUtils.build(
                page,
                size,
                sortBy,
                sortDir,
                Set.of("id", "orderCode", "finalAmount", "paymentStatus", "orderStatus", "createdAt", "updatedAt"),
                "createdAt"
        );
        Specification<Order> spec = OrderSpecifications.adminFilter(
                q,
                userId,
                orderValidator.parseOrderStatus(orderStatus),
                orderValidator.parsePaymentStatus(paymentStatus)
        );
        Page<Order> result = orderRepository.findAll(spec, pageable);
        return toSummaryPage(result);
    }

    private PageResponse<OrderSummaryResponse> toSummaryPage(Page<Order> result) {
        return PageResponse.<OrderSummaryResponse>builder()
                .content(result.getContent().stream().map(orderMapper::toSummaryResponse).toList())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .last(result.isLast())
                .build();
    }
}
