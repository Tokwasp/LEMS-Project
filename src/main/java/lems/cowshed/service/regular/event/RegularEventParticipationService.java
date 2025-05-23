package lems.cowshed.service.regular.event;

import lems.cowshed.api.controller.dto.regular.event.response.RegularParticipantDetails;
import lems.cowshed.api.controller.dto.regular.event.response.RegularParticipantsInfo;
import lems.cowshed.domain.regular.event.RegularEvent;
import lems.cowshed.domain.regular.event.RegularEventRepository;
import lems.cowshed.domain.regular.event.participation.RegularEventParticipation;
import lems.cowshed.domain.regular.event.participation.RegularEventParticipationRepository;
import lems.cowshed.domain.user.User;
import lems.cowshed.domain.user.UserRepository;
import lems.cowshed.exception.BusinessException;
import lems.cowshed.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static lems.cowshed.exception.Message.*;
import static lems.cowshed.exception.Reason.*;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RegularEventParticipationService {

    private final UserRepository userRepository;
    private final RegularEventRepository regularEventRepository;
    private final RegularEventParticipationRepository regularEventParticipationRepository;

    @Transactional
    public void saveParticipation(Long regularId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_ID, USER_NOT_FOUND));

        RegularEvent regularEvent = regularEventRepository.findRegularEventWithLockById(regularId)
                .orElseThrow(() -> new NotFoundException(REGULAR_EVENT_ID, REGULAR_EVENT_NOT_FOUND));

        long participantCount = regularEventRepository.getParticipantCount(regularEvent.getId());

        if(regularEvent.isNotPossibleParticipation(participantCount)){
            throw new BusinessException(REGULAR_EVENT_PARTICIPATION, REGULAR_EVENT_NOT_POSSIBLE_PARTICIPATION);
        }
        regularEventParticipationRepository.save(RegularEventParticipation.of(user, regularEvent));
    }

    public RegularParticipantsInfo getRegularParticipants(Long regularId) {
        List<Long> participantsUserIds = regularEventRepository.findParticipantsUserIdsByRegularId(regularId);
        List<User> participants = userRepository.findByIdIn(participantsUserIds);

        List<RegularParticipantDetails> details = convertDetails(participants);
        return RegularParticipantsInfo.of(details, participantsUserIds.size());
    }

    @Transactional
    public void deleteParticipation(Long participationId, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_ID, USER_NOT_FOUND));

        regularEventParticipationRepository.deleteByIdAndUserId(participationId, userId);
    }

    private List<RegularParticipantDetails> convertDetails(List<User> participants) {
        return participants.stream()
                .map(user -> RegularParticipantDetails.of(user.getUsername(), user.getMbti()))
                .toList();
    }
}
