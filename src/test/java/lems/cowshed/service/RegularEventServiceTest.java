package lems.cowshed.service;

import jakarta.persistence.EntityManager;
import lems.cowshed.IntegrationTestSupport;
import lems.cowshed.domain.event.Category;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.event.participation.EventParticipation;
import lems.cowshed.domain.regular.event.RegularEvent;
import lems.cowshed.domain.regular.event.participation.RegularEventParticipation;
import lems.cowshed.domain.user.Mbti;
import lems.cowshed.domain.user.User;
import lems.cowshed.dto.regular.event.request.RegularEventEditRequest;
import lems.cowshed.dto.regular.event.request.RegularEventSaveRequest;
import lems.cowshed.dto.regular.event.request.RegularSearchCondition;
import lems.cowshed.dto.regular.event.response.RegularEventPagingInfo;
import lems.cowshed.dto.regular.event.response.RegularEventSearchInfo;
import lems.cowshed.dto.regular.event.response.RegularEventSearchResponse;
import lems.cowshed.dto.regular.event.response.RegularEventSimpleInfo;
import lems.cowshed.repository.event.EventRepository;
import lems.cowshed.repository.event.participation.EventParticipantRepository;
import lems.cowshed.repository.regular.event.RegularEventRepository;
import lems.cowshed.repository.regular.event.participation.RegularEventParticipationRepository;
import lems.cowshed.repository.user.UserRepository;
import lems.cowshed.service.regular.event.RegularEventService;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static lems.cowshed.domain.user.Mbti.INTP;
import static lems.cowshed.dto.regular.event.response.RegularEventPagingInfo.RegularEventInfo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegularEventServiceTest extends IntegrationTestSupport {

    @Autowired
    private RegularEventService regularEventService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventParticipantRepository eventParticipantRepository;

    @Autowired
    private RegularEventRepository regularEventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private RegularEventParticipationRepository participationRepository;

    @DisplayName("정기 모임을 등록 한다.")
    @Test
    void saveRegularEvent() {
        //given
        Event event = createEvent("테스터", "테스트 모임");
        eventRepository.save(event);

        RegularEventSaveRequest request = RegularEventSaveRequest.builder()
                .name("정기 모임")
                .dateTime(LocalDateTime.of(2025, 5, 2, 12, 0, 0))
                .location("테스트 장소")
                .capacity(50)
                .build();

        //when
        Long regularEventId = regularEventService.saveRegularEvent(request, event.getId(), null);

        //then
        RegularEvent regularEvent = regularEventRepository.findById(regularEventId).orElseThrow();
        assertThat(regularEvent)
                .extracting("name", "location")
                .containsExactly("정기 모임", "테스트 장소");
    }

    @DisplayName("정기 모임을 만들때 만든 회원은 정기 모임에 참여 한다.")
    @Test
    void saveRegularEvent_ThenParticipateMyself() {
        //given
        User user = createUser("정기 모임 생성자", "RegularEventCreator");
        userRepository.save(user);

        Event event = createEvent("테스터", "테스트 모임");
        eventRepository.save(event);

        RegularEventSaveRequest request = RegularEventSaveRequest.builder()
                .name("정기 모임")
                .dateTime(LocalDateTime.of(2025, 5, 2, 12, 0, 0))
                .location("테스트 장소")
                .capacity(50)
                .build();

        //when
        regularEventService.saveRegularEvent(request, event.getId(), user.getId());

        //then
        List<RegularEventParticipation> participants = participationRepository.findAll();
        assertThat(participants).hasSize(1);
    }

    @DisplayName("정기 모임을 조회 합니다.")
    @Test
    void getRegularEvent() {
        //given
        User user = createUser("정기 모임 생성자", "RegularEventCreator");
        userRepository.save(user);

        Event event = createEvent("테스터", "테스트 모임");
        eventRepository.save(event);

        RegularEvent regularEvent = createRegularEvent(user.getId(), event, "정기 모임", "정기 모임 장소");
        regularEventRepository.save(regularEvent);

        //when
        RegularEventSimpleInfo result = regularEventService.getRegularEvent(regularEvent.getId());

        //then
        assertThat(result)
                .extracting("name", "location")
                .containsExactly("정기 모임", "정기 모임 장소");
    }

    @DisplayName("정기 모임을 수정 한다.")
    @Test
    void editRegularEvent() {
        //given
        User user = createUser("테스터", INTP);
        userRepository.save(user);

        Event event = createEvent(user.getUsername(), "테스트 모임");
        eventRepository.save(event);

        RegularEvent regularEvent = createRegularEvent(user.getId(), event);
        regularEventRepository.save(regularEvent);

        RegularEventEditRequest request = createRegularEditRequest("이름 변경", "장소 변경");

        //when
        regularEventService.editRegularEvent(request, regularEvent.getId());
        em.flush();
        em.clear();

        //then
        RegularEvent findRegularEvent = regularEventRepository.findById(regularEvent.getId()).orElseThrow();
        assertThat(findRegularEvent)
                .extracting("name", "location")
                .containsExactly("이름 변경", "장소 변경");
    }

    @DisplayName("정기 모임을 제거 한다.")
    @Test
    void delete() {
        //given
        User user = createUser("정기 모임 생성자", "RegularEventCreator");
        userRepository.save(user);

        Event event = createEvent("테스터", "테스트 모임");
        eventRepository.save(event);

        RegularEvent regularEvent = createRegularEvent(user.getId(), event);
        regularEventRepository.save(regularEvent);

        //when
        Long deletedId = regularEventService.delete(regularEvent.getId());
        em.flush();
        em.clear();

        //then
        assertThatThrownBy(() -> regularEventRepository.findById(deletedId).orElseThrow())
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("정기 모임을 페이징 조회할 때 정기 모임에 참여 하지 않았을때 참여 식별자는 null이다.")
    @Test
    void findPagingInfo_WhenNotParticipated_thenParticipationIdIsNull() {
        //given
        Event event = createEvent("테스터", "테스트 모임");
        eventRepository.save(event);

        Long userId = 1L;
        RegularEvent regularEvent = createRegularEvent(userId, event, "정기 모임", "장소");
        regularEventRepository.save(regularEvent);

        //when
        PageRequest request = PageRequest.of(0, 2);
        RegularEventPagingInfo result = regularEventService.findPagingInfo(event.getId(), request, userId);

        //then
        assertThat(result.isHasNext()).isFalse();
        List<RegularEventInfo> regularEventInfos = result.getRegularEventInfos();

        assertThat(regularEventInfos).hasSize(1)
                .extracting("participationId", "isRegistrant")
                .containsExactly(
                        Tuple.tuple(null, true)
                );
    }

    @DisplayName("정기모임을 페이징 조회할 때 회원이 정기 모임에 참여 했다면 참여 ID 값이 있다.")
    @Test
    void findPagingInfo_WhenParticipated_thenParticipationIdIsExist() {
        //given
        Event event = createEvent("테스터", "테스트 모임");
        eventRepository.save(event);

        Long userId = 1L;
        RegularEvent regularEvent = createRegularEvent(userId, event, "정기 모임", "장소");

        RegularEventParticipation participation = createParticipation(userId);
        participation.connectRegularEvent(regularEvent);
        regularEventRepository.save(regularEvent);

        //when
        PageRequest request = PageRequest.of(0, 2);
        RegularEventPagingInfo result = regularEventService.findPagingInfo(event.getId(), request, userId);

        //then
        assertThat(result.isHasNext()).isFalse();
        List<RegularEventInfo> regularEventInfos = result.getRegularEventInfos();

        assertThat(regularEventInfos).hasSize(1)
                .extracting("participationId", "isRegistrant")
                .containsExactly(
                        Tuple.tuple(participation.getId(), true)
                );
    }

    @DisplayName("정기모임을 페이징 조회할 때 회원이 등록 하지 않은 정기 모임이라면 정기 모임 등록 여부는 false 이다.")
    @Test
    void findPagingInfo_WhenNotRegistrant_thenRegistrantIsFalse() {
        //given
        Event event = createEvent("테스터", "테스트 모임");
        eventRepository.save(event);

        Long differentUserId = 1L;
        RegularEvent regularEvent = createRegularEvent(differentUserId, event, "정기 모임", "장소");

        RegularEventParticipation participation = createParticipation(differentUserId);
        participation.connectRegularEvent(regularEvent);
        regularEventRepository.save(regularEvent);

        //when
        Long myUserId = 2L;
        PageRequest request = PageRequest.of(0, 2);
        RegularEventPagingInfo result = regularEventService.findPagingInfo(event.getId(), request, myUserId);

        //then
        assertThat(result.isHasNext()).isFalse();
        List<RegularEventInfo> regularEventInfos = result.getRegularEventInfos();

        assertThat(regularEventInfos).hasSize(1)
                .extracting("participationId", "isRegistrant", "participantsCount")
                .containsExactly(
                        Tuple.tuple(null, false, 1)
                );
    }

    @DisplayName("정기모임을 페이징 조회할 때 두 회원이 정기 모임에 참여 했다면 참여자 수는 2명이다.")
    @Test
    void findPagingInfo_WhenTwoParticipated_ThenTwoParticipants() {
        //given
        Event event = createEvent("테스터", "테스트 모임");
        eventRepository.save(event);

        Long myUserId = 1L;
        RegularEvent regularEvent = createRegularEvent(myUserId, event, "정기 모임", "장소");
        RegularEventParticipation participation = createParticipation(myUserId);
        participation.connectRegularEvent(regularEvent);

        Long differentUserId = 2L;
        RegularEventParticipation participation2 = createParticipation(differentUserId);
        participation2.connectRegularEvent(regularEvent);
        regularEventRepository.save(regularEvent);

        //when
        PageRequest request = PageRequest.of(0, 2);
        RegularEventPagingInfo result = regularEventService.findPagingInfo(event.getId(), request, myUserId);

        //then
        assertThat(result.isHasNext()).isFalse();
        List<RegularEventInfo> regularEventInfos = result.getRegularEventInfos();

        assertThat(regularEventInfos).hasSize(1)
                .extracting("participationId", "isRegistrant", "participantsCount")
                .containsExactly(
                        Tuple.tuple(participation.getId(), true, 2)
                );
    }

    @DisplayName("정기 모임을 페이징 조회할 때 두 정기 모임이 등록된 경우를 확인 한다.")
    @Test
    void findPagingInfo() {
        //given
        Event event = createEvent("테스터", "테스트 모임");
        eventRepository.save(event);

        Long myUserId = 1L;
        RegularEvent regularEvent = createRegularEvent(myUserId, event, "정기 모임", "장소");
        RegularEventParticipation participation = createParticipation(myUserId);
        participation.connectRegularEvent(regularEvent);
        regularEventRepository.save(regularEvent);

        Long differUserId = 2L;
        RegularEvent regularEvent2 = createRegularEvent(differUserId, event, "정기 모임2", "장소2");
        RegularEventParticipation participation2 = createParticipation(differUserId);
        participation2.connectRegularEvent(regularEvent2);
        regularEventRepository.save(regularEvent2);

        //when
        PageRequest pageRequest = PageRequest.of(0, 2);
        RegularEventPagingInfo pagingInfo = regularEventService.findPagingInfo(event.getId(), pageRequest, myUserId);

        //then
        assertThat(pagingInfo.isHasNext()).isFalse();

        List<RegularEventInfo> regularEventInfos = pagingInfo.getRegularEventInfos();

        assertThat(regularEventInfos)
                .extracting("name", "location", "participantsCount", "participationId", "isRegistrant")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("정기 모임", "장소", 1, participation.getId(), true),
                        Tuple.tuple("정기 모임2", "장소2", 1, null, false)
                );
    }

    @DisplayName("정기 모임을 검색 한다.")
    @Test
    void search() {
        //given
        Event event = createEvent(Category.GAME, "테스트 모임");
        eventRepository.save(event);

        RegularEvent regularEvent = createRegularEvent(null, event, "정기 모임", "장소");
        regularEventRepository.save(regularEvent);

        String searchContent = "모임";
        RegularSearchCondition searchCondition = new RegularSearchCondition(searchContent, null);

        //when
        PageRequest pageRequest = PageRequest.of(0, 2);
        RegularEventSearchResponse response = regularEventService.search(pageRequest, searchCondition, null);

        //then
        List<RegularEventSearchInfo> searchInfos = response.getSearchInfos();
        assertThat(searchInfos).hasSize(1)
                .extracting("name", "category")
                .containsExactly(Tuple.tuple("정기 모임", "게임"));
    }

    @DisplayName("정기 모임을 검색할때 회원이 모임에 참여 했는지 여부를 확인 한다. ")
    @Test
    void search_WhenRegisterEventByUser() {
        //given
        String author = "모임 생성자";
        User user = createUser(author, "EventCreator");
        userRepository.save(user);

        Event event = createEvent(author, Category.GAME, "테스트 모임");
        eventRepository.save(event);

        EventParticipation participation = EventParticipation.of(user, event);
        eventParticipantRepository.save(participation);

        RegularEvent regularEvent = createRegularEvent(user.getId(), event, "정기 모임", "장소");
        regularEventRepository.save(regularEvent);

        RegularSearchCondition searchCondition = new RegularSearchCondition(null, null);

        //when
        PageRequest pageRequest = PageRequest.of(0, 2);
        RegularEventSearchResponse response = regularEventService.search(pageRequest, searchCondition, user.getId());

        //then
        List<RegularEventSearchInfo> searchInfos = response.getSearchInfos();
        System.out.println(searchInfos);
        assertThat(searchInfos).hasSize(1)
                .extracting("name", "category", "isEventParticipated")
                .containsExactly(Tuple.tuple("정기 모임", "게임", true));
    }

    private RegularEventParticipation createParticipation(Long userId) {
        return RegularEventParticipation.builder()
                .userId(userId)
                .build();
    }

    private RegularEventEditRequest createRegularEditRequest(String name, String location) {
        return RegularEventEditRequest.builder()
                .name(name)
                .location(location)
                .dateTime(LocalDateTime.of(2025, 5, 1, 12, 0, 0))
                .capacity(50)
                .build();
    }

    private Event createEvent(String author, String name) {
        return Event.builder()
                .name(name)
                .author(author)
                .build();
    }

    private Event createEvent(Category category, String name) {
        return Event.builder()
                .name(name)
                .category(category)
                .build();
    }

    private Event createEvent(String author, Category category, String name) {
        return Event.builder()
                .name(name)
                .author(author)
                .category(category)
                .build();
    }

    private RegularEvent createRegularEvent(Long userId, Event event) {
        return RegularEvent.builder()
                .name("정기 모임")
                .event(event)
                .dateTime(LocalDateTime.of(2025, 5, 2, 12, 0, 0))
                .location("테스트 장소")
                .capacity(50)
                .userId(userId)
                .build();
    }

    private RegularEvent createRegularEvent(Long userId, Event event, String name, String location) {
        return RegularEvent.builder()
                .name(name)
                .event(event)
                .dateTime(LocalDateTime.of(2025, 5, 2, 12, 0, 0))
                .location(location)
                .capacity(50)
                .userId(userId)
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
}