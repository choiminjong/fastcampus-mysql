package com.example.fastcampusmysql.domain.post.repository;

import com.example.fastcampusmysql.domain.post.dto.DailyPostCount;
import com.example.fastcampusmysql.domain.post.dto.DailyPostCountRequest;
import com.example.fastcampusmysql.domain.post.entity.Post;
import com.example.fastcampusmysql.util.PageHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
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

    public Page<Post> findAllByMemberId(Long membeId, Pageable pageable){
        MapSqlParameterSource params = new MapSqlParameterSource()
                                        .addValue("memberId", membeId)
                                        .addValue("offset", pageable.getOffset())
                                        .addValue("size", pageable.getPageSize());

        String query = String.format("""
                                    SELECT *
                                    FROM %s
                                    WHERE memberId = :memberId
                                    ORDER BY %s
                                    LIMIT :size
                                    OFFSET :offset
                                    """, TABLE, PageHelper.orderBy(pageable.getSort()));

        List<Post> posts = namedParameterJdbcTemplate.query(query, params, ROW_MAPPER);
        return new PageImpl(posts,pageable,getCount(membeId));

    }

    private Integer getCount(Long memberId) {
        String countQuery = String.format("""
                            SELECT count(id)
                            FROM %s
                            WHERE memberId = :memberId
                            """, TABLE);
        MapSqlParameterSource countParam = new MapSqlParameterSource().addValue("memberId", memberId);
        return namedParameterJdbcTemplate.queryForObject(countQuery, countParam, Integer.class);
    }


    public List<Post> findAllByMemberIdAndOrderByIdDesc(Long memberId, int size) {
        var params = new MapSqlParameterSource()
                                        .addValue("memberId", memberId)
                                        .addValue("size", size);

        String query = String.format("""
                SELECT *
                FROM %s
                WHERE memberId = :memberId
                ORDER BY id DESC
                LIMIT :size
                """, TABLE);

        return namedParameterJdbcTemplate.query(query, params, ROW_MAPPER);
    }

    public List<Post> findAllByLessThanIdAndMemberIdAndOrderByIdDesc(Long id, Long memberId, int size) {
        var params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("memberId", memberId)
                .addValue("size", size);

        String query = String.format("""
                SELECT *
                FROM %s
                WHERE memberId = :memberId and id < :id
                ORDER BY id DESC
                LIMIT :size
                """, TABLE);

        return namedParameterJdbcTemplate.query(query, params, ROW_MAPPER);
    }

    /*
        ?????????  ?????? ??????
    */
    public List<Post> findAllByMemberIdInAndOrderByIdDesc(List<Long> memberIds, int size) {
        if (memberIds.isEmpty()) {
            return List.of();
        }

        var params = new MapSqlParameterSource()
                .addValue("memberIds", memberIds)
                .addValue("size", size);

        String query = String.format("""
                SELECT *
                FROM %s
                WHERE memberId in (:memberIds)
                ORDER BY id DESC
                LIMIT :size
                """, TABLE);

        return namedParameterJdbcTemplate.query(query, params, ROW_MAPPER);

    }

    /*
        ?????????  ?????? ??????
    */
    public List<Post> findAllByLessThanIdAndMemberIdInAndOrderByIdDesc(Long id, List<Long> memberIds, int size) {
        if (memberIds.isEmpty()) {
            return List.of();
        }

        var params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("memberIds", memberIds)
                .addValue("size", size);

        String query = String.format("""
                SELECT *
                FROM %s
                WHERE memberId in (:memberIds) and id < :id
                ORDER BY id DESC
                LIMIT :size
                """, TABLE);

        return namedParameterJdbcTemplate.query(query, params, ROW_MAPPER);

    }


    public List<Post> findAllByIdIn(List<Long> postIds) {
        if (postIds.isEmpty()) {
            return List.of();
        }

        var params = new MapSqlParameterSource()
                .addValue("postIds", postIds);

        String query = String.format("""
                SELECT *
                FROM %s
                WHERE id in (:postIds)
                """, TABLE);

        return namedParameterJdbcTemplate.query(query, params, ROW_MAPPER);

    }


    public Post save(Post post) {
        if (post.getId() == null)
            return insert(post);
        throw new UnsupportedOperationException("post??? ????????? ???????????? ????????????");
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
