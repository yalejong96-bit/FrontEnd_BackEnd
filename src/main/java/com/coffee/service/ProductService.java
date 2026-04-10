package com.coffee.service;

import com.coffee.entity.Product;
import com.coffee.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

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
            File file = new File(productImageLocation + fileName);

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

    private String saveProductImage(String base64Image) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedNow = LocalDateTime.now().format(formatter);

        // UUID 클래스 공부

        String imageFileName = "product_" + formattedNow + ".jpg";

        File imageFile = new File(productImageLocation + imageFileName);
        System.out.println("등록할 이미지 이름");
        System.out.println(imageFile.getAbsolutePath());

        byte[] decodedImage = Base64.getDecoder().decode(base64Image.split(",")[1]);

        try {
            FileOutputStream fos = new FileOutputStream(imageFile);
            fos.write(decodedImage); // c:\shop\images 폴더에 복사
            return imageFileName;

        } catch (Exception e) {
            String message = "이미지 파일 저장중에 오류가 발생했습니다.";
            throw new IllegalStateException(message);
        }
    }

    public Product insertProduct(Product product) {
        // product는 리엑트에서 넘어온 상품 등록을 위한 정보입니다.
        if (product.getImage() != null && product.getImage().startsWith("data:image")) {
            String imageFileName = this.saveProductImage(product.getImage());

            // 데이터 베이스에는 prodcut_년월일시분초.jpg 형식으로 저장되어야 합니다.
            product.setImage(imageFileName);

            product.setInputdate(LocalDate.now());
            System.out.println("서비스 클래스에서 상품 등록 정보 확인");
            System.out.println(product);

            return this.productRepository.save(product); // 데이터 베이스에 추가하기
        }else {
            return null;

        }

    }
}
