package com.example.fastcampusmysql.domain.member.service;


import com.example.fastcampusmysql.domain.member.dto.ResisterMemberCommand;
import com.example.fastcampusmysql.domain.member.entity.Member;
import com.example.fastcampusmysql.domain.member.entity.MemberNicknameHistory;
import com.example.fastcampusmysql.domain.member.repository.MemberNicknameHistoryRepository;
import com.example.fastcampusmysql.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberWriteService {

    final private MemberRepository memberRepository;
    final private MemberNicknameHistoryRepository memberNicknameHistoryRepository;

    public Member register(ResisterMemberCommand command){
        Member member = Member.builder()
                        .nickname(command.nickname())
                        .birthday(command.birthday())
                        .email(command.email())
                        .build();

        Member saveMember = memberRepository.save(member);

        //TODO : 변경내역 히스토리를 저장한다.
        saveMemberNicknamehistory(saveMember);
        return saveMember;
    }

    public void changeNickname(Long memberId, String nickname){
        /*
            1. 회원의 이름을 변경
            2. 변경 내역을 저장
         */
        Member member = memberRepository.findById(memberId).orElseThrow();
        member.changeNickname(nickname);
        Member saveMember = memberRepository.save(member);

        //TODO : 변경내역 히스토리를 저장한다.
        saveMemberNicknamehistory(saveMember);
    }

    private void saveMemberNicknamehistory(Member member){
        MemberNicknameHistory history = MemberNicknameHistory
                                        .builder()
                                        .memberId(member.getId())
                                        .nickname(member.getNickname())
                                        .build();

        memberNicknameHistoryRepository.save(history);
    }
}
