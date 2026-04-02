package com.jewelry.jewelryshopbackend.controller.public_api;

import com.jewelry.jewelryshopbackend.dto.request.order.OrderCreateRequest;
import com.jewelry.jewelryshopbackend.dto.response.order.OrderResponse;
import com.jewelry.jewelryshopbackend.dto.response.order.OrderSummaryResponse;
import com.jewelry.jewelryshopbackend.payload.ApiResponse;
import com.jewelry.jewelryshopbackend.payload.PageResponse;
import com.jewelry.jewelryshopbackend.service.order.OrderCommandService;
import com.jewelry.jewelryshopbackend.service.order.OrderQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/orders")
@RequiredArgsConstructor
public class UserOrderController {

    private final OrderCommandService orderCommandService;
    private final OrderQueryService orderQueryService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> create(@Valid @RequestBody OrderCreateRequest request) {
        OrderResponse response = orderCommandService.createFromCart(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Order created successfully"));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getById(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success(orderQueryService.getMyOrderById(orderId)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<OrderSummaryResponse>>> getMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        return ResponseEntity.ok(ApiResponse.success(orderQueryService.getMyOrders(page, size, sortBy, sortDir)));
    }
}
