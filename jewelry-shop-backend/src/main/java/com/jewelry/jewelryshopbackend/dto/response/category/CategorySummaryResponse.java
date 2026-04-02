package com.jewelry.jewelryshopbackend.dto.response.category;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategorySummaryResponse {
    private Long id;
    private String name;
    private String slug;
}