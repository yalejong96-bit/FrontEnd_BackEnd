package com.coffee.repository;

import com.coffee.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository<관리하고자 하는 엔티티 이름, 엔티티의 기본키 타입>
public interface MemberRepository extends JpaRepository<Member, Long> {
}
