package com.example.fastcampusmysql.controller;

import com.example.fastcampusmysql.domain.member.dto.MemberDto;
import com.example.fastcampusmysql.domain.member.dto.ResisterMemberCommand;
import com.example.fastcampusmysql.domain.member.entity.Member;
import com.example.fastcampusmysql.domain.member.service.MemberReadService;
import com.example.fastcampusmysql.domain.member.service.MemberWriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemberController {

    final private MemberWriteService memberWriteService;

    final private MemberReadService memberReadService;

    @PostMapping("/members")
    public Member register(@RequestBody ResisterMemberCommand command){
        return memberWriteService.register(command);
    }

    @GetMapping("/members/{id}")
    public MemberDto getMember(@PathVariable long id){
        return memberReadService.getMember(id);
    }
}
