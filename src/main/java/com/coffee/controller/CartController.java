package com.coffee.controller;

import com.coffee.dto.CartItemDto;
import com.coffee.dto.CartProductDto;
import com.coffee.entity.Member;
import com.coffee.service.CartProductService;
import com.coffee.service.CartService;
import com.coffee.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;
    private final CartProductService cartProductService;

    @PostMapping("/insert")
    public ResponseEntity<String> addToCart(@RequestBody CartProductDto dto, Authentication authentication) {
        try {
            String email = authentication.getName();
            String message = cartService.addProductToCart(dto, email);

            return ResponseEntity.ok(message);
        } catch (Exception err) {
            return ResponseEntity.badRequest().body(err.getMessage());
        }
    }

    private final MemberService memberService;

    @GetMapping("/list")
    public ResponseEntity<List<CartItemDto>> getCartProducts(Authentication authentication) {
        String email = authentication.getName();

        Member member = memberService.findByEmail(email);

        if (member == null) {
            new RuntimeException("사용자가 존재하지 않습니다.");
        }

        return ResponseEntity.ok(cartService.getCartItemsByMemberId(member.getId()));
    }

    @PatchMapping("/edit/{cartProductId}")
    public ResponseEntity<String> editCartProductQuantity(
            @PathVariable Long cartProductId,
            @RequestParam(required = false) Integer quantity) {
        System.out.println("카트 상품 아이디 : " + cartProductId);
        System.out.println("변경할 갯수 : " + quantity);
        try{
            String message = cartProductService.editCartProductQuantity(cartProductId, quantity);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            System.out.println(this.getClass());
            System.out.println(e.getMessage());
            return  ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{cartProductId}")
    public ResponseEntity<String> deleteCartProduct(@PathVariable Long cartProductId) {
        System.out.println("삭제할 카드 상품 아이디 : " + cartProductId);

        cartProductService.deleteCartProductById((cartProductId));

        String message = "카트 상품 " + cartProductId + "번이 장바구니 목록에서 삭제되었습니다.";
        return ResponseEntity.ok(message);
    }
}
