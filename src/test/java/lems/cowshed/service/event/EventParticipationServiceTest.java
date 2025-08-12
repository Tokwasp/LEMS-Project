package lems.cowshed.service.event;

import lems.cowshed.IntegrationTestSupport;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.event.participation.EventParticipation;
import lems.cowshed.domain.user.User;
import lems.cowshed.global.exception.BusinessException;
import lems.cowshed.global.exception.NotFoundException;
import lems.cowshed.repository.event.EventRepository;
import lems.cowshed.repository.event.participation.EventParticipantRepository;
import lems.cowshed.repository.user.UserRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventParticipationServiceTest extends IntegrationTestSupport {

    @Autowired
    private EventParticipationService eventParticipationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventParticipantRepository eventParticipantRepository;

    @Autowired
    private EventRepository eventRepository;

    @DisplayName("유저가 모임에 참여 한다.")
    @Test
    void saveEventParticipation() {
        //given
        Event event = createEvent("테스터", "자전거 모임", 10);
        eventRepository.save(event);

        User user = createUser("테스터", "test@naver.com");
        userRepository.save(user);

        //when
        eventParticipationService.saveEventParticipation(event.getId(), user.getId());

        //then
        Event findEvent = eventRepository.findById(event.getId()).orElseThrow();
        assertThat(findEvent.getName()).isEqualTo("자전거 모임");
    }

    @Disabled
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DisplayName("최대 인원이 3명인 모임에 5명의 회원이 동시에 참가 하면 1명만 참여 할 수 있다.")
    @Test
    void saveEventParticipation_WhenFiveUsersJoin_ThenThreeParticipantsAllowed() throws Exception {
        //given
        int taskCount = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        CountDownLatch countDownLatch = new CountDownLatch(taskCount);

        Event findEvent = eventRepository.save(createEvent("테스터", "테스트 모임", 3));

        List<User> users = Stream
                .generate(() -> {
                    User user = createUser("테스터", "testEmail");
                    userRepository.save(user);
                    return user;
                })
                .limit(taskCount)
                .toList();

        //when
        AtomicInteger exceptionCount = new AtomicInteger(0);

        for (User user : users) {
            executorService.submit(() -> {
                try {
                    eventParticipationService.saveEventParticipation(findEvent.getId(), user.getId());
                } catch (BusinessException ex) {
                    exceptionCount.incrementAndGet();
                } finally {
                    countDownLatch.countDown();  // 카운트다운
                }
            });
        }
        countDownLatch.await();
        executorService.shutdown();

        // then
        long participants = eventParticipantRepository.getParticipationCountById(findEvent.getId());
        assertThat(participants).isEqualTo(3);
        assertThat(exceptionCount.get()).isEqualTo(2);
    }

    @DisplayName("회원이 참석한 모임의 참석을 해제 한다.")
    @Test
    void deleteEventParticipation() {
        //given
        User user = createUser("테스터", "test@naver.com");
        userRepository.save(user);

        Event event = createEvent("테스터", "테스트 모임");
        eventRepository.save(event);

        EventParticipation eventParticipation = EventParticipation.of(user, event.getId());
        eventParticipantRepository.save(eventParticipation);

        //when
        eventParticipationService.deleteEventParticipation(event.getId(), user.getId());

        //then
        assertThatThrownBy(() -> eventParticipantRepository.findById(eventParticipation.getId()).orElseThrow())
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("회원이 모임 참석을 해제 할때 등록 하지 않은 모임 이라면 예외가 발생 한다.")
    @Test
    void deleteEventParticipationWhenNotParticipated() {
        //given
        User user = createUser("테스터", "test@naver.com");
        userRepository.save(user);

        Event event = createEvent("테스터", "테스트 모임");
        eventRepository.save(event);

        EventParticipation eventParticipation = EventParticipation.of(user, event.getId());
        eventParticipantRepository.save(eventParticipation);

        //when //then
        assertThatThrownBy(() -> eventParticipationService.deleteEventParticipation(null, user.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    private User createUser(String username, String email) {
        return User.builder()
                .username(username)
                .email(email)
                .build();
    }

    private static Event createEvent(String author, String name) {
        return Event.builder()
                .name(name)
                .author(author)
                .build();
    }

    private static Event createEvent(String author, String name, int capacity) {
        return Event.builder()
                .name(name)
                .author(author)
                .capacity(capacity)
                .build();
    }
}