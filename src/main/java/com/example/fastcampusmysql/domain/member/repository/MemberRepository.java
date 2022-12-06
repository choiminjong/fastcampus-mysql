package com.example.fastcampusmysql.domain.member.repository;

import com.example.fastcampusmysql.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;



@RequiredArgsConstructor
@Repository
public class MemberRepository {

    final private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    final static private String TABLE="Member";

    public Optional<Member> findById(Long id){
        /*
         select *
         from member
         where id =: id
        */
        String sql = String.format("SELECT * FROM %s WHERE id = :id", TABLE);
        MapSqlParameterSource param = new MapSqlParameterSource().addValue("id", id);

        RowMapper<Member> rowMapper =(ResultSet resultSet, int rowNum)-> Member
                                    .builder()
                                    .id(resultSet.getLong("id"))
                                    .nickname(resultSet.getString("nickname"))
                                    .email(resultSet.getString("email"))
                                    .birthday(resultSet.getObject("birthday", LocalDate.class))
                                    .createdAt(resultSet.getObject("createdAt", LocalDateTime.class))
                                    .build();

        Member member = namedParameterJdbcTemplate.queryForObject(sql, param, rowMapper);

        return Optional.ofNullable(member);
    }

    public Member save(Member member){
        /*
           member id데이터가 빈 값이라면 저장 빈 값이 아니라면 업데이트
        */
        if(member.getId() ==null){
            return insert(member);
        }

        return update(member);
    }

    private Member insert(Member member){
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(namedParameterJdbcTemplate.getJdbcTemplate())
                                                .withTableName("Member")
                                                .usingGeneratedKeyColumns("id");
        SqlParameterSource params = new BeanPropertySqlParameterSource(member);
        long id = simpleJdbcInsert.executeAndReturnKey(params).longValue();

        return Member.builder().id(id)
                                .email(member.getEmail())
                                .nickname(member.getNickname())
                                .birthday(member.getBirthday())
                                .build();
    }

    private Member update(Member member){
        String sql = String.format("UPDATE %s set email =:email, nickname = :nickname, birthday = :birthday WHERE id =:id",TABLE);
        SqlParameterSource params = new BeanPropertySqlParameterSource(member);
        namedParameterJdbcTemplate.update(sql, params);
        return member;
    }
}

