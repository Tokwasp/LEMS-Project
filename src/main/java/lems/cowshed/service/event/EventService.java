package lems.cowshed.service.event;

import lems.cowshed.config.aws.AwsS3Util;
import lems.cowshed.domain.image.ImageType;
import lems.cowshed.domain.image.UploadFile;
import lems.cowshed.domain.bookmark.Bookmark;
import lems.cowshed.domain.bookmark.BookmarkStatus;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.event.Events;
import lems.cowshed.domain.event.participation.EventParticipation;
import lems.cowshed.domain.event.participation.Participants;
import lems.cowshed.domain.regular.event.RegularEvent;
import lems.cowshed.domain.regular.event.participation.RegularEventParticipation;
import lems.cowshed.domain.user.User;
import lems.cowshed.dto.event.EventIdProvider;
import lems.cowshed.dto.event.request.EventSaveRequestDto;
import lems.cowshed.dto.event.request.EventSearchCondition;
import lems.cowshed.dto.event.request.EventUpdateRequestDto;
import lems.cowshed.dto.event.response.*;
import lems.cowshed.dto.event.response.query.EventParticipantQueryDto;
import lems.cowshed.global.exception.BusinessException;
import lems.cowshed.global.exception.NotFoundException;
import lems.cowshed.repository.bookmark.BookmarkRepository;
import lems.cowshed.repository.event.EventRepository;
import lems.cowshed.repository.event.participation.EventParticipantRepository;
import lems.cowshed.repository.event.query.BookmarkedEventSimpleInfoQuery;
import lems.cowshed.repository.event.query.EventQueryRepository;
import lems.cowshed.repository.event.query.ParticipatingEventSimpleInfoQuery;
import lems.cowshed.repository.regular.event.RegularEventRepository;
import lems.cowshed.repository.regular.event.participation.RegularEventParticipationRepository;
import lems.cowshed.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static lems.cowshed.domain.bookmark.BookmarkStatus.BOOKMARK;
import static lems.cowshed.domain.bookmark.BookmarkStatus.NOT_BOOKMARK;
import static lems.cowshed.global.exception.Message.*;
import static lems.cowshed.global.exception.Reason.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class EventService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventQueryRepository eventQueryRepository;
    private final BookmarkRepository bookmarkRepository;
    private final EventParticipantRepository eventParticipantRepository;
    private final RegularEventRepository regularEventRepository;
    private final RegularEventParticipationRepository regularEventParticipationRepository;
    private final AwsS3Util awsS3Util;
    private final CacheManager cacheManager;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String FIRST_PAGE_CACHE_KEY = "eventFirstPage::page0";
    private static final long INVALIDATION_CAP_SECONDS = 5L;

    public EventInfo getEvent(Long eventId, String username) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(EVENT_ID, EVENT_NOT_FOUND));

        List<EventParticipation> participants = eventParticipantRepository.findByEventId(event.getId());

        if (event.isNotSameAuthor(username)) {
            throw new BusinessException(EVENT_AUTHOR, EVENT_NOT_REGISTERED_BY_USER);
        }

        String accessUrl = awsS3Util.getAccessUrl(event.getUploadFile());
        return EventInfo.of(event, participants.size(), accessUrl);
    }

    @Transactional
    public Long saveEvent(EventSaveRequestDto requestDto, String username) throws IOException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(USER_NAME, USER_NOT_FOUND));

        UploadFile uploadFile = awsS3Util.uploadFile(requestDto.getFile(), ImageType.PUBLIC);
        Event event = requestDto.toEntity(username, uploadFile);
        eventRepository.save(event);

        EventParticipation participation = EventParticipation.of(user, event.getId());
        eventParticipantRepository.save(participation);
        capFirstPageCacheTtl();
        return event.getId();
    }

    @Transactional
    public void editEvent(Long eventId, EventUpdateRequestDto request, String username) throws IOException {
        Event event = eventRepository.findByIdAndAuthor(eventId, username)
                .orElseThrow(() -> new NotFoundException(EVENT_ID, EVENT_NOT_FOUND));

        UploadFile uploadFile = awsS3Util.uploadFile(request.getFile(), ImageType.PUBLIC);

        List<EventParticipation> participants = eventParticipantRepository.findByEventIdIn(List.of(event.getId()));
        event.modify(
                request.getName(),
                request.getCapacity(),
                request.getContent(),
                request.getCategory(),
                uploadFile,
                participants.size()
        );
        capFirstPageCacheTtl();
    }

    public EventParticipantsInfo getEventParticipants(Long eventId) {
        List<EventParticipantQueryDto> eventParticipants = eventQueryRepository.getEventParticipants(eventId);
        return EventParticipantsInfo.of(eventParticipants, eventParticipants.size());
    }

    public EventWithRegularInfo getEventWithRegularInfo(Long eventId, Long userId, String username) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(EVENT_ID, EVENT_NOT_FOUND));

        List<EventParticipation> participants = eventParticipantRepository.findByEventId(event.getId());

        BookmarkStatus bookmarkStatus = bookmarkRepository.findBookmark(userId, eventId, BOOKMARK)
                .map(Bookmark::getStatus)
                .orElse(NOT_BOOKMARK);

        List<RegularEvent> regularEvents = regularEventRepository.findByEventId(event.getId());
        List<RegularEventParticipation> regularParticipants = findRegularParticipantsFrom(regularEvents);

        Map<Long, List<RegularEventParticipation>> groupedByRegularIdMap = groupedByRegularEventId(regularParticipants);
        return EventWithRegularInfo.of(event, participants, regularEvents, groupedByRegularIdMap, userId, username, bookmarkStatus);
    }

    public EventsPagingResponse getEventsPaging(Pageable pageable, Long userId) {
        if (pageable.getPageNumber() != 0) {
            return fetchEventsPagingFromDb(pageable, userId);
        }

        Cache cache = cacheManager.getCache("eventFirstPage");
        Cache.ValueWrapper cached = cache.get("page0");
        Long remainingTtlMs = redisTemplate.getExpire(FIRST_PAGE_CACHE_KEY, TimeUnit.MILLISECONDS);

        if (cached != null && isCacheAlive(remainingTtlMs)) {
            if (isNotPerTarget(remainingTtlMs)) {
                return (EventsPagingResponse) cached.get();
            }
            redisTemplate.expire(FIRST_PAGE_CACHE_KEY, 1, TimeUnit.MINUTES);
        }

        EventsPagingResponse result = fetchEventsPagingFromDb(pageable, userId);
        cache.put("page0", result);
        return result;
    }

    @Transactional
    public void deleteEvent(Long eventId, String username) {
        Event event = eventRepository.findByIdAndAuthor(eventId, username).orElseThrow(
                () -> new NotFoundException(EVENT_ID, EVENT_NOT_FOUND));

        eventRepository.delete(event);
        capFirstPageCacheTtl();
    }

    public BookmarkedEventsPagingInfo getEventsBookmarkedByUser(Pageable pageable, Long userId) {
        List<BookmarkedEventSimpleInfoQuery> bookmarkedEvents = eventQueryRepository.findBookmarkedEventsFromUser(userId, pageable);

        List<Long> eventIds = getEventIds(bookmarkedEvents);
        Map<Long, Long> eventParticipantCountByEventId = eventQueryRepository.findEventParticipantCountByEventIds(eventIds);
        List<BookmarkedEventSimpleInfoQuery> result = setApplicants(eventParticipantCountByEventId, bookmarkedEvents);

        return BookmarkedEventsPagingInfo.of(result);
    }

    public ParticipatingEventsPagingInfo getEventsParticipatedInUser(Pageable pageable, Long userId) {
        List<Long> eventIds = eventQueryRepository.getEventIdsParticipatedByUser(userId, pageable);
        List<ParticipatingEventSimpleInfoQuery> queryResponse = eventQueryRepository.findEventsParticipatedByUserWithApplicants(eventIds);

        List<Long> participatedEventIds = getEventIds(queryResponse);
        List<Long> bookmarkedEventIds = eventQueryRepository.getBookmarkedEventIdSet(userId, participatedEventIds);

        List<ParticipatingEventSimpleInfoQuery> bookmarkedResponse = updateBookmarkStatus(queryResponse, bookmarkedEventIds);
        return ParticipatingEventsPagingInfo.from(bookmarkedResponse);
    }

    public EventsSearchResponse searchEvents(Pageable pageable, EventSearchCondition condition, Long userId) {
        Slice<Event> searchEvents = eventQueryRepository.search(pageable, condition.getContent(), condition.getCategory());
        List<Event> targetEvents = searchEvents.getContent();
        List<Long> eventIds = getEventsId(targetEvents);

        List<EventParticipation> participants = eventParticipantRepository.findByEventIdIn(eventIds);
        Map<Long, List<EventParticipation>> groupedByEventIdMap = participants.stream()
                .collect(Collectors.groupingBy(
                        EventParticipation::getEventId
                ));

        List<Event> eventFetchBookmarks = eventRepository.findByIdInFetchBookmarks(eventIds);
        return EventsSearchResponse.of(targetEvents, groupedByEventIdMap, eventFetchBookmarks, userId, searchEvents.hasNext());
    }

    public int searchEventsCount(EventSearchCondition condition) {
        return eventQueryRepository.searchCount(condition.getContent(), condition.getCategory());
    }

    private boolean isCacheAlive(Long remainingTtlMs) {
        return remainingTtlMs != null && remainingTtlMs > 0;
    }

    /**
     * 모임 생성/수정/삭제 시 첫 페이지 캐시를 완전 삭제하지 않고 남은 TTL을 짧게 줄인다(cap).
     * 키를 남겨 두어 PER이 동작할 값을 유지하므로, 하드 미스로 인한 스탬피드 없이 PER이 cap 구간
     * 안에서 한 번 부드럽게 재계산하도록 유도한다. 트랜잭션 커밋 이후 실행해, 다른 스레드의 PER
     * 재계산이 커밋 전 옛 데이터를 fresh로 다시 캐싱하는 것을 막는다.
     */
    private void capFirstPageCacheTtl() {
        Runnable cap = () -> {
            Long remainingTtlSeconds = redisTemplate.getExpire(FIRST_PAGE_CACHE_KEY, TimeUnit.SECONDS);
            if (remainingTtlSeconds != null && remainingTtlSeconds > INVALIDATION_CAP_SECONDS) {
                redisTemplate.expire(FIRST_PAGE_CACHE_KEY, INVALIDATION_CAP_SECONDS, TimeUnit.SECONDS);
            }
        };

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    cap.run();
                }
            });
        } else {
            cap.run();
        }
    }

    private boolean isNotPerTarget(Long remainingTtlMs) {
        double beta = 1.0;
        double deltaMs = 7000.0;
        return Math.random() >= Math.exp(-(double) remainingTtlMs / (deltaMs * beta));
    }

    private EventsPagingResponse fetchEventsPagingFromDb(Pageable pageable, Long userId) {
        Slice<Event> eventsToLookFor = eventRepository.findEventsBy(pageable);

        List<Event> content = eventsToLookFor.getContent();
        Events events = Events.of(content);
        List<Long> eventIds = events.extractIds();

        List<EventParticipation> participatedEvent = eventParticipantRepository.findEventParticipationByEventIdIn(eventIds);
        Participants participants = Participants.of(participatedEvent);
        Map<Long, Long> participantsCountByGroupId = participants.findNumberOfParticipants();

        Set<Long> userBookmarkedEventIds = eventRepository.findEventIdsBookmarkedByUser(userId, eventIds, BOOKMARK);
        List<EventPagingInfo> result = createEventSimpleInfo(content, participantsCountByGroupId, userBookmarkedEventIds);

        return EventsPagingResponse.of(result, eventsToLookFor.isLast());
    }

    private List<Long> getEventsId(List<Event> events) {
        return events.stream()
                .map(Event::getId)
                .toList();
    }

    private <T extends EventIdProvider> List<Long> getEventIds(List<T> events) {
        return events.stream()
                .map(EventIdProvider::getEventId)
                .toList();
    }

    private List<RegularEventParticipation> findRegularParticipantsFrom(List<RegularEvent> regularEvents) {
        List<Long> regularEventIds = regularEvents.stream()
                .map(RegularEvent::getId)
                .toList();
        return regularEventParticipationRepository.findByRegularEventIdIn(regularEventIds);
    }

    private Map<Long, List<RegularEventParticipation>> groupedByRegularEventId(List<RegularEventParticipation> regularParticipants) {
        return regularParticipants.stream()
                .collect(Collectors.groupingBy(
                        RegularEventParticipation::getRegularEventId
                ));
    }

    private List<ParticipatingEventSimpleInfoQuery> updateBookmarkStatus(List<ParticipatingEventSimpleInfoQuery> events,
                                                                         List<Long> bookmarkedEventIdSet) {
        return events.stream()
                .map(dto ->
                        ParticipatingEventSimpleInfoQuery.from(dto, bookmarkedEventIdSet.contains(dto.getId()) ? BOOKMARK : NOT_BOOKMARK))
                .toList();
    }

    private List<EventPagingInfo> createEventSimpleInfo(List<Event> events,
                                                        Map<Long, Long> eventCountMap, Set<Long> bookmarkedEventIds) {
        return events.stream()
                .map(event -> EventPagingInfo
                        .of(event, eventCountMap.getOrDefault(event.getId(), 0L),
                                bookmarkedEventIds.contains(event.getId()) ? BOOKMARK : NOT_BOOKMARK))
                .toList();
    }

    private List<BookmarkedEventSimpleInfoQuery> setApplicants(Map<Long, Long> eventParticipantsCountByEventId,
                                                               List<BookmarkedEventSimpleInfoQuery> bookmarkList) {
        return bookmarkList.stream()
                .map(dto ->
                        BookmarkedEventSimpleInfoQuery.updateApplicants(dto, eventParticipantsCountByEventId.getOrDefault(dto.getId(), 0L)))
                .toList();
    }
}