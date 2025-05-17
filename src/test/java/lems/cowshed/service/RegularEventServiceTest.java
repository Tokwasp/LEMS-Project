package lems.cowshed.service;

import jakarta.persistence.EntityManager;
import lems.cowshed.IntegrationTestSupport;
import lems.cowshed.api.controller.dto.regular.event.request.RegularEventEditRequest;
import lems.cowshed.api.controller.dto.regular.event.request.RegularEventSaveRequest;
import lems.cowshed.api.controller.dto.regular.event.response.RegularEventSimpleInfo;
import lems.cowshed.api.controller.dto.regular.event.response.RegularParticipantsInfo;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.event.EventRepository;
import lems.cowshed.domain.regular.event.RegularEvent;
import lems.cowshed.domain.regular.event.RegularEventRepository;
import lems.cowshed.domain.regular.event.participation.RegularEventParticipation;
import lems.cowshed.domain.regular.event.participation.RegularEventParticipationRepository;
import lems.cowshed.domain.user.Mbti;
import lems.cowshed.domain.user.User;
import lems.cowshed.domain.user.UserRepository;
import lems.cowshed.service.regular.event.RegularEventService;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static lems.cowshed.domain.user.Mbti.INTP;
import static lems.cowshed.domain.user.Mbti.ISTP;
import static org.assertj.core.api.Assertions.*;

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

        RegularEvent regularEvent = createRegularEvent(user, event, "정기 모임", "정기 모임 장소");
        regularEventRepository.save(regularEvent);

        //when
        RegularEventSimpleInfo result = regularEventService.getRegularEvent(regularEvent.getId());

        //then
        assertThat(result)
                .extracting("name", "location")
                .containsExactly("정기 모임", "정기 모임 장소");
    }

    @DisplayName("정기 모임 참석을 해제 한다.")
    @Test
    void deleteByParticipationIdAndUserId() {
        //given
        User user = createUser("정기 모임 생성자", "RegularEventCreator");
        userRepository.save(user);

        Event event = createEvent("테스터", "테스트 모임");
        eventRepository.save(event);

        RegularEvent regularEvent = createRegularEvent(user, event);
        regularEventRepository.save(regularEvent);

        RegularEventParticipation participation = RegularEventParticipation.of(user, regularEvent);
        participationRepository.save(participation);

        //when
        regularEventService.deleteParticipation(participation.getId(), user.getId());

        //then
        assertThat(participationRepository.findById(participation.getId())).isEmpty();
    }

    @DisplayName("정기 모임에 참여한 회원들 정보를 조회 한다.")
    @Test
    void getRegularParticipants() {
        //given
        User user = createUser("테스터", INTP);
        User user2 = createUser("테스터2", ISTP);
        userRepository.saveAll(List.of(user, user2));

        Event event = createEvent(user.getUsername(), "테스트 모임");
        eventRepository.save(event);

        RegularEvent regularEvent = createRegularEvent(user, event);
        regularEventRepository.save(regularEvent);

        RegularEventParticipation participation = RegularEventParticipation.of(user, regularEvent);
        RegularEventParticipation participation2 = RegularEventParticipation.of(user2, regularEvent);
        participationRepository.saveAll(List.of(participation, participation2));

        //when
        RegularParticipantsInfo regularParticipants = regularEventService.getRegularParticipants(regularEvent.getId());

        //then
        assertThat(regularParticipants.getParticipantCount()).isEqualTo(2);
        assertThat(regularParticipants.getRegularParticipants()).hasSize(2)
                .extracting("name","mbti")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("테스터", INTP),
                        Tuple.tuple("테스터2", ISTP)
                );
    }

    @DisplayName("정기 모임을 수정 한다.")
    @Test
    void editRegularEvent() {
        //given
        User user = createUser("테스터", INTP);
        userRepository.save(user);

        Event event = createEvent(user.getUsername(), "테스트 모임");
        eventRepository.save(event);

        RegularEvent regularEvent = createRegularEvent(user, event);
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

    private RegularEvent createRegularEvent(User user, Event event){
        return RegularEvent.builder()
                .name("정기 모임")
                .event(event)
                .dateTime(LocalDateTime.of(2025, 5, 2,12 ,0,0))
                .location("테스트 장소")
                .capacity(50)
                .userId(user.getId())
                .build();
    }

    private RegularEvent createRegularEvent(User user, Event event, String name, String location){
        return RegularEvent.builder()
                .name(name)
                .event(event)
                .dateTime(LocalDateTime.of(2025, 5, 2,12 ,0,0))
                .location(location)
                .capacity(50)
                .userId(user.getId())
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