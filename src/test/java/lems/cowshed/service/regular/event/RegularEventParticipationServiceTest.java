package lems.cowshed.service.regular.event;

import jakarta.persistence.EntityManager;
import lems.cowshed.IntegrationTestSupport;
import lems.cowshed.dto.regular.event.response.RegularParticipantsInfo;
import lems.cowshed.domain.event.Event;
import lems.cowshed.repository.event.EventRepository;
import lems.cowshed.domain.regular.event.RegularEvent;
import lems.cowshed.repository.regular.event.RegularEventRepository;
import lems.cowshed.domain.regular.event.participation.RegularEventParticipation;
import lems.cowshed.repository.regular.event.participation.RegularEventParticipationRepository;
import lems.cowshed.domain.user.Mbti;
import lems.cowshed.domain.user.User;
import lems.cowshed.repository.user.UserRepository;
import lems.cowshed.global.exception.BusinessException;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static lems.cowshed.domain.user.Mbti.INTP;
import static lems.cowshed.domain.user.Mbti.ISTP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegularEventParticipationServiceTest extends IntegrationTestSupport {

    @Autowired
    private RegularEventParticipationService regularEventParticipationService;

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

    @DisplayName("정기 모임에 참여 한다.")
    @Test
    void saveParticipation() {
        //given
        User user = createUser("테스터", INTP);
        userRepository.save(user);

        Event event = createEvent("테스터", "테스트 모임");
        eventRepository.save(event);

        RegularEvent regularEvent = createRegularEvent(user.getId(), event, "정기모임", "장소");
        regularEventRepository.save(regularEvent);

        //when
        Long savedParticipationId = regularEventParticipationService.saveParticipation(regularEvent.getId(), user.getId());
        em.flush(); em.clear();

        //then
        RegularEventParticipation findParticipation = participationRepository.findById(savedParticipationId).orElseThrow();
        assertThat(findParticipation.getUserId()).isEqualTo(user.getId());

        RegularEvent findRegularEvent = findParticipation.getRegularEvent();
        assertThat(findRegularEvent.getName()).isEqualTo("정기모임");
        assertThat(findRegularEvent.getLocation()).isEqualTo("장소");
    }

    @DisplayName("정기 모임에 참여 할때 이미 참여한 모임 이라면 예외가 발생 한다.")
    @Test
    void saveParticipation_WhenAlreadyParticipation_ThenThrowException() {
        //given
        User user = createUser("테스터", INTP);
        userRepository.save(user);

        Event event = createEvent("테스터", "테스트 모임");
        eventRepository.save(event);

        RegularEvent regularEvent = createRegularEvent(user.getId(), event, "정기모임", "장소");
        regularEventRepository.save(regularEvent);

        RegularEventParticipation participation = createRegularEventParticipation(user.getId());
        participation.connectRegularEvent(regularEvent);
        participationRepository.save(participation);

        //when
        assertThatThrownBy(() -> regularEventParticipationService.saveParticipation(regularEvent.getId(), user.getId()))
                .isInstanceOf(BusinessException.class).hasMessage("정기 모임에 이미 참여중 입니다.");
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

        RegularEvent regularEvent = createRegularEvent(user.getId(), event);
        regularEventRepository.save(regularEvent);

        RegularEventParticipation participation = RegularEventParticipation.of(user.getId(), regularEvent);
        RegularEventParticipation participation2 = RegularEventParticipation.of(user2.getId(), regularEvent);
        participationRepository.saveAll(List.of(participation, participation2));

        //when
        RegularParticipantsInfo regularParticipants = regularEventParticipationService.getRegularParticipants(regularEvent.getId());

        //then
        assertThat(regularParticipants.getParticipantCount()).isEqualTo(2);
        assertThat(regularParticipants.getCapacity()).isEqualTo(50);
        assertThat(regularParticipants.getRegularParticipants()).hasSize(2)
                .extracting("name","mbti")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("테스터", INTP),
                        Tuple.tuple("테스터2", ISTP)
                );
    }

    @DisplayName("정기 모임 참석을 해제 한다.")
    @Test
    void deleteByParticipationIdAndUserId() {
        //given
        User user = createUser("정기 모임 생성자", "RegularEventCreator");
        userRepository.save(user);

        Event event = createEvent("테스터", "테스트 모임");
        eventRepository.save(event);

        RegularEvent regularEvent = createRegularEvent(user.getId(), event);
        regularEventRepository.save(regularEvent);

        RegularEventParticipation participation = RegularEventParticipation.of(user.getId(), regularEvent);
        participationRepository.save(participation);

        //when
        regularEventParticipationService.deleteParticipation(participation.getId(), user.getId());

        //then
        assertThat(participationRepository.findById(participation.getId())).isEmpty();
    }


    private Event createEvent(String author, String name) {
        return Event.builder()
                .name(name)
                .author(author)
                .build();
    }

    private RegularEventParticipation createRegularEventParticipation(Long userId){
        return RegularEventParticipation.builder()
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

    private User createUser(String username, Mbti mbti) {
        return User.builder()
                .username(username)
                .mbti(mbti)
                .build();
    }

    private User createUser(String username, String email) {
        return User.builder()
                .username(username)
                .email(email)
                .build();
    }
}