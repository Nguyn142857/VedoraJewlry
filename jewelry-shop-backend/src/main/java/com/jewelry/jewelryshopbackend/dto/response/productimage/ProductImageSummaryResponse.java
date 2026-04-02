package com.jewelry.jewelryshopbackend.dto.response.productimage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductImageSummaryResponse {
    private Long id;
    private String imageUrl;
    private Boolean isMain;
    private Integer sortOrder;
}
