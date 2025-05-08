package lems.cowshed.service;

import lems.cowshed.api.controller.dto.regular.event.request.RegularEventSaveRequest;
import lems.cowshed.api.controller.dto.regular.event.response.RegularParticipantsInfo;
import lems.cowshed.api.controller.dto.regular.event.response.RegularParticipantDetails;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.event.EventRepository;
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

@Transactional
@RequiredArgsConstructor
@Service
public class RegularEventService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RegularEventRepository regularEventRepository;
    private final RegularEventParticipationRepository regularEventParticipationRepository;

    public void save(RegularEventSaveRequest request, Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(EVENT_ID, EVENT_NOT_FOUND));

        RegularEvent regularEvent = request.toEntity(event, userId);
        regularEventRepository.save(regularEvent);
    }

    public void saveParticipation(Long regularId, Long userId) {
        User user = findUser(userId);
        RegularEvent regularEvent = findRegularEvent(regularId);
        regularEventParticipation(regularEvent);

        regularEventParticipationRepository.save(RegularEventParticipation.of(user, regularEvent));
    }

    public RegularParticipantsInfo getRegularParticipants(Long regularId) {
        List<Long> participantsUserIds = regularEventRepository.findParticipantsUserIdsByRegularId(regularId);
        List<User> participants = userRepository.findByIdIn(participantsUserIds);
        List<RegularParticipantDetails> details = convertDetails(participants);
        return RegularParticipantsInfo.of(details, participantsUserIds.size());
    }

    public void deleteParticipation(Long participationId, Long userId) {
        findUser(userId);
        regularEventParticipationRepository.deleteByIdAndUserId(participationId, userId);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_ID, USER_NOT_FOUND));
    }


    private void regularEventParticipation(RegularEvent regularEvent) {
        long participantCount = regularEventRepository.getParticipantCount(regularEvent.getId());
        if(regularEvent.isNotPossibleParticipation(participantCount)){
            throw new BusinessException(REGULAR_EVENT_PARTICIPATION, REGULAR_EVENT_NOT_POSSIBLE_PARTICIPATION);
        }
    }

    private RegularEvent findRegularEvent(Long regularId) {
        return regularEventRepository.findRegularEventWithLockById(regularId)
                .orElseThrow(() -> new NotFoundException(REGULAR_EVENT_ID, REGULAR_EVENT_NOT_FOUND));
    }

    private List<RegularParticipantDetails> convertDetails(List<User> participants) {
        return participants.stream()
                .map(user -> RegularParticipantDetails.of(user.getUsername(), user.getMbti()))
                .toList();
    }
}