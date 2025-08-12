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
import java.util.Map;
import java.util.stream.Collectors;

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
        Slice<EventParticipation> slice = userMyPageQueryRepository.findPagingEvents(lastEventId, userId, pageSize);
        List<EventParticipation> myEventParticipants = slice.getContent();

        List<Event> events = findConnectedEventFrom(myEventParticipants);
        Map<Long, List<EventParticipation>> groupedByEventIdMap = findEventParticipantCountFrom(events);
        return MyPageParticipatedEventsInfo.fromEvents(events, groupedByEventIdMap, slice.hasNext());
    }

    public MyPageBookmarkedEventsInfo getBookmarkedEvents(Long lastEventId, Long userId, int pageSize) {
        Slice<Bookmark> bookmarks = userMyPageQueryRepository.findPagingBookmark(lastEventId, userId, pageSize);
        List<Event> events = findBookmarkedEventInfo(bookmarks.getContent());
        Map<Long, List<EventParticipation>> groupedByEventIdMap = findEventParticipantCountFrom(events);

        return MyPageBookmarkedEventsInfo.of(events, groupedByEventIdMap, bookmarks.hasNext());
    }

    private List<Event> findConnectedEventFrom(List<EventParticipation> eventParticipants){
        List<Long> eventIds = eventParticipants.stream()
                .map(EventParticipation::getEventId)
                .toList();

        return eventRepository.findByIdIn(eventIds);
    }

    private Map<Long, List<EventParticipation>> findEventParticipantCountFrom(List<Event> events){
        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .toList();

        List<EventParticipation> eventParticipants = participantRepository.findByEventIdIn(eventIds);

        return eventParticipants.stream()
                .collect(Collectors.groupingBy(
                        EventParticipation::getEventId
                ));
    }

    private List<Event> findBookmarkedEventInfo(List<Bookmark> bookmarks) {
        List<Long> eventIds = bookmarks.stream()
                .map(b -> b.getEvent().getId())
                .toList();

        return eventRepository.findByIdIn(eventIds);
    }

}