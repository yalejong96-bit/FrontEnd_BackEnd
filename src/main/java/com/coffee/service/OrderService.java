package com.coffee.service;

import com.coffee.dto.OrderDto;
import com.coffee.dto.OrderProductDto;
import com.coffee.entity.Member;
import com.coffee.entity.Order;
import com.coffee.entity.OrderProduct;
import com.coffee.entity.Product;
import com.coffee.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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


    public Order createOrder(OrderDto dto){
        Optional<Member> optionalMember = memberService.findMemberById(dto.getMemberId());
        if(!optionalMember.isPresent()){
            throw new RuntimeException("회원이 존재하지 않습니다.");
        }
        Member member = optionalMember.get();

        Order order = new Order();
        order.setMember(member);
        order.setOrderdate(LocalDate.now());
        order.setOrderStatus(dto.getStatus());

        List<OrderProduct> orderProductList = new ArrayList<>();

        for(OrderProductDto item : dto.getOrderItems()){
            Long productId = item.getProductId();
            System.out.println("상품 아이디 : " + productId);

            Optional<Product> optionalProduct = productService.findProductById(productId);

            if(!optionalProduct.isPresent()){
                throw new RuntimeException("해당 상품이 존재하지 않습니다.");
            }

            Product product = optionalProduct.get();

            if(product.getStock() < item.getQuantity()){
                throw new RuntimeException("재고 수량이 부족합니다.");
            }

            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setOrder(order);
            orderProduct.setProduct(product);
            orderProduct.setQuantity(item.getQuantity());

            orderProductList.add(orderProduct);

            product.setStock(product.getStock() - item.getQuantity());

            Long cartProductId = item.getCartProductId();
            if(cartProductId != null){
                cartProductService.deleteCartProductById(cartProductId);
            }else {
                System.out.println("상품 상세 보기 페이지에서 클릭하셨군요.");
            }
        }

        order.setOrderProducts(orderProductList);

        return orderRepository.save(order);
    }
}
