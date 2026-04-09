package com.coffee.controller;

import com.coffee.entity.Product;
import com.coffee.repository.ProductRepository;
import com.coffee.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/list")
    public List<Product> list() {
        List<Product> products = this.productService.getProductList();
    /*    List<Product> productList = new ArrayList<>();

        for (Product product : products) {

            if (product.getImage().contains(".jpg")) {
                productList.add(product);

            }

        }
        return productList;*/
        return products;
    }

    @DeleteMapping("/delete/{id}") // {id}를 경로 변수라고 부르면, 가변 매개 변수
    public ResponseEntity<String> delete(@PathVariable Long id) {
        try {
            boolean isDeleted = this.productService.deleteProduct(id);// 문제가 생김

            if (isDeleted) {
                return ResponseEntity.ok(id + "번 상품이 삭제되었습니다.");
            } else {
                return ResponseEntity.badRequest().body(id + "번 상품이 존재하지 않습니다.");
            }

        } catch (DataIntegrityViolationException err) {
            String message = "해당 상품은 장바구니에 포함이 되어 있거나, 이미 매출이 발생한 상품 입니다. \n확인해 주세요." ;
            return ResponseEntity.badRequest().body(message);

        } catch (Exception err) {
            return ResponseEntity.internalServerError().body("오류 발생 : " + err.getMessage());//예외 객체가 처리
        }
    }
}
