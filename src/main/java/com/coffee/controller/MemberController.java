package com.coffee.controller;

import com.coffee.entity.Member;
import com.coffee.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MemberController {
//    @Autowired 이걸 많이 봄
    private final MemberService memberService;

    @PostMapping("/member/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody Member bean, BindingResult bindingResult) { // 회원 가입하기
        System.out.println("회원 가입 정보");
        System.out.println(bean);

        if(bindingResult.hasErrors()){ // 가입에 뭔가 문제 있음
            Map<String, String> errors = new HashMap<>();
            for( FieldError xx : bindingResult.getFieldErrors()){
                errors.put(xx.getField(), xx.getDefaultMessage());
            }
//            System.out.println(errors);
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        // 이메일 중복 체크
        Member member = memberService.findByEmail(bean.getEmail());
        if(member != null){ // 이미 존재하는 이메일 주소
            return new ResponseEntity<>(Map.of("email", "이미 존재하는 이메일 주소입니다."), HttpStatus.BAD_REQUEST);
        }

        // 회원 가입 처리
        memberService.insert(bean);
        return new ResponseEntity<>("회원 가입 성공", HttpStatus.OK) ; // 회원 가입 성공
    }
}
