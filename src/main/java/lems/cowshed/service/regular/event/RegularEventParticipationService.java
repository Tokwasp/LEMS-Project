package lems.cowshed.service.regular.event;

import lems.cowshed.domain.regular.event.RegularEvent;
import lems.cowshed.domain.regular.event.participation.RegularEventParticipation;
import lems.cowshed.domain.user.User;
import lems.cowshed.dto.regular.event.response.RegularParticipantDetails;
import lems.cowshed.dto.regular.event.response.RegularParticipantsInfo;
import lems.cowshed.global.exception.BusinessException;
import lems.cowshed.global.exception.NotFoundException;
import lems.cowshed.repository.regular.event.RegularEventRepository;
import lems.cowshed.repository.regular.event.participation.RegularEventParticipationRepository;
import lems.cowshed.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static lems.cowshed.global.exception.Message.*;
import static lems.cowshed.global.exception.Reason.*;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RegularEventParticipationService {

    private final UserRepository userRepository;
    private final RegularEventRepository regularEventRepository;
    private final RegularEventParticipationRepository participationRepository;

    @Transactional
    public Long saveParticipation(Long regularId, Long userId) {
        alreadyParticipateCheck(regularId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_ID, USER_NOT_FOUND));

        RegularEvent regularEvent = regularEventRepository.findWithOptimisticLockById(regularId)
                .orElseThrow(() -> new NotFoundException(REGULAR_EVENT_ID, REGULAR_EVENT_NOT_FOUND));

        long participantCount = regularEventRepository.getParticipantCount(regularEvent.getId());

        if (regularEvent.isNotJoinAble(participantCount)) {
            throw new BusinessException(REGULAR_EVENT_PARTICIPATION, REGULAR_EVENT_NOT_POSSIBLE_PARTICIPATION);
        }

        RegularEventParticipation participation = participationRepository.save(
                RegularEventParticipation.of(user.getId(), regularEvent.getId())
        );
        return participation.getId();
    }

    public RegularParticipantsInfo getRegularParticipants(Long regularId) {
        RegularEvent regularEvent = regularEventRepository.findById(regularId)
                .orElseThrow(() -> new NotFoundException(REGULAR_EVENT_ID, REGULAR_EVENT_NOT_FOUND));

        List<RegularEventParticipation> participants = participationRepository.findByRegularEventId(regularEvent.getId());
        List<User> users = findUsersFrom(participants);
        return RegularParticipantsInfo.of(convertDtoList(users), participants.size(), regularEvent.getCapacity());
    }

    @Transactional
    public void deleteParticipation(Long participationId, Long userId) {
        RegularEventParticipation participation = participationRepository.findByIdAndUserId(participationId, userId)
                .orElseThrow(() -> new NotFoundException(REGULAR_EVENT_PARTICIPATION, REGULAR_EVENT_PARTICIPATION_NOT_FOUND));

        participationRepository.delete(participation);
    }

    private void alreadyParticipateCheck(Long regularId, Long userId) {
        participationRepository.findByRegularEventIdAndUserId(regularId, userId)
                .ifPresent(participation -> {
                    throw new BusinessException(REGULAR_EVENT_ID, REGULAR_EVENT_ALREADY_PARTICIPATION);
                });
    }

    private List<User> findUsersFrom(List<RegularEventParticipation> participants) {
        List<Long> userIds = participants.stream()
                .map(RegularEventParticipation::getUserId)
                .toList();
        return userRepository.findByIdIn(userIds);
    }

    private List<RegularParticipantDetails> convertDtoList(List<User> participants) {
        return participants.stream()
                .map(user -> RegularParticipantDetails.of(user.getUsername(), user.getMbti()))
                .toList();
    }
}
