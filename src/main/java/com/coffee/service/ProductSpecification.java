package com.coffee.service;

import com.coffee.constant.Category;
import com.coffee.entity.Product;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ProductSpecification {
    public static Specification<Product> hasDateRange(String searchDateType) {
        return new Specification<Product>() {
            @Override
            public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                LocalDateTime now = LocalDateTime.now(); // 현재 시각

                LocalDateTime startDate = null; // 검색 시작 일자

                switch (searchDateType){
                    case "1d":
                        startDate = now.minus(1, ChronoUnit.DAYS) ;
                        break;
                    case "1w":
                        startDate = now.minus(1, ChronoUnit.WEEKS) ;
                        break;
                    case "1m":
                        startDate = now.minus(1, ChronoUnit.MONTHS) ;
                        break;
                    case "6m":
                        startDate = now.minus(6, ChronoUnit.MONTHS) ;
                        break;
                    case "all":
                    default: // 전체 기간 조회
                        return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
                }
                return criteriaBuilder.greaterThanOrEqualTo(root.get("inputdate"), startDate);
            }
        };
    }

    public static Specification<Product> hasCategory(Category category){
        return new Specification<Product>() {
            @Override
            public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if(category == Category.ALL){
                    return criteriaBuilder.conjunction();
                }else {
                    return criteriaBuilder.equal(root.get("category"), category);
                }
            }
        };
    }

    public static Specification<Product> hasNameLike(String keyword){
        return new Specification<Product>() {
            @Override
            public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.like(root.get("name"), "%" + keyword + "%");
            }
        };
    }

    public static Specification<Product> hasDescriptionLike(String keyword){
        return new Specification<Product>() {
            @Override
            public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.like(root.get("description"), "%" + keyword + "%");
            }
        };
    }
}
