package com.coffee.repository;

import com.coffee.constant.OrderStatus;
import com.coffee.entity.Order;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByMemberIdAndOrderStatusOrderByIdDesc(Long memberId, OrderStatus status);

    List<Order> findByOrderStatusOrderByIdDesc(OrderStatus status);


    @Modifying // 이 쿼리는 select 구문이 아니고, 데이터 변경을 위한 쿼리입니다.
    @Transactional // import jakarta.transaction.Transactional;
    @Query("update Order o set o.orderStatus = :status where o.id = :orderId")
    int updateOrderStatus(@Param("orderId") Long orderId, @Param("status") OrderStatus status);
}
