package com.jewelry.jewelryshopbackend.dto.response.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class AdminRevenuePointResponse {
    private String label;
    private BigDecimal revenue;
    private Long orders;
}