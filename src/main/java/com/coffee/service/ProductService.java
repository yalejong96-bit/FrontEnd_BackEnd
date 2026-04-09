package com.coffee.service;

import com.coffee.entity.Product;
import com.coffee.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public List<Product> getProductList() {
        return this.productRepository.findProductByOrderByIdDesc();
    }

    @Value("${productImageLocation}")
    private String productImageLocation;

    // 상품 id를 이용한 삭제
    public boolean deleteProduct(Long id) {
        Product product = productRepository.findById(id).orElse(null);

        if (product == null) {
            return false;
        }

        String fileName = product.getImage();
        if (fileName != null && !fileName.isEmpty()) {
            File file = new File(productImageLocation+fileName);

            System.out.println("삭제될 파일 이름");
            System.out.println(file.getAbsolutePath());

            if (file.exists()) {
                boolean deleted = file.delete();

                if (!deleted) {
                    System.out.println("이미지 삭제 실패");
                }
            }
        }
        productRepository.deleteById(id);
        return true;
    }
}
