package com.bookstore.orderservice.service;

import com.bookstore.orderservice.client.BookDto;
import com.bookstore.orderservice.client.BookServiceClient;
import com.bookstore.orderservice.dto.OrderDto;
import com.bookstore.orderservice.dto.OrderItemDto;
import com.bookstore.orderservice.model.Order;
import com.bookstore.orderservice.model.OrderItem;
import com.bookstore.orderservice.exception.OrderNotFoundException;
import com.bookstore.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final BookServiceClient bookServiceClient;

    @Transactional
    public OrderDto createOrder(OrderDto orderDto, String username, String token) {
        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .username(username)
                .orderDate(LocalDateTime.now())
                .status("PENDING")
                .shippingAddress(orderDto.getShippingAddress())
                .paymentMethod(orderDto.getPaymentMethod())
                .paymentStatus("PENDING")
                .build();

        List<OrderItem> orderItems = orderDto.getOrderItems().stream()
                .map(itemDto -> {
                    BookDto book = bookServiceClient.getBook(itemDto.getBookId(), token);
                    return OrderItem.builder()
                            .order(order)
                            .bookId(book.getId())
                            .bookTitle(book.getTitle())
                            .quantity(itemDto.getQuantity())
                            .price(book.getPrice())
                            .subtotal(book.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())))
                            .build();
                })
                .collect(Collectors.toList());

        order.setOrderItems(orderItems);
        order.setTotalAmount(calculateTotalAmount(orderItems));

        Order savedOrder = orderRepository.save(order);
        return convertToDto(savedOrder);
    }

    public OrderDto getOrder(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber);
        if (order == null) {
            throw new OrderNotFoundException("Order not found with order number: " + orderNumber);
        }
        return convertToDto(order);
    }

    public List<OrderDto> getUserOrders(String username) {
        return orderRepository.findByUsername(username).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderDto updateOrderStatus(String orderNumber, String status) {
        Order order = orderRepository.findByOrderNumber(orderNumber);
        if (order == null) {
            throw new OrderNotFoundException("Order not found with order number: " + orderNumber);
        }
        order.setStatus(status);
        return convertToDto(orderRepository.save(order));
    }

    private String generateOrderNumber() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private BigDecimal calculateTotalAmount(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private OrderDto convertToDto(Order order) {
        List<OrderItemDto> orderItemDtos = order.getOrderItems().stream()
                .map(item -> OrderItemDto.builder()
                        .bookId(item.getBookId())
                        .bookTitle(item.getBookTitle())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .subtotal(item.getSubtotal())
                        .build())
                .collect(Collectors.toList());

        return OrderDto.builder()
                .orderNumber(order.getOrderNumber())
                .username(order.getUsername())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .orderItems(orderItemDtos)
                .shippingAddress(order.getShippingAddress())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .build();
    }
}
