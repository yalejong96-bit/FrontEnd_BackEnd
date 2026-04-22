package com.coffee.controller;

import com.coffee.constant.Category;
import com.coffee.dto.SearchDto;
import com.coffee.entity.Product;
import com.coffee.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;

//    @GetMapping("/list")
//    public List<Product> list() {
//        List<Product> products = this.productService.getProductList();
        /*List<Product> productList = new ArrayList<>();

        for (Product product : products) {

            if (product.getImage().contains(".jpg")) {
                productList.add(product);

            }

        }
        return productList;*/

        /*List<Product> productList = new ArrayList<>();

        for (Product product : products) {

            if (product.getCategory().getDescription().contains("빵")) {
                productList.add(product);

            }

        }
        return productList;*/

        /*List<Product> productList = new ArrayList<>();

        for (Product product : products) {
            if (product.getPrice() > 10000) {
                productList.add(product);

            }

        }
        return productList;*/

        /*List<Product> productList = new ArrayList<>();

        for (Product product : products) {

            if (product.getCategory() != null && product.getCategory().getDescription() != null) {
                if (product.getCategory().getDescription().contains("빵") && product.getPrice() > 10000) {
                    productList.add(product);

                }
            }
        }
        return productList;*/

    /*return products.stream().filter(p -> p.getPrice() > 10000).toList();*/

//        return products;
//    }

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
            String message = "해당 상품은 장바구니에 포함이 되어 있거나, 이미 매출이 발생한 상품 입니다. \n확인해 주세요.";
            return ResponseEntity.badRequest().body(message);

        } catch (Exception err) {
            return ResponseEntity.internalServerError().body("오류 발생 : " + err.getMessage());//예외 객체가 처리
        }
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insert(@Valid @RequestBody Product product, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError xx : bindingResult.getFieldErrors()) {
                errors.put(xx.getField(), xx.getDefaultMessage());
            }

            return new ResponseEntity<>(
                    Map.of(
                            "message", "상품 등록 유효성 검사에 문제가 있습니다.",
                            "errors", errors
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }

        /*if(!product.getImage().startsWith("data:image")){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            Map.of(
                                    "errors", Map.of("image", "이미지 파일이 아닙니다.")
                            )
                    );
        }*/

        try {
            Product savedProduct = this.productService.insertProduct(product);

            if (savedProduct == null) {
                return ResponseEntity
                        .status(500)
                        .body(
                                Map.of(
                                        "message", "상품 등록에 실패하였습니다.",
                                        "error", "bad image file format"
                                )
                        );
            }

            return ResponseEntity.ok(
                    Map.of(
                            "message", "상품이 성공적으로 등록되었습니다.",
                            "image", savedProduct.getImage()
                    )
            );

        } catch (IllegalStateException err) { // 경로 또는 이미지 저장 문제
            return ResponseEntity
                    .status(500)
                    .body(
                            Map.of(
                                    "message", err.getMessage(),
                                    "error", "File Save Error"
                            )
                    );

        } catch (Exception err) { // 데이터 베이스 오류
            return ResponseEntity
                    .status(500)
                    .body(
                            Map.of(
                                    "message", err.getMessage(),
                                    "error", "Internal Server Error"
                            )
                    );
        }
    }

    @GetMapping("/update/{id}")
    public ResponseEntity<Product> getUpdate(@PathVariable Long id) {
        System.out.println("수정할 상품 번호 : " + id);

        Product product = this.productService.getProductById(id);

        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } else {
            return ResponseEntity.ok(product);
        }
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<?> putUpdate(@PathVariable Long id,
                                       @Valid @RequestBody Product updatedProduct,
                                       BindingResult bindingResult) {

        // 유효성 검사
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError xx : bindingResult.getFieldErrors()) {
                errors.put(xx.getField(), xx.getDefaultMessage());
            }
            return new ResponseEntity<>(
                    Map.of("message", "상품 수정 유효성 검사에 문제가 있습니다.", "errors", errors
                    ),
                    HttpStatus.BAD_REQUEST

            );
        }

        /*if (!updatedProduct.getImage().startsWith("data:image")) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            Map.of(
                                    "errors", Map.of("image", "이미지 파일이 아닙니다.")
                            )
                    );
        }*/

        // 상품 정보 수정
        Optional<Product> findProduct = productService.findById(id);


        if (findProduct.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            Product saveProduct = findProduct.get();
            this.productService.updateProduct(saveProduct, updatedProduct);

            return ResponseEntity.ok(Map.of("message", "상품 수정 성공"));

        } catch (Exception err) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            Map.of(
                                    "messgae", err.getMessage(),
                                    "error", "상품 수정 실패"
                            )
                    );
        }
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<Product> detail(@PathVariable Long id) {
        Product product = productService.getProductById(id);

        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            return ResponseEntity.ok(product);
        }
    }

    @GetMapping("")
    public List<Product> getBigsizeProducts(@RequestParam(required = false) String filter) {
        return productService.getProductsByFilter(filter);
    }

    //    @GetMapping("/list")
//    public ResponseEntity<Page<Product>> listProducts(
//            @RequestParam(defaultValue = "0") int pageNumber,
//            @RequestParam(defaultValue = "6") int pageSize
//    ) {
//        System.out.println("pageNumber : " + pageNumber + ", pageSize : " + pageSize);
//
//        Sort mysort = Sort.by(Sort.Direction.DESC, "id");
//
//        Pageable pageable = PageRequest.of(pageNumber, pageSize, mysort);
//
//        Page<Product> productPage = productService.listProducts(pageable);
//
//        System.out.println(productPage.getContent());
//
//        return ResponseEntity.ok(productPage);
//    }
    @GetMapping("/list")
    public ResponseEntity<Page<Product>> listProducts(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "6") int pageSize,
            @RequestParam(defaultValue = "all") String searchDateType,
            @RequestParam(defaultValue = "") Category category,
            @RequestParam(defaultValue = "") String searchMode,
            @RequestParam(defaultValue = "") String searchKeyword
    ) {
        SearchDto searchDto = new SearchDto(searchDateType, category, searchMode, searchKeyword);

        Page<Product> products = productService.listProducts(searchDto, pageNumber, pageSize);

        System.out.println("검색 조건 : " + searchDto);
        System.out.println("총 상품 개수 : " + products.getTotalElements());
        System.out.println("총 페이지 번호 : " + products.getTotalPages());
        System.out.println("현재 페이지 번호 : " + products.getNumber());

        // Http 응답 코드 200과 함께 상품 정보를 json 형태로 반환해 줍니다.
        return ResponseEntity.ok(products);
    }
}
