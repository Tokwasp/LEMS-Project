package lems.cowshed.service.regular.event;

import lems.cowshed.IntegrationTestSupport;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.regular.event.RegularEvent;
import lems.cowshed.domain.user.User;
import lems.cowshed.global.exception.BusinessException;
import lems.cowshed.repository.event.EventRepository;
import lems.cowshed.repository.regular.event.RegularEventRepository;
import lems.cowshed.repository.regular.event.participation.RegularEventParticipationRepository;
import lems.cowshed.repository.user.UserRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class RegularEventParticipationFacadeTest extends IntegrationTestSupport {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RegularEventRepository regularEventRepository;

    @Autowired
    private RegularEventParticipationRepository regularEventParticipationRepository;

    @Autowired
    private RegularEventParticipationFacade regularEventFacade;

    @Disabled
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DisplayName("3명 정원인 정기 모임에 3명이 동시 참여할 때 실패 시 재시도가 수행된다.")
    void saveRegularParticipation_ShouldHandleOptimisticLock_And_MaintainDataIntegrity() throws InterruptedException {
        //given
        int taskCount = 3;
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        CountDownLatch countDownLatch = new CountDownLatch(taskCount);

        Event event = eventRepository.save(createEvent("테스터", "테스트 모임", 3));

        List<User> users = Stream
                .generate(() -> {
                    User user = createUser("테스터", "testEmail");
                    userRepository.save(user);
                    return user;
                })
                .limit(taskCount)
                .toList();

        RegularEvent regularEvent = regularEventRepository.save(createRegularEvent(users.get(0).getId(), event, 3));

        //when
        for (User user : users) {
            executorService.submit(() -> {
                try {
                    regularEventFacade.saveParticipation(regularEvent.getId(), user.getId());
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        executorService.shutdown();

        // then
        long participants = regularEventParticipationRepository.getParticipantCountByRegularId(regularEvent.getId());
        assertThat(participants).isEqualTo(3);
    }

    @Disabled
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DisplayName("3명 정원인 정기 모임에 10명이 동시 참여할 때 실패 시 재시도가 수행된다.")
    void saveRegularParticipation_ShouldThrowBusinessException_AfterMaxRetries() throws InterruptedException {
        //given
        int taskCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch countDownLatch = new CountDownLatch(taskCount);

        Event event = eventRepository.save(createEvent("테스터", "테스트 모임", 3));

        List<User> users = Stream
                .generate(() -> {
                    User user = createUser("테스터", "testEmail");
                    userRepository.save(user);
                    return user;
                })
                .limit(taskCount)
                .toList();

        RegularEvent regularEvent = regularEventRepository.save(createRegularEvent(users.get(0).getId(), event, 3));
        AtomicReference<Throwable> capturedException = new AtomicReference<>();

        //when
        for (User user : users) {
            executorService.submit(() -> {
                try {
                    regularEventFacade.saveParticipation(regularEvent.getId(), user.getId());
                } catch (BusinessException e) {
                    capturedException.set(e);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        executorService.shutdown();

        // then
        assertThat(capturedException.get()).isInstanceOf(BusinessException.class);
    }

    private Event createEvent(String author, String name, int capacity) {
        return Event.builder()
                .name(name)
                .author(author)
                .capacity(capacity)
                .build();
    }

    private RegularEvent createRegularEvent(Long userId, Event event, int capacity) {
        return RegularEvent.builder()
                .name("정기 모임")
                .event(event)
                .dateTime(LocalDateTime.of(2025, 5, 2, 12, 0, 0))
                .location("테스트 장소")
                .capacity(capacity)
                .userId(userId)
                .build();
    }

    private User createUser(String username, String email) {
        return User.builder()
                .username(username)
                .email(email)
                .build();
    }
}