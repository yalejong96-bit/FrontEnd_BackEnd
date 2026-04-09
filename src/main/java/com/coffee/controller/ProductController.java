package com.coffee.controller;

import com.coffee.entity.Product;
import com.coffee.repository.ProductRepository;
import com.coffee.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/list")
    public List<Product> list() {
        List<Product> products = this.productService.getProductList();

        for (Product product : products) {
            product.getName().contains(".jpg");
            if (product.getName().contains(".jpg")) {
            }
        }
        return products;
    }

}
