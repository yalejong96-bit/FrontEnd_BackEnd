package com.coffee.service;

import com.coffee.constant.Role;
import com.coffee.entity.Member;
import com.coffee.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public Member findByEmail(String email){
        return memberRepository.findByEmail(email);
    }

    @Autowired
    private PasswordEncoder passwordEncoder ;

    public void insert (Member bean){
        // 회원 가입환 사용자의 역할과 등록 일자는 여기서 설정
        bean.setRole(Role.USER);
        bean.setRegdate(LocalDate.now());

        String encodedPassword = passwordEncoder.encode(bean.getPassword());
        bean.setPassword(encodedPassword);

        memberRepository.save(bean);
    }
}
