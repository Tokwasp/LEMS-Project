package lems.cowshed.service;

import jakarta.persistence.EntityManager;
import lems.cowshed.IntegrationTestSupport;
import lems.cowshed.api.controller.dto.regular.event.request.RegularEventSaveRequest;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.event.EventRepository;
import lems.cowshed.domain.regular.event.RegularEvent;
import lems.cowshed.domain.regular.event.RegularEventRepository;
import lems.cowshed.domain.regular.event.participation.RegularEventParticipation;
import lems.cowshed.domain.regular.event.participation.RegularEventParticipationRepository;
import lems.cowshed.domain.user.User;
import lems.cowshed.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

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
    void save() {
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
        regularEventService.save(request, event.getId(), null);

        //then
        List<RegularEvent> savedEvents = regularEventRepository.findAll();
        assertThat(savedEvents).hasSize(1);
        assertThat(savedEvents.get(0).getName()).isEqualTo("정기 모임");
    }

    @DisplayName("정기 모임 참석을 해제 한다.")
    @Test
    void deleteByParticipationIdAndUserId() {
        //given
        User user = createUser("정기 모임 생성자", "RegularEventCreator");
        userRepository.save(user);

        Event event = createEvent("테스터", "테스트 모임");
        eventRepository.save(event);

        RegularEvent regularEvent = createRegularEvent(user);
        regularEventRepository.save(regularEvent);

        RegularEventParticipation participation = RegularEventParticipation.of(user, regularEvent);
        participationRepository.save(participation);

        //when
        regularEventService.deleteParticipation(participation.getId(), user.getId());

        //then
        assertThat(participationRepository.findById(participation.getId())).isEmpty();
    }

    private Event createEvent(String author, String name) {
        return Event.builder()
                .name(name)
                .author(author)
                .build();
    }

    private RegularEvent createRegularEvent(User user){
        return RegularEvent.builder()
                .name("정기 모임")
                .dateTime(LocalDateTime.of(2025, 5, 2,12 ,0,0))
                .location("테스트 장소")
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
}