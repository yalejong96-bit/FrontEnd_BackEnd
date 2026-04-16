package com.coffee.dto;

import com.coffee.constant.OrderStatus;
import com.coffee.entity.OrderProduct;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter @Setter @ToString
public class OrderDto {
    private Long memberId; // 주문자 정보
    private OrderStatus status;
    private List<OrderProductDto> orderItems; // 주문 상품 목록
}
