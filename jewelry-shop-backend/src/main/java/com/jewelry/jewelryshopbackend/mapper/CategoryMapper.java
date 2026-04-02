package com.jewelry.jewelryshopbackend.mapper;



import com.jewelry.jewelryshopbackend.dto.response.category.CategoryResponse;
import com.jewelry.jewelryshopbackend.dto.response.category.CategorySummaryResponse;
import com.jewelry.jewelryshopbackend.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryResponse toResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setSlug(category.getSlug());
        response.setDescription(category.getDescription());
        response.setStatus(category.getStatus().name());
        response.setCreatedAt(category.getCreatedAt());
        response.setUpdatedAt(category.getUpdatedAt());
        return response;
    }

    public CategorySummaryResponse toSummary(Category category) {
        CategorySummaryResponse response = new CategorySummaryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setSlug(category.getSlug());
        return response;
    }
}