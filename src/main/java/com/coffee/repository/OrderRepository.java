package com.coffee.repository;

import com.coffee.constant.OrderStatus;
import com.coffee.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByMemberIdAndOrderStatusOrderByIdDesc(Long memberId, OrderStatus status);

    List<Order> findByOrderStatusOrderByIdDesc(OrderStatus status);

}
