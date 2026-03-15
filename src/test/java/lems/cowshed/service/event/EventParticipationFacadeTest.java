package lems.cowshed.service.event;

import lems.cowshed.IntegrationTestSupport;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.user.User;
import lems.cowshed.global.exception.BusinessException;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class EventParticipationFacadeTest extends IntegrationTestSupport {

    @Autowired
    private EventParticipationFacade eventParticipationFacade;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventParticipantRepository eventParticipantRepository;

    @Autowired
    private UserRepository userRepository;

    @Disabled
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DisplayName("3명 정원인 모임에 3명이 동시 참여할 때 실패 시 재시도가 수행된다.")
    void saveEventParticipation_ShouldRetry_WhenOptimisticLockExceptionOccurs() throws InterruptedException {
        //given
        int taskCount = 3;
        ExecutorService executorService = Executors.newFixedThreadPool(3);
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
        for (User user : users) {
            executorService.submit(() -> {
                try {
                    eventParticipationFacade.saveEventParticipation(findEvent.getId(), user.getId());
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        executorService.shutdown();

        // then
        long participants = eventParticipantRepository.getParticipationCountById(findEvent.getId());
        assertThat(participants).isEqualTo(3);
    }

    @Disabled
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DisplayName("5명이 최대인원인 모임에 10명 동시 참여 시 재시도 후 Recover 메서드가 호출된다.")
    void saveEventParticipation_ShouldThrowBusinessException_AfterMaxRetries() throws InterruptedException {
        //given
        int taskCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch countDownLatch = new CountDownLatch(taskCount);

        Event findEvent = eventRepository.save(createEvent("테스터", "테스트 모임", 5));

        List<User> users = Stream
                .generate(() -> {
                    User user = createUser("테스터", "testEmail");
                    userRepository.save(user);
                    return user;
                })
                .limit(taskCount)
                .toList();

        AtomicReference<Throwable> capturedException = new AtomicReference<>();

        //when
        for (User user : users) {
            executorService.submit(() -> {
                try {
                    eventParticipationFacade.saveEventParticipation(findEvent.getId(), user.getId());
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

    private User createUser(String username, String email) {
        return User.builder()
                .username(username)
                .email(email)
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