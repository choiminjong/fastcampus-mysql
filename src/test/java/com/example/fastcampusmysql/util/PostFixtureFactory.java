package com.example.fastcampusmysql.util;

import com.example.fastcampusmysql.domain.post.entity.Post;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.randomizers.range.LocalDateRangeRandomizer;
import org.jeasy.random.randomizers.range.LongRangeRandomizer;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.function.Predicate;
import static org.jeasy.random.FieldPredicates.*;

public class PostFixtureFactory {

    public static EasyRandom get(Long memberId, LocalDate firsDate, LocalDate lastDate) {

        Predicate<Field> idPredicate = named("id")
                                       .and((ofType(Long.class))
                                       .and(inClass(Post.class)));

        Predicate<Field> memberIdPredicate = named("memberId")
                                            .and((ofType(Long.class))
                                            .and(inClass(Post.class)));

        EasyRandomParameters param = new EasyRandomParameters()
                                        .excludeField(idPredicate)
                                        .dateRange(firsDate,lastDate)
                                        .randomize(memberIdPredicate,()->memberId);
        return new EasyRandom(param);
    }
}
