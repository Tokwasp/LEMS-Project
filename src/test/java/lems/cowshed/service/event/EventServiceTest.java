package lems.cowshed.service.event;

import lems.cowshed.IntegrationTestSupport;
import lems.cowshed.domain.event.Category;
import lems.cowshed.dto.event.request.EventSaveRequestDto;
import lems.cowshed.dto.event.request.EventSearchCondition;
import lems.cowshed.dto.event.request.EventUpdateRequestDto;
import lems.cowshed.dto.event.response.*;
import lems.cowshed.dto.regular.event.response.RegularEventInfo;
import lems.cowshed.domain.bookmark.Bookmark;
import lems.cowshed.repository.bookmark.BookmarkRepository;
import lems.cowshed.domain.event.Event;
import lems.cowshed.repository.event.EventRepository;
import lems.cowshed.domain.event.participation.EventParticipation;
import lems.cowshed.domain.regular.event.RegularEvent;
import lems.cowshed.repository.regular.event.RegularEventRepository;
import lems.cowshed.domain.regular.event.participation.RegularEventParticipation;
import lems.cowshed.repository.regular.event.participation.RegularEventParticipationRepository;
import lems.cowshed.domain.user.Mbti;
import lems.cowshed.domain.user.User;
import lems.cowshed.repository.user.UserRepository;
import lems.cowshed.repository.event.participation.EventParticipantRepository;
import lems.cowshed.global.exception.BusinessException;
import lems.cowshed.global.exception.NotFoundException;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import static lems.cowshed.domain.bookmark.BookmarkStatus.*;
import static lems.cowshed.domain.user.Mbti.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class EventServiceTest extends IntegrationTestSupport {

    @Autowired
    EventService eventService;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    EventParticipationService eventParticipationService;

    @Autowired
    RegularEventRepository regularEventRepository;

    @Autowired
    RegularEventParticipationRepository regularEventParticipationRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EventParticipantRepository eventParticipantRepository;

    @Autowired
    BookmarkRepository bookmarkRepository;

    @BeforeEach
    void cleanUp() {
        eventParticipantRepository.deleteAllInBatch();
        bookmarkRepository.deleteAllInBatch();
        eventRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("모임을 등록 한다.")
    @Test
    void saveEvent() throws IOException {
        //given
        String username = "테스터";
        User user = createUser(username, "testEmail");
        userRepository.save(user);
        EventSaveRequestDto request = createRequestDto("자전거 모임", "서울", 10);

        //when
        eventService.saveEvent(request, username);

        //then
        Event findEvent = eventRepository.findByName("자전거 모임");
        assertThat(findEvent).extracting("name", "capacity")
                .containsExactly("자전거 모임", 10);
    }

    @DisplayName("모임을 등록할때 만든 회원은 모임에 참여 한다.")
    @Test
    void saveEvent_ThenParticipateMySelf() throws IOException {
        //given
        User user = createUser("테스터", "testEmail");
        userRepository.save(user);
        EventSaveRequestDto request = createRequestDto("자전거 모임", "서울", 10);

        //when
        eventService.saveEvent(request, "테스터");

        //then
        List<EventParticipation> eventParticipants = eventParticipantRepository.findAll();
        assertThat(eventParticipants).hasSize(1);
    }

    @DisplayName("모임을 등록할때 업로드 파일이 없다면 업로드 파일은 null이다.")
    @Test
    void saveEvent_whenNotRegisterFile_UploadFileIsNull() throws IOException {
        //given
        String username = "테스터";
        User user = createUser(username, "testEmail");
        userRepository.save(user);
        EventSaveRequestDto request = createRequestDto("자전거 모임", "서울", 10);

        //when
        eventService.saveEvent(request, username);

        //then
        Event findEvent = eventRepository.findByName("자전거 모임");
        assertThat(findEvent.getUploadFile()).isNull();
    }

    @DisplayName("모임을 조회 한다.")
    @Test
    void getEvent() {
        //given
        String author = "테스터";
        Event event = createEvent(author, "자전거 모임");
        eventRepository.save(event);

        //when
        EventInfo eventInfo = eventService.getEvent(event.getId(), author);

        //then
        assertThat(eventInfo.getName()).isEqualTo("자전거 모임");
    }

    @DisplayName("모임을 조회할 때 자신이 만든 모임이 아니라면 예외가 발생 한다.")
    @Test
    void getEvent_WhenNotAuthor_ThenThrowsException() {
        //given
        String author = "테스터";
        Event event = createEvent(author, "자전거 모임");
        eventRepository.save(event);

        //when //then
        assertThatThrownBy(() -> eventService.getEvent(event.getId(), "비등록자"))
                .isInstanceOf(BusinessException.class);
    }

    @DisplayName("모임과 정기모임을 함께 조회할 때 모임에 참여/북마크 하지 않았다면 결과 모임의 상태는 북마크x / 참여x 이다.")
    @Test
    void getEventWithRegularInfo_WhenNoParticipationNoBookmark_ThenStatusCheck() {
        //given
        User user = createUser("테스터", "testEmail");
        userRepository.save(user);

        Event event = createEvent("테스터", "자전거 모임");
        eventRepository.save(event);

        //when
        EventWithRegularInfo response = eventService.getEventWithRegularInfo(event.getId(), user.getId(), user.getUsername());

        //then
        assertThat(response).extracting("name", "bookmarkStatus", "isParticipated")
                .containsExactly("자전거 모임", NOT_BOOKMARK, false);
    }

    @DisplayName("모임과 정기모임을 함께 조회할 때 모임에 참여 했다면 결과 모임의 상태는 참여 이다.")
    @Test
    void getEventWithRegularInfo_WhenParticipatedEvent_ThenIsParticipatedIsTrue() {
        //given
        User user = createUser("테스터", "testEmail");
        userRepository.save(user);

        Event event = createEvent("테스터", "자전거 모임");
        eventRepository.save(event);

        EventParticipation eventParticipation = EventParticipation.of(user, event.getId());
        eventParticipantRepository.save(eventParticipation);

        //when
        EventWithRegularInfo response = eventService.getEventWithRegularInfo(event.getId(), user.getId(), user.getUsername());

        //then
        assertThat(response).extracting("bookmarkStatus", "isParticipated")
                .containsExactly(NOT_BOOKMARK, true);
    }

    @DisplayName("모임과 정기모임을 함께 조회할 때 모임에 북마크를 했다면 북마크의 상태는 BOOKMARK 이다.")
    @Test
    void getEventWithRegularInfo_WhenBookmarkedEvent_ThenBookmarkStatusIsBookmark() {
        //given
        User user = createUser("테스터", "testEmail");
        userRepository.save(user);

        Event event = createEvent("테스터", "자전거 모임");
        eventRepository.save(event);

        Bookmark bookmark = createBookmark(user.getId());
        bookmark.connectEvent(event);
        bookmarkRepository.save(bookmark);

        //when
        EventWithRegularInfo result = eventService.getEventWithRegularInfo(event.getId(), user.getId(), user.getUsername());

        //then
        assertThat(result).isNotNull()
                .extracting("bookmarkStatus", "isParticipated")
                .containsExactly(BOOKMARK, false);
    }

    @DisplayName("모임과 정기모임을 함께 조회할 때 내가 만든 모임이라면 모임 등록자가 맞다.")
    @Test
    void getEventWithRegularInfo_WhenIsEventRegistrant_ThenIsEventRegistrantIsTrue() {
        //given
        User user = createUser("테스터", "testEmail");
        userRepository.save(user);

        Event event = createEvent("테스터", "자전거 모임");
        eventRepository.save(event);

        //when
        EventWithRegularInfo result = eventService.getEventWithRegularInfo(event.getId(), user.getId(), user.getUsername());

        //then
        assertThat(result.isEventRegistrant()).isTrue();
    }

    @DisplayName("모임과 정기모임을 함께 조회할 때 모임에 두명이 참여 한다면 참여 인원수는 두명이다.")
    @Test
    void getEventWithRegularInfo_WhenTwoParticipants_ThenCountIsTwo() {
        //given
        User user = createUser("테스터", "testOne");
        User user2 = createUser("테스터2", "testTwo");
        userRepository.saveAll(List.of(user, user2));

        Event event = createEvent("테스터", "자전거 모임");
        eventRepository.save(event);

        EventParticipation eventParticipation = EventParticipation.of(user, event.getId());
        EventParticipation eventParticipation2 = EventParticipation.of(user2, event.getId());
        eventParticipantRepository.saveAll(List.of(eventParticipation, eventParticipation2));

        //when
        EventWithRegularInfo result = eventService.getEventWithRegularInfo(event.getId(), user.getId(), user.getUsername());

        //then
        assertThat(result.getApplicants()).isEqualTo(2);
    }

    @DisplayName("모임과 정기모임을 함께 조회할 때 정기모임에 참여 하지 않았다면 결과 정기 모임 참여 id는 null 이다.")
    @Test
    void getEventWithRegularInfo_WhenNotParticipatedRegularEvent_ThenReturnNotParticipated() {
        //given
        User user = createUser("테스터", "testEmail");
        userRepository.save(user);

        Event event = createEvent("테스터", "자전거 모임");
        eventRepository.save(event);

        RegularEvent regularEvent = createRegularEvent(event, "정기 모임", "테스트 장소", user.getId());
        regularEventRepository.save(regularEvent);

        //when
        EventWithRegularInfo response = eventService.getEventWithRegularInfo(event.getId(), user.getId(), user.getUsername());

        //then
        assertThat(response.getRegularEvents())
                .extracting("name", "participationId")
                .containsExactly(Tuple.tuple("정기 모임", null));
    }

    @DisplayName("모임과 정기모임을 함께 조회할 때 정기모임에 참여 했다면 결과 정기 모임 참여 id를 확인 한다.")
    @Test
    void getEventWithRegularInfo_WhenParticipatedRegularEvent_ThenReturnParticipated() {
        //given
        User user = createUser("테스터", "testEmail");
        userRepository.save(user);

        Event event = createEvent("테스터", "자전거 모임");
        eventRepository.save(event);

        RegularEvent regularEvent = createRegularEvent(event, "정기 모임", "테스트 장소", user.getId());
        regularEventRepository.save(regularEvent);

        RegularEventParticipation regularParticipation = createRegularParticipation(user.getId(), regularEvent);
        regularEventParticipationRepository.save(regularParticipation);

        //when
        EventWithRegularInfo response = eventService.getEventWithRegularInfo(event.getId(), user.getId(), user.getUsername());

        //then
        assertThat(response.getRegularEvents())
                .extracting("name", "participationId")
                .containsExactly(Tuple.tuple("정기 모임", regularParticipation.getId()));
    }

    @DisplayName("모임과 정기모임을 함께 조회할 때 정기모임에 참석 했다면 정기 모임 참여 인원수는 1명 이다.")
    @Test
    void getEventWithRegularInfo_WhenTwoParticipatedRegularEvent_ThenCountIsTwo() {
        //given
        User user = createUser("테스터", "testEmail");
        userRepository.save(user);

        Event event = createEvent("테스터", "자전거 모임");
        eventRepository.save(event);

        RegularEvent regularEvent = createRegularEvent(event, "참석 정기 모임", "참석 장소", user.getId());
        RegularEvent regularEvent2 = createRegularEvent(event, "비참석 정기 모임", "비참석 장소", user.getId());
        regularEventRepository.saveAll(List.of(regularEvent, regularEvent2));

        RegularEventParticipation regularParticipation = createRegularParticipation(user.getId(), regularEvent);
        regularEventParticipationRepository.save(regularParticipation);

        //when
        EventWithRegularInfo response = eventService.getEventWithRegularInfo(event.getId(), user.getId(), user.getUsername());

        //then
        List<RegularEventInfo> regularEvents = response.getRegularEvents();
        assertThat(regularEvents).hasSize(2)
                .extracting("name", "applicants")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("참석 정기 모임", 1),
                        Tuple.tuple("비참석 정기 모임", 0)
                );
    }

    @DisplayName("모임과 정기모임을 함께 조회할 때 내가 만든 정기 모임인지 확인 한다.")
    @Test
    void getEventWithRegularInfo_WhenIsRegularEventRegistrant_ThenTure() {
        //given
        User user = createUser("정기 모임 생성자", "RegularEventCreator");
        userRepository.save(user);

        Event event = createEvent(user.getUsername(), "자전거 모임");
        eventRepository.save(event);

        RegularEvent regularEvent = createRegularEvent(event, "참석 정기 모임", "참석 장소", user.getId());
        RegularEvent regularEvent2 = createRegularEvent(event, "비참석 정기 모임", "비참석 장소", 2L);
        regularEventRepository.saveAll(List.of(regularEvent, regularEvent2));

        //when
        EventWithRegularInfo result = eventService.getEventWithRegularInfo(event.getId(), user.getId(), user.getUsername());

        //then
        List<RegularEventInfo> regularEvents = result.getRegularEvents();
        assertThat(regularEvents)
                .extracting("isRegularRegistrant")
                .containsExactlyInAnyOrder(
                        true,false
                );
    }

    @DisplayName("두명의 회원이 하나의 모임에 참여 할때 모임의 참여자 수는 두명이다.")
    @Test
    void getEventsPagingWithApplicantsWithRegular() {
        //given
        Event event = createEvent("테스터", "테스트 모임", 2);
        eventRepository.save(event);

        int userCount = 2;

        for(int i = 0; i < userCount; i++){
            User user = createUser("테스터", "testEmail");
            userRepository.save(user);
            eventParticipationService.saveEventParticipation(event.getId(), user.getId());
        }

        Pageable pageable = PageRequest.of(0, 1);

        //when
        EventsPagingResponse result = eventService.getEventsPaging(pageable, 0L);

        //then
        assertThat(result.getContent()).hasSize(1)
                .extracting("applicants")
                .containsExactly(2L);
    }

    @DisplayName("모임에 참여한 회원들을 조회 할때 두명의 회원이 참여한 경우 모임의 참여 인원수는 2명 이다.")
    @Test
    void getEventParticipants_WhenTwoUsersParticipate_ThenCountIsTwo() {
        //given
        User user = createUser("테스터", INTP);
        User user2 = createUser("테스터2", ISTP);
        userRepository.saveAll(List.of(user, user2));

        Event event = createEvent("산책 모임", "테스터");
        eventRepository.save(event);

        EventParticipation eventParticipation = EventParticipation.of(user, event.getId());
        EventParticipation eventParticipation2 = EventParticipation.of(user2, event.getId());
        eventParticipantRepository.saveAll(List.of(eventParticipation, eventParticipation2));

        //when
        EventParticipantsInfo participantsInfo = eventService.getEventParticipants(event.getId());

        //then
        assertThat(participantsInfo.getParticipantCount()).isEqualTo(2);
        assertThat(participantsInfo.getEventParticipants())
                .extracting("name", "mbti")
                .containsExactlyInAnyOrder(Tuple.tuple("테스터", INTP),
                        Tuple.tuple("테스터2", ISTP));
    }


    @DisplayName("페이징 정보를 받아 모임을 조회 합니다.")
    @Test
    void getEventsPagingWithRegular() {
        //given
        User user = createUser("테스터", "testEmail");
        userRepository.save(user);

        for (int i = 0; i < 10; i++) {
            Event event = createEvent("테스터" + i, "자전거 모임");
            eventRepository.save(event);
        }

        Pageable pageable = PageRequest.of(1, 3);

        //when
        EventsPagingResponse result = eventService.getEventsPaging(pageable, user.getId());

        //then
        assertThat(result.getContent())
                .extracting("author")
                .containsExactlyInAnyOrder("테스터3", "테스터4", "테스터5");
    }

    @DisplayName("모임에 참여한 회원이 없을 경우 참여자 수는 0명이다.")
    @Test
    void getEventsPagingWhenNothingApplicantsWithRegular() {
        //given
        Event event = createEvent("테스터", "테스트 모임", 2);
        eventRepository.save(event);

        Pageable pageable = PageRequest.of(0, 1);

        //when
        EventsPagingResponse result = eventService.getEventsPaging(pageable, 0L);

        //then
        assertThat(result.getContent()).hasSize(1)
                .extracting("applicants")
                .containsExactly(0L);
    }

    @DisplayName("모임을 수정 한다.")
    @Test
    void editEvent() throws IOException {
        //given
        Event event = createEvent("테스터", "자전거 모임", 10);
        eventRepository.save(event);

        EventUpdateRequestDto updateRequest = createUpdateRequestDto("산책 모임", 20);

        //when
        eventService.editEvent(event.getId(), updateRequest, "테스터");

        //then
        Event findEvent = eventRepository.findById(event.getId()).orElseThrow();
        assertThat(findEvent).extracting("author", "name", "capacity")
                .containsExactly("테스터", "산책 모임", 20);
    }

    @DisplayName("모임을 수정할때 모임 참여인원이 변경 최대 인원보다 많다면 예외가 발생 한다.")
    @Test
    void editEvent_WhenParticipantCountExceedsNewCapacity_ThenException() throws IOException {
        //given
        User user = createUser("회원1", "test1@naver.com");
        User user2 = createUser("회원2", "test2@naver.com");
        userRepository.saveAll(List.of(user, user2));

        Event event = createEvent("테스터", "자전거 모임", 2);
        eventRepository.save(event);

        EventParticipation eventParticipation = EventParticipation.of(user, event.getId());
        EventParticipation eventParticipation2 = EventParticipation.of(user2, event.getId());
        eventParticipantRepository.saveAll(List.of(eventParticipation, eventParticipation2));

        EventUpdateRequestDto updateRequest = createUpdateRequestDto("산책 모임", 1);

        //when //then
        assertThatThrownBy(() -> eventService.editEvent(event.getId(), updateRequest, "테스터"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("모임에 참여한 회원이 최대 인원보다 더 많습니다.");
    }

    @DisplayName("모임을 수정할 때 내가 등록한 모임이 아니라면 예외가 발생 한다.")
    @Test
    void editEventWhenNotRegisteredByMe() {
        //given
        Event event = createEvent("테스터", "자전거 모임", 10);
        eventRepository.save(event);

        EventUpdateRequestDto updateRequest = createUpdateRequestDto("산책 모임", 20);

        //when //then
        assertThatThrownBy(() -> eventService.editEvent(event.getId(), updateRequest, "등록 안한 테스터"))
                .isInstanceOf(NotFoundException.class);
    }

    @DisplayName("모임을 삭제 한다.")
    @Test
    void deleteEvent() {
        //given
        Event event = createEvent("테스터", "자전거 모임");
        eventRepository.save(event);

        //when
        eventService.deleteEvent(event.getId(), event.getAuthor());

        //then
        assertThrows(NoSuchElementException.class,
                () -> eventRepository.findById(event.getId()).orElseThrow());
    }

    @DisplayName("회원의 북마크한 모임을 페이징 조회 합니다.")
    @Test
    void getPagingBookmarkEvents() {
        //given
        String author = "테스터";
        User user = createUser(author, "test@naver.com");
        userRepository.save(user);

        int bookmarkCount = 3;
        for(int i = 0; i < bookmarkCount; i++){
            Event event = createEvent(author, "테스트" + i);
            eventRepository.save(event);

            Bookmark bookmark = createBookmark(user.getId());
            bookmark.connectEvent(event);
            bookmarkRepository.save(bookmark);
        }

        Pageable pageable = PageRequest.of(0, 3);

        //when
        BookmarkedEventsPagingInfo bookmarkEvents = eventService.getEventsBookmarkedByUser(pageable, user.getId());

        //then
        assertThat(bookmarkEvents.getBookmarks())
                .hasSize(3)
                .extracting("name")
                .containsExactlyInAnyOrder("테스트0", "테스트1", "테스트2");
    }

    @DisplayName("북마크한 모임을 페이징 조회 할 때 북마크 여부를 확인 한다.")
    @Test
    void getEventsPagingForBookmarkWithRegular() {
        //given
        User user = createUser("북마크 테스터", "test@naver.com");
        userRepository.save(user);

        Event bookmarkEvent = createEvent("테스트", "북마크 모임");
        eventRepository.save(bookmarkEvent);

        Event unBookmarkEvent = createEvent("테스트", "북마크 안한 모임");
        eventRepository.save(unBookmarkEvent);

        Bookmark bookmark = createBookmark(user.getId());
        bookmark.connectEvent(bookmarkEvent);
        bookmarkRepository.save(bookmark);
        Pageable pageable = PageRequest.of(0, 2);

        //when
        List<EventPagingInfo> result = eventService.getEventsPaging(pageable, user.getId()).getContent();

        //then
        assertThat(result)
                .extracting("bookmarkStatus")
                .containsExactlyInAnyOrder(BOOKMARK, NOT_BOOKMARK);
    }

    @DisplayName("모임을 검색할때 검색어가 없고 카테고리를 선택 하지 않으면 모임 전체중 일부를 조회 한다.")
    @Test
    void searchEvents() {
        //given
        User user = createUser("테스터", "test@naver.com");
        userRepository.save(user);

        Event event = createEvent("테스터", "모임" , "내용", Category.GAME);
        Event event2 = createEvent("테스터", "모임2", "내용2", Category.HOBBY);
        eventRepository.saveAll(List.of(event,event2));

        EventSearchCondition searchCondition = createSearchCondition(null, null);

        //when
        PageRequest pageRequest = PageRequest.of(0, 2);
        EventsSearchResponse response = eventService.searchEvents(pageRequest, searchCondition, user.getId());

        //then
        assertThat(response.getEventSearchInfos()).hasSize(2);
        assertThat(response.isHasNext()).isFalse();

        List<EventSearchInfo> eventSearchInfos = response.getEventSearchInfos();
        assertThat(eventSearchInfos)
                .extracting("name", "category", "applicants", "bookmarkStatus")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("모임", "게임", 0, NOT_BOOKMARK),
                        Tuple.tuple("모임2", "취미", 0, NOT_BOOKMARK)
                );
    }

    @DisplayName("모임 이름 혹은 내용에 검색어가 있는 모임을 조회 한다.")
    @Test
    void searchEvents_WhenKeywordExist() {
        //given
        User user = createUser("테스터", "test@naver.com");
        userRepository.save(user);

        String searchKeyword = "검색";
        Event event = createEvent("테스터", "모임 " + searchKeyword, "내용", Category.GAME);
        Event event2 = createEvent("테스터", "모임", "내용", Category.HOBBY);
        eventRepository.saveAll(List.of(event,event2));

        EventSearchCondition searchCondition = createSearchCondition(searchKeyword, null);

        //when
        PageRequest pageRequest = PageRequest.of(0, 2);
        EventsSearchResponse response = eventService.searchEvents(pageRequest, searchCondition, user.getId());

        //then
        assertThat(response.getEventSearchInfos()).hasSize(1);
        assertThat(response.isHasNext()).isFalse();

        List<EventSearchInfo> eventSearchInfos = response.getEventSearchInfos();
        assertThat(eventSearchInfos)
                .extracting("name", "category", "applicants", "bookmarkStatus")
                .containsExactly(Tuple.tuple("모임 검색", "게임", 0, NOT_BOOKMARK));
    }

    @DisplayName("카테고리를 통해 모임을 조회 한다.")
    @Test
    void searchEvents_WhenCategoryExist() {
        //given
        User user = createUser("테스터", "test@naver.com");
        userRepository.save(user);

        Category searchCategory = Category.HOBBY;
        Event event = createEvent("테스터", "모임", "내용", Category.GAME);
        Event event2 = createEvent("테스터", "모임2", "내용2", Category.HOBBY);
        eventRepository.saveAll(List.of(event,event2));

        EventSearchCondition searchCondition = createSearchCondition(null, searchCategory);

        //when
        PageRequest pageRequest = PageRequest.of(0, 2);
        EventsSearchResponse response = eventService.searchEvents(pageRequest, searchCondition, user.getId());

        //then
        assertThat(response.getEventSearchInfos()).hasSize(1);
        assertThat(response.isHasNext()).isFalse();

        List<EventSearchInfo> eventSearchInfos = response.getEventSearchInfos();
        assertThat(eventSearchInfos)
                .extracting("name", "category", "applicants", "bookmarkStatus")
                .containsExactly(Tuple.tuple("모임2", "취미", 0, NOT_BOOKMARK));
    }

    @DisplayName("모임 이름 혹은 내용에 검색어가 포함되고 카테고리도 같은 모임을 조회 한다.")
    @Test
    void searchEvents_WhenKeywordAndCategoryExist() {
        //given
        User user = createUser("테스터", "test@naver.com");
        userRepository.save(user);

        String searchKeyword = "검색";
        Category searchCategory = Category.PET;
        Event event = createEvent("테스터", "모임 " + searchKeyword, "내용", Category.GAME);

        Event event2 = createEvent("테스터", "모임2" , "내용 " + searchKeyword, searchCategory);
        Event event3 = createEvent("테스터", "모임3", "내용", searchCategory);
        eventRepository.saveAll(List.of(event,event2, event3));

        EventSearchCondition searchCondition = createSearchCondition(searchKeyword, searchCategory);

        //when
        PageRequest pageRequest = PageRequest.of(0, 2);
        EventsSearchResponse response = eventService.searchEvents(pageRequest, searchCondition, user.getId());

        //then
        assertThat(response.getEventSearchInfos()).hasSize(1);
        assertThat(response.isHasNext()).isFalse();

        List<EventSearchInfo> eventSearchInfos = response.getEventSearchInfos();
        assertThat(eventSearchInfos)
                .extracting("name", "category", "applicants", "bookmarkStatus")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("모임2", "반려동물", 0, NOT_BOOKMARK)
                );
    }

    @DisplayName("모임을 서칭할 때 두명이 모임에 참여 했다면 참여 인원은 두명이다.")
    @Test
    void searchEvents_WhenTwoParticipants_ThenApplicantsTwo() {
        //given
        User user = createUser("테스터", "test@naver.com");
        userRepository.save(user);

        User user2 = createUser("테스터", "test@naver.com");
        userRepository.save(user2);

        String searchKeyword = "검색";
        Category searchCategory = Category.PET;
        Event event = createEvent("테스터", "모임 " + searchKeyword, "내용", searchCategory);
        eventRepository.save(event);

        EventParticipation eventParticipation = EventParticipation.of(user, event.getId());
        EventParticipation eventParticipation2 = EventParticipation.of(user2, event.getId());
        eventParticipantRepository.saveAll(List.of(eventParticipation, eventParticipation2));
        EventSearchCondition searchCondition = createSearchCondition(searchKeyword, searchCategory);

        //when
        PageRequest pageRequest = PageRequest.of(0, 2);
        EventsSearchResponse response = eventService.searchEvents(pageRequest, searchCondition, user.getId());

        //then
        assertThat(response.getEventSearchInfos()).hasSize(1);
        assertThat(response.isHasNext()).isFalse();

        List<EventSearchInfo> eventSearchInfos = response.getEventSearchInfos();
        assertThat(eventSearchInfos)
                .extracting("name", "category", "content", "applicants", "bookmarkStatus")
                .containsExactly(Tuple.tuple("모임 검색", "반려동물", "내용", 2, NOT_BOOKMARK));
    }

    @DisplayName("모임을 서칭할 때 모임에 북마크 했다면 북마크 상태는 BOOKMARK 이다.")
    @Test
    void searchEvents_WhenBookmarked_ThenStatusIsBookmark() {
        //given
        User user = createUser("테스터", "test@naver.com");
        userRepository.save(user);

        String searchKeyword = "검색";
        Category searchCategory = Category.PET;

        Event event = createEvent("테스터", "모임 " + searchKeyword, "내용", searchCategory);
        Bookmark bookmark = Bookmark.of(user.getId());
        bookmark.connectEvent(event);
        eventRepository.save(event);

        Event event2 = createEvent("테스터", "모임2 " + searchKeyword, "내용2", searchCategory);
        eventRepository.save(event2);

        EventSearchCondition searchCondition = createSearchCondition(searchKeyword, searchCategory);

        //when
        PageRequest pageRequest = PageRequest.of(0, 2);
        EventsSearchResponse response = eventService.searchEvents(pageRequest, searchCondition, user.getId());

        //then
        assertThat(response.getEventSearchInfos()).hasSize(2);
        assertThat(response.isHasNext()).isFalse();

        List<EventSearchInfo> eventSearchInfos = response.getEventSearchInfos();
        assertThat(eventSearchInfos)
                .extracting("name", "category", "content", "applicants", "bookmarkStatus")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("모임 검색", "반려동물", "내용", 0, BOOKMARK),
                        Tuple.tuple("모임2 검색", "반려동물", "내용2", 0, NOT_BOOKMARK)
                );
    }

    private static EventSearchCondition createSearchCondition(String content, Category category){
        return EventSearchCondition.builder()
                .content(content)
                .category(category)
                .build();
    }

    private static Event createEvent(String author, String name) {
        return Event.builder()
                .name(name)
                .author(author)
                .build();
    }

    private static Event createEvent(String author, String name, String content, Category category){
        return Event.builder()
                .name(name)
                .author(author)
                .content(content)
                .category(category)
                .build();
    }

    private static Event createEvent(String author, String name, int capacity) {
        return Event.builder()
                .name(name)
                .author(author)
                .capacity(capacity)
                .build();
    }

    private EventSaveRequestDto createRequestDto(String name, String content, int capacity) {
        return EventSaveRequestDto.builder()
                .name(name)
                .content(content)
                .capacity(capacity)
                .build();
    }

    private EventUpdateRequestDto createUpdateRequestDto(String name, int capacity) {
        return EventUpdateRequestDto.builder()
                .name(name)
                .capacity(capacity)
                .build();
    }

    private User createUser(String username, String email) {
        return User.builder()
                .username(username)
                .email(email)
                .build();
    }

    private User createUser(String username, Mbti mbti) {
        return User.builder()
                .username(username)
                .mbti(mbti)
                .build();
    }

    private Bookmark createBookmark(Long userId) {
        return Bookmark.builder()
                .userId(userId)
                .status(BOOKMARK)
                .build();
    }

    private RegularEvent createRegularEvent(Event event, String name, String location, Long userId){
        return RegularEvent.builder()
                .event(event)
                .name(name)
                .capacity(50)
                .location(location)
                .userId(userId)
                .build();
    }

    private RegularEventParticipation createRegularParticipation(Long userId, RegularEvent regularEvent){
        return RegularEventParticipation.of(userId, regularEvent.getId());
    }
}