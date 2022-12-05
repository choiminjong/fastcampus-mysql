package com.example.fastcampusmysql.domain.member.service;


import com.example.fastcampusmysql.domain.member.dto.ResisterMemberCommand;
import com.example.fastcampusmysql.domain.member.entity.Member;
import com.example.fastcampusmysql.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberWriteService {

    final private MemberRepository memberRepository;

    public Member register(ResisterMemberCommand command){
        /*
            목표 - 회원정보 등록(이메일, 닉네임, 생년월일)를 등록한다.
            - 닉네임은 10자를 넘길 수 없다,
            파라미터 - memberResisterCommand

            val member = Member.of(memberResisterCommand)
            memberRepository.save(member);
        */

        Member member = Member.builder()
                        .nickname(command.nickname())
                        .birthday(command.birthday())
                        .email(command.email())
                        .build();

       return memberRepository.save(member);
    }
}
