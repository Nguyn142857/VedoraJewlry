package com.jewelry.jewelryshopbackend.service.user.impl;

import com.jewelry.jewelryshopbackend.dto.response.admin.AdminAccountResponse;
import com.jewelry.jewelryshopbackend.dto.response.user.UserProfileResponse;
import com.jewelry.jewelryshopbackend.entity.Role;
import com.jewelry.jewelryshopbackend.entity.User;
import com.jewelry.jewelryshopbackend.entity.UserRole;
import com.jewelry.jewelryshopbackend.enums.UserStatus;
import com.jewelry.jewelryshopbackend.exception.ResourceNotFoundException;
import com.jewelry.jewelryshopbackend.mapper.UserMapper;
import com.jewelry.jewelryshopbackend.payload.PageResponse;
import com.jewelry.jewelryshopbackend.repository.UserRepository;
import com.jewelry.jewelryshopbackend.service.user.CurrentUserService;
import com.jewelry.jewelryshopbackend.service.user.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.jewelry.jewelryshopbackend.dto.response.admin.AdminRevenuePointResponse;
import com.jewelry.jewelryshopbackend.dto.response.admin.AdminRevenueStatsResponse;
import com.jewelry.jewelryshopbackend.entity.Order;
import com.jewelry.jewelryshopbackend.enums.PaymentStatus;
import com.jewelry.jewelryshopbackend.repository.OrderRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryServiceImpl implements UserQueryService {

    private final CurrentUserService currentUserService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Override
    public UserProfileResponse getCurrentUserProfile() {
        User user = currentUserService.requireCurrentUser();
        return userMapper.toProfileResponse(user);
    }

    @Override
    public String getAdminDashboardMessage() {
        return "Only ADMIN can access this endpoint";
    }

    @Override
    public PageResponse<AdminAccountResponse> getAllForAdmin(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String q,
            String status
    ) {
        int safePage = Math.max(page, 0);
        int safeSize = size <= 0 ? 10 : Math.min(size, 200);

        List<User> users = new ArrayList<>(userRepository.findAll());

        if (StringUtils.hasText(q)) {
            String keyword = q.trim().toLowerCase();
            users = users.stream()
                    .filter(user -> containsIgnoreCase(user.getFullName(), keyword)
                            || containsIgnoreCase(user.getEmail(), keyword)
                            || containsIgnoreCase(user.getPhone(), keyword)
                            || containsIgnoreCase(user.getAddress(), keyword))
                    .toList();
        }

        if (StringUtils.hasText(status)) {
            Boolean active = parseBooleanStatus(status);
            if (active != null) {
                users = users.stream()
                        .filter(user -> active
                                ? user.getStatus() == UserStatus.ACTIVE
                                : user.getStatus() != UserStatus.ACTIVE)
                        .toList();
            }
        }

        Comparator<User> comparator = buildComparator(sortBy);
        if (!"asc".equalsIgnoreCase(sortDir)) {
            comparator = comparator.reversed();
        }
        users = users.stream().sorted(comparator).toList();

        int totalElements = users.size();
        int fromIndex = Math.min(safePage * safeSize, totalElements);
        int toIndex = Math.min(fromIndex + safeSize, totalElements);

        List<AdminAccountResponse> content = users.subList(fromIndex, toIndex).stream()
                .map(this::toAdminAccountResponse)
                .toList();

        int totalPages = (int) Math.ceil((double) totalElements / safeSize);

        return PageResponse.<AdminAccountResponse>builder()
                .content(content)
                .page(safePage)
                .size(safeSize)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .last(toIndex >= totalElements)
                .build();
    }

    @Override
    public AdminAccountResponse getByIdForAdmin(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        return toAdminAccountResponse(user);
    }

    private AdminAccountResponse toAdminAccountResponse(User user) {
        List<String> roles = user.getUserRoles().stream()
                .map(UserRole::getRole)
                .filter(role -> role != null && role.getName() != null)
                .map(Role::getName)
                .distinct()
                .sorted()
                .toList();

        return AdminAccountResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .status(user.getStatus() == UserStatus.ACTIVE)
                .roles(roles)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private Comparator<User> buildComparator(String sortBy) {
        String normalized = sortBy == null ? "createdAt" : sortBy.trim();
        return switch (normalized) {
            case "fullName" -> Comparator.comparing(
                    user -> safeString(user.getFullName()),
                    String.CASE_INSENSITIVE_ORDER
            );
            case "email" -> Comparator.comparing(
                    user -> safeString(user.getEmail()),
                    String.CASE_INSENSITIVE_ORDER
            );
            case "updatedAt" -> Comparator.comparing(
                    User::getUpdatedAt,
                    Comparator.nullsLast(Comparator.naturalOrder())
            );
            case "status" -> Comparator.comparing(user -> user.getStatus() == UserStatus.ACTIVE);
            default -> Comparator.comparing(
                    User::getCreatedAt,
                    Comparator.nullsLast(Comparator.naturalOrder())
            );
        };
    }

    private String safeString(String value) {
        return value == null ? "" : value;
    }

    private boolean containsIgnoreCase(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
    }

    private Boolean parseBooleanStatus(String status) {
        String normalized = status.trim().toLowerCase();
        if ("true".equals(normalized) || "active".equals(normalized)) {
            return true;
        }
        if ("false".equals(normalized)
                || "inactive".equals(normalized)
                || "blocked".equals(normalized)) {
            return false;
        }
        return null;
    }
    @Override
    public AdminRevenueStatsResponse getRevenueStats(int days) {
        int safeDays = Math.max(1, Math.min(days, 365));

        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(safeDays - 1L);
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        var paidOrders = orderRepository.findAllByPaymentStatusAndCreatedAtBetweenOrderByCreatedAtAsc(
                PaymentStatus.PAID,
                start,
                end
        );

        Map<LocalDate, RevenueBucket> buckets = new LinkedHashMap<>();
        for (int i = 0; i < safeDays; i++) {
            LocalDate date = startDate.plusDays(i);
            buckets.put(date, new RevenueBucket());
        }

        BigDecimal totalRevenue = BigDecimal.ZERO;
        long totalPaidOrders = 0L;

        for (Order order : paidOrders) {
            LocalDate orderDate = order.getCreatedAt().toLocalDate();
            RevenueBucket bucket = buckets.get(orderDate);
            if (bucket == null) {
                continue;
            }

            BigDecimal revenue = order.getFinalAmount() == null
                    ? BigDecimal.ZERO
                    : order.getFinalAmount();

            bucket.revenue = bucket.revenue.add(revenue);
            bucket.orders++;

            totalRevenue = totalRevenue.add(revenue);
            totalPaidOrders++;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

        var chart = buckets.entrySet().stream()
                .map(entry -> AdminRevenuePointResponse.builder()
                        .label(entry.getKey().format(formatter))
                        .revenue(entry.getValue().revenue)
                        .orders(entry.getValue().orders)
                        .build())
                .toList();

        BigDecimal averageOrderValue = totalPaidOrders > 0
                ? totalRevenue.divide(BigDecimal.valueOf(totalPaidOrders), 0, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return AdminRevenueStatsResponse.builder()
                .days(safeDays)
                .totalRevenue(totalRevenue)
                .totalPaidOrders(totalPaidOrders)
                .averageOrderValue(averageOrderValue)
                .chart(chart)
                .build();
    }

    private static class RevenueBucket {
        private BigDecimal revenue = BigDecimal.ZERO;
        private long orders = 0L;
    }
}