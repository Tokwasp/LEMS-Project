package lems.cowshed.domain.user.query;

import lems.cowshed.IntegrationTestSupport;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.event.EventJpaRepository;
import lems.cowshed.domain.user.Mbti;
import lems.cowshed.domain.user.User;
import lems.cowshed.domain.user.UserRepository;
import lems.cowshed.domain.event.participation.EventParticipant;
import lems.cowshed.domain.event.participation.EventParticipantRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static lems.cowshed.domain.user.Mbti.*;
import static org.assertj.core.api.Assertions.*;

class UserQueryRepositoryTest extends IntegrationTestSupport {

    @Autowired
    EventJpaRepository eventJpaRepository;
    
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserQueryRepository userQueryRepository;

    @Autowired
    EventParticipantRepository eventParticipantRepository;

    @DisplayName("모임에 참여한 회원들을 조회 한다.")
    @Test
    void findParticipants() {
        //given
        User user = createUser("테스터", INTP);
        userRepository.save(user);
        Event event = createEvent("산책 모임", "테스터");
        eventJpaRepository.save(event);
        EventParticipant eventParticipant = EventParticipant.of(user, event);
        eventParticipantRepository.save(eventParticipant);

        //when
        List<EventParticipantQueryDto> result = userQueryRepository.findParticipants(event.getId());

        //then
        assertThat(result.get(0))
                .extracting("name")
                .isEqualTo("테스터");
    }

    @DisplayName("모임에 참여한 회원들을 조회 할때 참여한 회원이 없다면 빈 리스트를 반환 한다.")
    @Test
    void findParticipantsWhenZeroParticipating() {
        //given
        User user = createUser("테스터", INTP);
        userRepository.save(user);
        Event event = createEvent("산책 모임", "테스터");
        eventJpaRepository.save(event);

        //when
        List<EventParticipantQueryDto> result = userQueryRepository.findParticipants(event.getId());

        //then
        assertThat(result).isEmpty();
    }

    @DisplayName("두명의 회원이 같은 모임에 참여 했을때 모임의 참여자들을 확인 한다.")
    @Test
    void findParticipantsWhenTwoUserParticipating() {
        //given
        User user = createUser("테스터", INTP);
        User user2 = createUser("테스터2", ESFJ);
        Event event = createEvent("산책 모임", "테스터");

        EventParticipant eventParticipant = EventParticipant.of(user, event);
        EventParticipant eventParticipant2 = EventParticipant.of(user2, event);

        userRepository.saveAll(List.of(user, user2));
        eventJpaRepository.save(event);
        eventParticipantRepository.saveAll(List.of(eventParticipant, eventParticipant2));

        //when
        List<EventParticipantQueryDto> result = userQueryRepository.findParticipants(event.getId());

        //then
        assertThat(result).hasSize(2)
                .extracting("name")
                .containsExactlyInAnyOrder("테스터", "테스터2");
    }

    private User createUser(String username, Mbti mbti) {
        return User.builder()
                .username(username)
                .mbti(mbti)
                .build();
    }

    private Event createEvent(String name, String author) {
        return Event.builder()
                .name(name)
                .author(author)
                .build();
    }
}