package com.bookstore.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private String orderNumber;
    private String username;
    private LocalDateTime orderDate;
    private String status;
    private BigDecimal totalAmount;

    @NotEmpty(message = "At least one order item is required")
    private List<OrderItemDto> orderItems;

    @NotEmpty(message = "Shipping address is required")
    private String shippingAddress;

    @NotEmpty(message = "Payment method is required")
    private String paymentMethod;
    private String paymentStatus;
}
