package com.example.fastcampusmysql.domain.post.repository;

import com.example.fastcampusmysql.domain.post.dto.DailyPostCount;
import com.example.fastcampusmysql.domain.post.dto.DailyPostCountRequest;
import com.example.fastcampusmysql.domain.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@RequiredArgsConstructor
@Repository
public class PostRepository {

    final static private String TABLE="Post";
    final private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final RowMapper<Post> ROW_MAPPER = (ResultSet resultSet, int rowNum) -> Post.builder()
            .id(resultSet.getLong("id"))
            .memberId(resultSet.getLong("memberId"))
            .contents(resultSet.getString("contents"))
            .createdDate(resultSet.getObject("createdDate", LocalDate.class))
            .createdAt(resultSet.getObject("createdAt", LocalDateTime.class))
            .build();

    public List<DailyPostCount> groupByCreatedDate(DailyPostCountRequest request) {
        var params = new BeanPropertySqlParameterSource(request);
        String query = String.format("""
                                    SELECT memberId, createdDate, count(id) as cnt 
                                    FROM %s 
                                    WHERE memberId = :memberId and createdDate between :firstDate and :lastDate
                                    GROUP BY memberId, createdDate
                                    """, TABLE);

        RowMapper<DailyPostCount> mapper = (ResultSet resultSet, int rowNum) -> new DailyPostCount(
                                            resultSet.getLong("memberId"),
                                            resultSet.getObject("createdDate", LocalDate.class),
                                            resultSet.getLong("cnt")
        );

        return namedParameterJdbcTemplate.query(query, params, mapper);
    }

    public Post save(Post post) {
        if (post.getId() == null)
            return insert(post);
        throw new UnsupportedOperationException("post는 갱신을 지원하지 않습니다");
    }

    private Post insert(Post post){
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(namedParameterJdbcTemplate.getJdbcTemplate())
                                            .withTableName(TABLE)
                                            .usingGeneratedKeyColumns("id");
        SqlParameterSource params = new BeanPropertySqlParameterSource(post);
        long id = simpleJdbcInsert.executeAndReturnKey(params).longValue();

        return Post.builder()
                .id(id)
                .memberId(post.getMemberId())
                .contents(post.getContents())
                .createdDate(post.getCreatedDate())
                .createdAt(post.getCreatedAt())
                .build();
    }

    public void bulkInsert(List<Post> posts) {
        var sql = String.format("""
                INSERT INTO `%s` (memberId, contents, createdDate, createdAt)
                VALUES (:memberId, :contents, :createdDate, :createdAt)
                """, TABLE);

        SqlParameterSource[] params = posts
                .stream()
                .map(BeanPropertySqlParameterSource::new)
                .toArray(SqlParameterSource[]::new);
        namedParameterJdbcTemplate.batchUpdate(sql, params);
    }

}
