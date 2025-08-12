package lems.cowshed.repository.user.query;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lems.cowshed.domain.bookmark.Bookmark;
import lems.cowshed.domain.event.participation.EventParticipation;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.function.Supplier;

import static lems.cowshed.domain.bookmark.BookmarkStatus.BOOKMARK;
import static lems.cowshed.domain.bookmark.QBookmark.bookmark;
import static lems.cowshed.domain.event.participation.QEventParticipation.eventParticipation;

@Repository
public class UserMyPageQueryRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public UserMyPageQueryRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    public Slice<EventParticipation> findPagingEvents(Long lastEventId, Long userId, int pageSize) {
        List<EventParticipation> content = queryFactory.selectFrom(eventParticipation)
                .where(
                        dynamicParticipatedEventId(lastEventId),
                        eventParticipation.user.id.eq(userId)
                )
                .orderBy(eventParticipation.eventId.desc())
                .limit(pageSize + 1)
                .fetch();

        boolean hasNext = pageSize < content.size();

        if (hasNext) {
            content.remove(content.size() - 1);
        }
        return new SliceImpl<>(content, PageRequest.of(0, pageSize), hasNext);
    }

    public Slice<Bookmark> findPagingBookmark(Long lastEventId, Long userId, int pageSize) {
        List<Bookmark> content = queryFactory.selectFrom(bookmark)
                .where(
                        dynamicBookmarkEventId(lastEventId),
                        bookmark.userId.eq(userId),
                        bookmark.status.eq(BOOKMARK)
                )
                .orderBy(bookmark.event.id.desc())
                .limit(pageSize + 1)
                .fetch();

        boolean hasNext = pageSize < content.size();

        if (hasNext) {
            content.remove(content.size() - 1);
        }
        return new SliceImpl<>(content, PageRequest.of(0, pageSize), hasNext);
    }

    private BooleanBuilder dynamicParticipatedEventId(Long lastEventId) {
        return nullSafeBuilder(() -> eventParticipation.eventId.lt(lastEventId));
    }

    private BooleanBuilder dynamicBookmarkEventId(Long eventId) {
        return nullSafeBuilder(() -> bookmark.event.id.lt(eventId));
    }

    private BooleanBuilder nullSafeBuilder(Supplier<BooleanExpression> supplier) {
        try {
            return new BooleanBuilder(supplier.get());
        } catch (Exception e) {
            return new BooleanBuilder();
        }
    }
}
