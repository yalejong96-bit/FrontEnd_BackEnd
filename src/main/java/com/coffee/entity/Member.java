package com.coffee.entity;

import com.coffee.constant.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter @Setter @ToString @Entity
// 회원 1명을 의미하는 엔티티 클래스
@Table(name = "members")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "member_id")// pk 컬럼 이름 : 테이블단수명_id
    private Long id;

    @NotBlank(message = "이름은 필수 입력 사항입니다.")
    private String name;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "이메일은 필수 입력 사항입니다.")
    @Email(message = "올바른 이메일 형식으로 입력해 주셔야 합니다.")
    private String email;

    @NotBlank(message = "비밀 번호는 필수 입력 사항입니다.")
    @Size(min = 8, max = 255, message = "비밀 번호는 8자리 이상, 255자리 이하로 입력해 주세요.")
    @Pattern(regexp = ".*[A-Z].*", message = "비밀 번호는 대문자 1개 이상을 포함해야 합니다.")
    @Pattern(regexp = ".*[!@#$%].*", message = "비밀 번호는 특수 문자 '!@#$%' 중 하나 이상을 포함해야 합니다.")
    private String password;

    @NotBlank(message = "주소는 필수 입력 사항입니다.")
    private String address;

    @Enumerated(EnumType.STRING)
    private Role role;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate regdate;
}
