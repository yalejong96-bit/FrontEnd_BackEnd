package com.coffee.test;

import com.coffee.constant.Role;
import com.coffee.entity.Member;
import com.coffee.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestConstructor;

import java.time.LocalDate;

@SpringBootTest
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class MemberTest {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder ;

    @Test
    @DisplayName("회원 몇 명 추가하기")
    void insertMember(){
        Member mem01 = new Member();
        mem01.setName("관리자");
        mem01.setEmail("admin1@naver.com");
        mem01.setPassword(passwordEncoder.encode("Admin@123"));
        mem01.setAddress("마포구 공덕동");
        mem01.setRole(Role.ADMIN);
        mem01.setRegdate(LocalDate.now());

        memberRepository.save(mem01);
    }
}
