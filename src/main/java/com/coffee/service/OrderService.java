package com.coffee.service;

import com.coffee.constant.OrderStatus;
import com.coffee.constant.Role;
import com.coffee.dto.OrderDetailDto;
import com.coffee.dto.OrderDto;
import com.coffee.dto.OrderProductDto;
import com.coffee.entity.Member;
import com.coffee.entity.Order;
import com.coffee.entity.OrderProduct;
import com.coffee.entity.Product;
import com.coffee.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final MemberService memberService;
    private final ProductService productService;
    private final CartProductService cartProductService;
    private final OrderRepository orderRepository;

    @Transactional
    public Order createOrder(OrderDto dto) {
        Optional<Member> optionalMember = memberService.findMemberById(dto.getMemberId());
        if (!optionalMember.isPresent()) {
            throw new RuntimeException("회원이 존재하지 않습니다.");
        }
        Member member = optionalMember.get();

        Order order = new Order();
        order.setMember(member);
        order.setOrderdate(LocalDate.now());
        order.setOrderStatus(dto.getStatus());

        List<OrderProduct> orderProductList = new ArrayList<>();

        for (OrderProductDto item : dto.getOrderItems()) {
            Long productId = item.getProductId();
            System.out.println("상품 아이디 : " + productId);

            Optional<Product> optionalProduct = productService.findProductById(productId);

            if (!optionalProduct.isPresent()) {
                throw new RuntimeException("해당 상품이 존재하지 않습니다.");
            }

            Product product = optionalProduct.get();

            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("재고 수량이 부족합니다.");
            }

            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setOrder(order);
            orderProduct.setProduct(product);
            orderProduct.setQuantity(item.getQuantity());

            orderProductList.add(orderProduct);

            product.setStock(product.getStock() - item.getQuantity());

            Long cartProductId = item.getCartProductId();
            if (cartProductId != null) {
                cartProductService.deleteCartProductById(cartProductId);
            } else {
                System.out.println("상품 상세 보기 페이지에서 클릭하셨군요.");
            }
        }

        order.setOrderProducts(orderProductList);

        return orderRepository.save(order);
    }

    // 주문 내역 조회 : 관리자(모든 내역), 일반인(본인 것만)
    public List<OrderDetailDto> getOrderListByRole(Long memberId, Role role) {
        List<Order> orders;

        if (role == Role.ADMIN) {
            orders = orderRepository.findByOrderStatusOrderByIdDesc(OrderStatus.PENDING);
        } else {
            orders = orderRepository.findByMemberIdAndOrderStatusOrderByIdDesc(memberId, OrderStatus.PENDING);
        }
        return convertToOrderDetailDtoList(orders);
    }

    private List<OrderDetailDto> convertToOrderDetailDtoList(List<Order> orders) {
        List<OrderDetailDto> responseDtos = new ArrayList<>();

        for (Order order : orders) {
            // 주문의 기초 정보 셋팅
            OrderDetailDto dto = new OrderDetailDto();
            dto.setOrderId(order.getId());
            dto.setName(order.getMember().getName()); //
            dto.setOrderDate(order.getOrderdate());
            dto.setStatus(order.getOrderStatus().name());

            // `주문 상품` 여러 개에 대한 셋팅
            List<OrderDetailDto.OrderItem> orderItems = new ArrayList<>();
            for (OrderProduct op : order.getOrderProducts()) {
                OrderDetailDto.OrderItem item =
                        new OrderDetailDto.OrderItem(op.getProduct().getName(), op.getQuantity());
                orderItems.add(item);
            }

            dto.setOrderItems(orderItems);
            responseDtos.add(dto);
        }

        return responseDtos;

    }

    @Transactional
    public String updateOrderStatus(Long orderId, OrderStatus newStatus) {
        final String message = "해당 주문이 존재하지 않습니다. 주문 Id : " + orderId;
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException(message));

        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("취소된 주문은 상태를 변경할 수 없습니다.");
        }

        order.setOrderStatus(newStatus);

        return "송장 번호 " + orderId + "의 주문 상태가 " + newStatus + "(으)로 변경되었습니다.";
    }

    @Transactional
    public String cancelOrder(Long orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);

        if (orderOptional.isEmpty()) {
            throw new IllegalArgumentException("해당 주문이 존재하지 않습니다. Id : " + orderId);
        }

        Order order = orderOptional.get();

        for (OrderProduct op : order.getOrderProducts()) {
            Product product = op.getProduct();
            int quantity = op.getQuantity();

            product.setStock(product.getStock() + quantity);
            productService.save(product);
        }

        orderRepository.deleteById(orderId);

        return "주문이 취소 되었습니다.";
    }
}
