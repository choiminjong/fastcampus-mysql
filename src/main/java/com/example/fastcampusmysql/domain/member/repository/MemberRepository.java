package com.example.fastcampusmysql.domain.member.repository;

import com.example.fastcampusmysql.domain.member.entity.Member;
import org.springframework.stereotype.Repository;

@Repository
public class MemberRepository {

    public Member save(Member member){
        /*
             member id데이터가 빈 값이라면 저장 빈 값이 아니라면 업데이트
         */
        return member;
    }
}
