package lems.cowshed.domain.regular.event.participation;

import lems.cowshed.IntegrationTestSupport;
import lems.cowshed.domain.event.Event;
import lems.cowshed.repository.event.EventRepository;
import lems.cowshed.domain.regular.event.RegularEvent;
import lems.cowshed.repository.regular.event.RegularEventRepository;
import lems.cowshed.domain.user.Mbti;
import lems.cowshed.domain.user.User;
import lems.cowshed.repository.regular.event.participation.RegularEventParticipationRepository;
import lems.cowshed.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class RegularEventParticipationRepositoryTest extends IntegrationTestSupport {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RegularEventRepository regularEventRepository;

    @Autowired
    private RegularEventParticipationRepository participationRepository;


    @DisplayName("정기 모임에 참여한 회원 수를 조회 한다.")
    @Test
    void getParticipantCountByRegularId() {
        //given
        User user = createUser("회원1", Mbti.INTP);
        User user2 = createUser("회원2", Mbti.INTP);
        userRepository.saveAll(List.of(user, user2));

        Event event = createEvent("회원1", "모임");
        eventRepository.save(event);

        RegularEvent regularEvent = createRegularEvent(event, "정기모임", "장소", user.getId());
        regularEventRepository.save(regularEvent);

        RegularEventParticipation regularEventParticipation = RegularEventParticipation.of(user, regularEvent);
        RegularEventParticipation regularEventParticipation2 = RegularEventParticipation.of(user2, regularEvent);
        participationRepository.saveAll(List.of(regularEventParticipation, regularEventParticipation2));

        //when
        long participantCount = participationRepository.getParticipantCountByRegularId(regularEvent.getId());

        //then
        assertThat(participantCount).isEqualTo(2);
    }

    private User createUser(String username, Mbti mbti) {
        return User.builder()
                .username(username)
                .mbti(mbti)
                .build();
    }

    private static Event createEvent(String author, String name) {
        return Event.builder()
                .name(name)
                .author(author)
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
}