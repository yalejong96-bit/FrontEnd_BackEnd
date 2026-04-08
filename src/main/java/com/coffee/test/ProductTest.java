package com.coffee.test;

import com.coffee.common.GenerateData;
import com.coffee.entity.Product;
import com.coffee.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
@Component
@SpringBootTest
public class ProductTest {

    @Autowired
    private ProductRepository productRepository ;

    @Test
    @DisplayName("이미지를 이용한 데이터 추가")
    public void createProductMany(){
        GenerateData gendata = new GenerateData();

        List<String> imageNameList = gendata.getImageFileNames();
        System.out.println("총 이미지 갯수 : " + imageNameList.size());

        for (int i = 0; i < imageNameList.size(); i++) {
            Product bean = gendata.createProduct(i, imageNameList.get(i));
            /*System.out.println(bean);*/
            this.productRepository.save(bean);

        }
    }
}
