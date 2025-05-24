package lems.cowshed.service;

import jakarta.persistence.EntityManager;
import lems.cowshed.IntegrationTestSupport;
import lems.cowshed.dto.regular.event.request.RegularEventEditRequest;
import lems.cowshed.dto.regular.event.request.RegularEventSaveRequest;
import lems.cowshed.dto.regular.event.response.RegularEventPagingInfo;
import lems.cowshed.dto.regular.event.response.RegularEventSimpleInfo;
import lems.cowshed.domain.event.Event;
import lems.cowshed.repository.event.EventRepository;
import lems.cowshed.domain.regular.event.RegularEvent;
import lems.cowshed.repository.regular.event.RegularEventRepository;
import lems.cowshed.domain.regular.event.participation.RegularEventParticipation;
import lems.cowshed.repository.regular.event.participation.RegularEventParticipationRepository;
import lems.cowshed.domain.user.Mbti;
import lems.cowshed.domain.user.User;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegularEventServiceTest extends IntegrationTestSupport {

    @Autowired
    private RegularEventService regularEventService;

    @Autowired
    private EventRepository eventRepository;

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
                .dateTime(LocalDateTime.of(2025, 5, 2,12 ,0,0))
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
                .dateTime(LocalDateTime.of(2025, 5, 2,12 ,0,0))
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
        em.flush(); em.clear();

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
        em.flush(); em.clear();

        //then
        assertThatThrownBy(() -> regularEventRepository.findById(deletedId).orElseThrow())
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("정기 모임을 페이징 조회 한다.")
    @Test
    void findPagingInfo() {
        //given
        Event event = createEvent("테스터", "테스트 모임");
        eventRepository.save(event);

        Long myUserId = 1L;
        Long differUserId = 2L;

        RegularEvent regularEvent = createRegularEvent(myUserId, event, "정기 모임", "장소");
        regularEventRepository.save(regularEvent);

        RegularEvent regularEvent2 = createRegularEvent(differUserId, event, "정기 모임2", "장소2");
        RegularEventParticipation participation = createParticipation(differUserId);
        participation.connectRegularEvent(regularEvent2);

        RegularEventParticipation participation2 = createParticipation(myUserId);
        participation2.connectRegularEvent(regularEvent2);
        regularEventRepository.save(regularEvent2);

        //when
        PageRequest pageRequest = PageRequest.of(0, 2);
        RegularEventPagingInfo pagingInfo = regularEventService.findPagingInfo(event.getId(), pageRequest, myUserId);

        //then
        assertThat(pagingInfo.getRegularEventInfos())
                .extracting( "name", "location", "participantsCount", "participationId", "isRegistrant")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("정기 모임", "장소", 0, null, true),
                        Tuple.tuple("정기 모임2", "장소2", 2, participation2.getId(), false)
                );
    }

    private RegularEventParticipation createParticipation(Long userId){
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

    private RegularEvent createRegularEvent(Long userId, Event event){
        return RegularEvent.builder()
                .name("정기 모임")
                .event(event)
                .dateTime(LocalDateTime.of(2025, 5, 2,12 ,0,0))
                .location("테스트 장소")
                .capacity(50)
                .userId(userId)
                .build();
    }

    private RegularEvent createRegularEvent(Long userId, Event event, String name, String location){
        return RegularEvent.builder()
                .name(name)
                .event(event)
                .dateTime(LocalDateTime.of(2025, 5, 2,12 ,0,0))
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