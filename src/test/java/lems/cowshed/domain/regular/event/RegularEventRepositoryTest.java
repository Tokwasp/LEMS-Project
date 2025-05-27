package lems.cowshed.domain.regular.event;

import lems.cowshed.IntegrationTestSupport;
import lems.cowshed.domain.event.Event;
import lems.cowshed.repository.event.EventRepository;
import lems.cowshed.domain.regular.event.participation.RegularEventParticipation;
import lems.cowshed.repository.regular.event.participation.RegularEventParticipationRepository;
import lems.cowshed.domain.user.Mbti;
import lems.cowshed.domain.user.User;
import lems.cowshed.repository.regular.event.RegularEventRepository;
import lems.cowshed.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static lems.cowshed.domain.user.Mbti.*;
import static org.assertj.core.api.Assertions.*;

class RegularEventRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RegularEventRepository regularEventRepository;

    @Autowired
    private RegularEventParticipationRepository participationRepository;

    @DisplayName("정기 모임에 참여한 회원들의 ID 리스트를 찾는다.")
    @Test
    void findParticipantsUserIdsByRegularId() {
        //given
        User user = createUser("테스터", INTP);
        User user2 = createUser("테스터2", ISTP);
        userRepository.saveAll(List.of(user, user2));

        Event event = createEvent("테스트 모임");
        eventRepository.save(event);

        RegularEvent regularEvent = createRegularEvent(event, "정기 모임", "정기 모임 장소");
        regularEventRepository.save(regularEvent);

        RegularEventParticipation participation = RegularEventParticipation.of(user.getId(), regularEvent);
        RegularEventParticipation participation2 = RegularEventParticipation.of(user2.getId(), regularEvent);
        participationRepository.saveAll(List.of(participation, participation2));

        //when
        List<Long> participantsUserIds = regularEventRepository.findParticipantsUserIdsByRegularId(regularEvent.getId());

        //then
        assertThat(participantsUserIds).hasSize(2)
                .containsExactlyInAnyOrder(
                        user.getId(), user2.getId()
                );
    }

    @DisplayName("정기 모임 식별자들에 속하는 참여 정보를 같이 조회 한다.")
    @Test
    void findByIdsFetchParticipation() {
        //given
        Long userId = 1L;
        RegularEvent regularEvent = createRegularEvent(null, "정기 모임", "장소");
        RegularEventParticipation participation = createParticipation(userId);
        participation.connectRegularEvent(regularEvent);

        RegularEvent regularEvent2 = createRegularEvent(null, "정기 모임2", "장소2");
        RegularEventParticipation participation2 = createParticipation(userId);
        participation2.connectRegularEvent(regularEvent2);
        regularEventRepository.saveAll(List.of(regularEvent, regularEvent2));

        //when
        List<Long> regularEventIds = List.of(regularEvent.getId(), regularEvent2.getId());
        List<RegularEvent> regularEvents = regularEventRepository.findByIdsFetchParticipation(regularEventIds);

        //then
        assertThat(regularEvents).hasSize(2);
        assertThat(regularEvents.get(0).getParticipations()).hasSize(1);
        assertThat(regularEvents.get(1).getParticipations()).hasSize(1);
    }

    private User createUser(String username, Mbti mbti) {
        return User.builder()
                .username(username)
                .mbti(mbti)
                .build();
    }

    private Event createEvent(String name) {
        return Event.builder()
                .name(name)
                .build();
    }

    private RegularEvent createRegularEvent(Event event, String name, String location){
        return RegularEvent.builder()
                .name(name)
                .event(event)
                .dateTime(LocalDateTime.of(2025,5,5,12,0,0))
                .location(location)
                .capacity(50)
                .build();
    }

    private RegularEventParticipation createParticipation(Long userId){
        return RegularEventParticipation.builder()
                .userId(userId)
                .build();
    }
}