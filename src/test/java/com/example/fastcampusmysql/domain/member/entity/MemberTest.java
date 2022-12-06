package com.example.fastcampusmysql.domain.member.entity;

import com.example.fastcampusmysql.util.MemberFixtureFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MemberTest {

    @DisplayName("회원은 닉네임을 변경할 수 있다.")
    @Test
    public void testCahgeName(){
        Member member = MemberFixtureFactory.create();
        String toChangeName ="pub";

        //member.changeNickname(toChangeName);
        Assertions.assertEquals(toChangeName, member.getNickname());
    }

    @DisplayName("회원은 닉네임을 10자리를 초과할 수 없다.")
    @Test
    public void testNicknameMaxLengh(){
        Member member = MemberFixtureFactory.create();
        String overMaxLenghthName ="pubpubpu";

        Assertions.assertThrows(IllegalArgumentException.class,
                               ()->member.changeNickname(overMaxLenghthName));
    }

}