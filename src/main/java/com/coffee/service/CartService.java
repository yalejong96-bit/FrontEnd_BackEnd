package com.coffee.service;

import com.coffee.dto.CartItemDto;
import com.coffee.dto.CartProductDto;
import com.coffee.entity.Cart;
import com.coffee.entity.CartProduct;
import com.coffee.entity.Member;
import com.coffee.entity.Product;
import com.coffee.repository.CartRepository;
import com.coffee.repository.MemberRepository;
import com.coffee.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final MemberService memberService;
    private final ProductService productService;
    private final CartProductService cartProductService;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    public Cart saveCart(Cart cart) {
        return cartRepository.save(cart);
    }

    private CartProduct findExistingProduct(Cart cart, Product product) {
        // 해당 상품이 카트 내에 들어 있으면, 해당 상품 객체를 반환해주는 메소드
        // 동일한 상품이 이미 카트 내에 들어 있으면 수량을 누적할 목적임
        if (cart.getCartProducts() == null) return null;

        for (CartProduct cp : cart.getCartProducts()) {
            if (cp.getProduct().getId().equals(product.getId())) {
                return cp;
            }
        }
        return null;
    }

    @Transactional
    public String addProductToCart(CartProductDto dto, String email) throws Exception {
        // 회원 조회
        Member member = memberRepository.findByEmail(email);

        if (member == null) {
            throw new RuntimeException("회원 없음");
        }

        // 상품 조회
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("상품 없음"));

        // 재고 확인(주문 수량이 재고보다 많으면)
        if (product.getStock() < dto.getQuantity()) {
            throw new RuntimeException("재고 수량이 부족합니다.");
        }

        // 장바구니 조회 또는 생성
        Cart cart = cartRepository.findByMember(member).orElse(null);

        if (cart == null) { // 카트가 구비가 안된 고객
            Cart newCart = new Cart(); // 새 카트 준비
            newCart.setMember(member); // 고객에게 할당

            cart = saveCart(newCart);
        }

        // 기존 상품이 존재하는 지 확인 후 수량 처리
        CartProduct existingCartProduct = findExistingProduct(cart, product);

        if (existingCartProduct != null) { // 장바구니에 해당 상품이 들어 있으면
            // 기존 수량에 장바구니에서 요청한 수량을 누적합니다.
            existingCartProduct.setQuantity(existingCartProduct.getQuantity() + dto.getQuantity());

            // 서비스를 저장 메소드를 요청하여 database에 저장합니다.
            cartProductService.saveCartProduct(existingCartProduct);

        } else { // 장바구니에 품목이 없는 경우
            CartProduct cp = new CartProduct();
            cp.setCart(cart);
            cp.setProduct(product);
            cp.setQuantity(dto.getQuantity());
            cartProductService.saveCartProduct(cp);
        }

        return "요청하신 장품이 장바구니에 추가되었습니다.";
    }

    public List<CartItemDto> getCartItemsByMemberId(Long memberId) {
        //회원 조회
        Member member = memberService.findMemberByEmail(memberId)
                .orElseThrow(() -> new RuntimeException("유효하지 않은 회원입니다"));

        // 회원이 소유한 카트 정보 조회
        // 없으면 빈 카트 생성
        Cart cart = cartRepository.findByMember(member).orElseGet(Cart::new);

        List<CartItemDto> cartItemDtoList = new ArrayList<>();
        for (CartProduct cp : cart.getCartProducts()) {
            cartItemDtoList.add(new CartItemDto(cp));
        }
        return cartItemDtoList;

        /*return cart.getCartProducts().stream()
                .map(CartItemDto::new).toList();*/
    }
}
