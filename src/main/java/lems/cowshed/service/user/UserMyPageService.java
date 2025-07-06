package lems.cowshed.service.user;

import lems.cowshed.domain.bookmark.Bookmark;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.event.participation.EventParticipation;
import lems.cowshed.domain.user.User;
import lems.cowshed.dto.user.mypage.MyPageParticipatedEventsInfo;
import lems.cowshed.dto.user.mypage.MyPageInfo;
import lems.cowshed.dto.user.mypage.MyPageBookmarkedEventsInfo;
import lems.cowshed.global.exception.NotFoundException;
import lems.cowshed.repository.bookmark.BookmarkRepository;
import lems.cowshed.repository.event.EventRepository;
import lems.cowshed.repository.event.participation.EventParticipantRepository;
import lems.cowshed.repository.user.UserRepository;
import lems.cowshed.repository.user.query.UserMyPageQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static lems.cowshed.global.exception.Message.USER_NOT_FOUND;
import static lems.cowshed.global.exception.Reason.USER_ID;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserMyPageService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final UserMyPageQueryRepository userMyPageQueryRepository;
    private final EventParticipantRepository participantRepository;
    private final BookmarkRepository bookmarkRepository;

    public MyPageInfo getMyPage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_ID, USER_NOT_FOUND));

        long participatedEventCount = participantRepository.findCountByUserId(user.getId());
        long bookmarkCount = bookmarkRepository.findCountByUserId(user.getId());
        return MyPageInfo.of(user.getUsername(), participatedEventCount, bookmarkCount);
    }

    public MyPageParticipatedEventsInfo getParticipatedEvents(Long lastEventId, Long userId, int pageSize) {
        Slice<EventParticipation> myEventParticipation = userMyPageQueryRepository.findPagingEvents(lastEventId, userId, pageSize);
        List<Event> events = findParticipatedEventInfo(myEventParticipation.getContent());

        return MyPageParticipatedEventsInfo.fromEvents(events, myEventParticipation.hasNext());
    }

    public MyPageBookmarkedEventsInfo getBookmarkedEvents(Long lastEventId, Long userId, int pageSize) {
        Slice<Bookmark> bookmarks = userMyPageQueryRepository.findPagingBookmark(lastEventId, userId, pageSize);
        List<Event> events = findBookmarkedEventInfo(bookmarks.getContent());

        return MyPageBookmarkedEventsInfo.of(events, bookmarks.hasNext());
    }

    private List<Event> findParticipatedEventInfo(List<EventParticipation> myEventParticipation) {
        List<Long> eventIds = myEventParticipation.stream()
                .map(p -> p.getEvent().getId())
                .toList();

        return eventRepository.findByIdInFetchParticipation(eventIds);
    }

    private List<Event> findBookmarkedEventInfo(List<Bookmark> bookmarks) {
        List<Long> eventIds = bookmarks.stream()
                .map(b -> b.getEvent().getId())
                .toList();

        return eventRepository.findByIdInFetchParticipation(eventIds);
    }

}