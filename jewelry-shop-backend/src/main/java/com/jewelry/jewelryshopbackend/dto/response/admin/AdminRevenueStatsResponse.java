package com.jewelry.jewelryshopbackend.dto.response.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class AdminRevenueStatsResponse {
    private int days;
    private BigDecimal totalRevenue;
    private Long totalPaidOrders;
    private BigDecimal averageOrderValue;
    private List<AdminRevenuePointResponse> chart;
}