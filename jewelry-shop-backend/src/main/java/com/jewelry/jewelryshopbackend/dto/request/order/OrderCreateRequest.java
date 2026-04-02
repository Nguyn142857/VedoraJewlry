package com.jewelry.jewelryshopbackend.dto.request.order;

import com.jewelry.jewelryshopbackend.enums.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderCreateRequest {

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    @NotBlank(message = "Receiver name is required")
    @Size(max = 100, message = "Receiver name must not exceed 100 characters")
    private String receiverName;

    @NotBlank(message = "Receiver phone is required")
    @Size(max = 20, message = "Receiver phone must not exceed 20 characters")
    private String receiverPhone;

    @NotBlank(message = "Receiver address is required")
    @Size(max = 255, message = "Receiver address must not exceed 255 characters")
    private String receiverAddress;

    @Size(max = 255, message = "Note must not exceed 255 characters")
    private String note;

    @DecimalMin(value = "0.0", inclusive = true, message = "Shipping fee must be greater than or equal to 0")
    private BigDecimal shippingFee;

    @DecimalMin(value = "0.0", inclusive = true, message = "Discount amount must be greater than or equal to 0")
    private BigDecimal discountAmount;
}
