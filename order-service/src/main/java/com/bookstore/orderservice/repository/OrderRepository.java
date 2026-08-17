package com.bookstore.orderservice.repository;

import com.bookstore.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUsername(String username);

    Order findByOrderNumber(String orderNumber);
}
