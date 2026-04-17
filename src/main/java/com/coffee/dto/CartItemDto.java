package com.coffee.dto;

import com.coffee.entity.CartProduct;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemDto {
    private Long cartProductId;
    private Long productId;
    private String name;
    private String image;
    private int quantity;
    private int price;
    private int stock;
    private boolean checked;

    public CartItemDto(CartProduct cartProduct) {
        this.cartProductId = cartProduct.getId();
        this.productId = cartProduct.getProduct().getId();
        this.name = cartProduct.getProduct().getName();
        this.image = cartProduct.getProduct().getImage();
        this.price = cartProduct.getProduct().getPrice();
        this.stock = cartProduct.getProduct().getStock();
        this.quantity = cartProduct.getQuantity();
    }
}
