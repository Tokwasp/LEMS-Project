package lems.cowshed.repository.regular.event;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lems.cowshed.domain.regular.event.RegularEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

import static lems.cowshed.domain.event.QEvent.event;
import static lems.cowshed.domain.regular.event.QRegularEvent.*;

@Repository
public class RegularEventQueryRepository {

    private EntityManager em;
    private JPAQueryFactory queryFactory;

    public RegularEventQueryRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    public Slice<RegularEvent> searchFetchEvent(Pageable pageable, String name, LocalDate date) {
        List<RegularEvent> regularEvents = queryFactory.select(regularEvent)
                .from(regularEvent)
                .where(nameLike(name), isSameDate(date))
                .join(regularEvent.event, event).fetchJoin()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .orderBy(regularEvent.dateTime.desc())
                .fetch();

        boolean hasNext = regularEvents.size() > pageable.getPageSize();

        if(hasNext){
            regularEvents.remove(regularEvents.size() - 1);
        }

        return new SliceImpl(regularEvents, pageable, hasNext);
    }

    public int searchCount(String name, LocalDate date) {
        Long count = queryFactory.select(regularEvent.count())
                .from(regularEvent)
                .where(nameLike(name), isSameDate(date))
                .fetchOne();

        return count.intValue();
    }

    private BooleanExpression isSameDate(LocalDate date) {
        if (date == null) return null;

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        return regularEvent.dateTime.goe(start).and(regularEvent.dateTime.lt(end));
    }

    private BooleanBuilder nameLike(String name) {
        return nullSafeBuilder(() -> regularEvent.name.contains(name));
    }

    private BooleanBuilder nullSafeBuilder(Supplier<BooleanExpression> f) {
        try {
            return new BooleanBuilder(f.get());
        } catch (Exception e) {
            return new BooleanBuilder();
        }
    }
}
