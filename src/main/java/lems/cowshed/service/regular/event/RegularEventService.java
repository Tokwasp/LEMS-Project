package lems.cowshed.service.regular.event;

import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.event.participation.EventParticipation;
import lems.cowshed.domain.regular.event.RegularEvent;
import lems.cowshed.domain.regular.event.RegularEventEditCommand;
import lems.cowshed.domain.regular.event.participation.RegularEventParticipation;
import lems.cowshed.dto.regular.event.request.RegularEventEditRequest;
import lems.cowshed.dto.regular.event.request.RegularEventSaveRequest;
import lems.cowshed.dto.regular.event.request.RegularSearchCondition;
import lems.cowshed.dto.regular.event.response.RegularEventPagingInfo;
import lems.cowshed.dto.regular.event.response.RegularEventSearchResponse;
import lems.cowshed.dto.regular.event.response.RegularEventSimpleInfo;
import lems.cowshed.global.exception.NotFoundException;
import lems.cowshed.repository.event.EventRepository;
import lems.cowshed.repository.event.participation.EventParticipantRepository;
import lems.cowshed.repository.regular.event.RegularEventQueryRepository;
import lems.cowshed.repository.regular.event.RegularEventRepository;
import lems.cowshed.repository.regular.event.participation.RegularEventParticipationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static lems.cowshed.global.exception.Message.EVENT_NOT_FOUND;
import static lems.cowshed.global.exception.Message.REGULAR_EVENT_NOT_FOUND;
import static lems.cowshed.global.exception.Reason.EVENT_ID;
import static lems.cowshed.global.exception.Reason.REGULAR_EVENT_ID;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RegularEventService {

    private final EventRepository eventRepository;
    private final EventParticipantRepository eventParticipantRepository;
    private final RegularEventRepository regularEventRepository;
    private final RegularEventQueryRepository regularEventQueryRepository;
    private final RegularEventParticipationRepository regularEventParticipationRepository;

    @Transactional
    public Long saveRegularEvent(RegularEventSaveRequest request, Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(EVENT_ID, EVENT_NOT_FOUND));

        RegularEvent regularEvent = request.toEntity(event, userId);
        regularEventRepository.save(regularEvent);

        RegularEventParticipation participation = createRegularEventParticipation(userId, regularEvent);
        regularEventParticipationRepository.save(participation);
        return regularEvent.getId();
    }

    public RegularEventSimpleInfo getRegularEvent(Long regularId) {
        RegularEvent regularEvent = regularEventRepository.findById(regularId)
                .orElseThrow(() -> new NotFoundException(REGULAR_EVENT_ID, REGULAR_EVENT_NOT_FOUND));

        return RegularEventSimpleInfo.from(regularEvent);
    }

    public RegularEventPagingInfo findPagingInfo(Long eventId, Pageable pageable, Long userId) {
        Slice<RegularEvent> pagingInfo = regularEventRepository.findByEventId(eventId, pageable);
        List<RegularEvent> regularEvents = pagingInfo.getContent();

        List<Long> regularEventIds = extractId(regularEvents);
        List<RegularEventParticipation> participants = regularEventParticipationRepository.findByRegularEventIdIn(regularEventIds);

        return RegularEventPagingInfo.of(regularEvents, groupedById(participants), userId, pagingInfo.hasNext());
    }

    @Transactional
    public void editRegularEvent(RegularEventEditRequest request, Long regularId) {
        RegularEvent regularEvent = regularEventRepository.findById(regularId)
                .orElseThrow(() -> new NotFoundException(REGULAR_EVENT_ID, REGULAR_EVENT_NOT_FOUND));

        long participantCount = regularEventParticipationRepository.getParticipantCountByRegularId(regularId);
        regularEvent.updateCapacity(participantCount, request.getCapacity());

        RegularEventEditCommand editCommand = createRegularEditCommand(request);
        regularEvent.edit(editCommand);
    }

    @Transactional
    public Long delete(Long regularId) {
        RegularEvent regularEvent = regularEventRepository.findById(regularId)
                .orElseThrow(() -> new NotFoundException(REGULAR_EVENT_ID, REGULAR_EVENT_NOT_FOUND));

        regularEventRepository.delete(regularEvent);
        return regularEvent.getId();
    }

    public int searchCount(RegularSearchCondition condition) {
        return regularEventQueryRepository.searchCount(condition.getName(), condition.getDate());
    }

    public RegularEventSearchResponse search(Pageable pageable, RegularSearchCondition condition, Long userId) {
        Slice<RegularEvent> slice = regularEventQueryRepository.searchFetchEvent(pageable, condition.getName(), condition.getDate());
        List<RegularEvent> regularEvents = slice.getContent();

        List<Long> regularEventIds = extractId(regularEvents);
        List<Long> eventIds = getEventIds(regularEvents);
        List<EventParticipation> participants = eventParticipantRepository.findByEventIdIn(eventIds);

        List<RegularEvent> regularFetchParticipation = regularEventRepository.findByIdInFetchParticipation(regularEventIds);
        return RegularEventSearchResponse.of(regularEvents, regularFetchParticipation, participants, userId, slice.hasNext());
    }

    private List<Long> extractId(List<RegularEvent> regularEvents) {
        return regularEvents.stream()
                .map(RegularEvent::getId)
                .toList();
    }

    private RegularEventParticipation createRegularEventParticipation(Long userId, RegularEvent regularEvent) {
        return RegularEventParticipation.of(userId, regularEvent.getId());
    }

    private Map<Long, List<RegularEventParticipation>> groupedById(List<RegularEventParticipation> participants) {
        return participants.stream()
                .collect(Collectors.groupingBy(
                        RegularEventParticipation::getRegularEventId
                ));
    }

    private RegularEventEditCommand createRegularEditCommand(RegularEventEditRequest request) {
        return RegularEventEditCommand.of(
                request.getName(),
                request.getDateTime(),
                request.getLocation(),
                request.getCapacity()
        );
    }

    private List<Long> getEventIds(List<RegularEvent> regularEvents) {
        return regularEvents.stream()
                .map(re -> re.getEvent().getId())
                .distinct()
                .toList();
    }
}