package com.jewelry.jewelryshopbackend.dto.response.category;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CategoryResponse {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}