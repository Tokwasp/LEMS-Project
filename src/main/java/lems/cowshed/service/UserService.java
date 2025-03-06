package lems.cowshed.service;

import com.querydsl.core.Tuple;
import lems.cowshed.api.controller.dto.user.request.UserEditRequestDto;
import lems.cowshed.api.controller.dto.user.request.UserLoginRequestDto;
import lems.cowshed.api.controller.dto.user.request.UserSaveRequestDto;
import lems.cowshed.api.controller.dto.user.response.ParticipatingUserListInfo;
import lems.cowshed.api.controller.dto.user.response.UserMyPageInfo;
import lems.cowshed.api.controller.dto.user.response.UserInfo;
import lems.cowshed.domain.event.query.BookmarkedEventSimpleInfoQuery;
import lems.cowshed.domain.event.query.EventQueryRepository;
import lems.cowshed.domain.event.query.ParticipatingEventSimpleInfoQuery;
import lems.cowshed.domain.user.Role;
import lems.cowshed.domain.user.User;
import lems.cowshed.domain.user.UserRepository;
import lems.cowshed.domain.user.query.EventParticipantQueryDto;
import lems.cowshed.domain.user.query.MyPageUserQueryDto;
import lems.cowshed.domain.user.query.UserQueryRepository;
import lems.cowshed.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static lems.cowshed.domain.bookmark.BookmarkStatus.*;
import static lems.cowshed.domain.userevent.QUserEvent.userEvent;
import static lems.cowshed.exception.Message.*;
import static lems.cowshed.exception.Reason.*;

@RequiredArgsConstructor
@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserQueryRepository userQueryRepository;
    private final EventQueryRepository eventQueryRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserMyPageInfo findMyPage(Long userId) {
        MyPageUserQueryDto userDto = userQueryRepository.findUser(userId);

        List<ParticipatingEventSimpleInfoQuery> participatedEvents = eventQueryRepository
                .findParticipatedEvents(eventQueryRepository.getParticipatedEventsId(userId, PageRequest.of(0, 5)));
        setBookmarkStatus(participatedEvents, eventQueryRepository.getBookmarkedEventIdSet(userId, getParticipatedEventIds(participatedEvents)));

        List<BookmarkedEventSimpleInfoQuery> bookmarkedEventList = eventQueryRepository
                .findBookmarkedEventsPaging(userId, PageRequest.of(0, 5));
        setApplicants(eventQueryRepository.findEventIdParticipants(mapToEventIdList(bookmarkedEventList)), bookmarkedEventList);

        return UserMyPageInfo.of(userDto, participatedEvents, bookmarkedEventList);
    }

    public ParticipatingUserListInfo findUserParticipatingInEvent(LocalDate currentYear, Long userId){
        List<EventParticipantQueryDto> userEventDtoList = userQueryRepository.findUserParticipatingInEvent(userId);
        calculateAndSetDtoAge(currentYear, userEventDtoList);

        return new ParticipatingUserListInfo(userEventDtoList);
    }

    public void signUp(UserSaveRequestDto saveDto) {
        if(userRepository.existsByEmailOrUsername(saveDto.getEmail(), saveDto.getUsername())){
            throw new BusinessException(USERNAME_OR_EMAIL, USERNAME_OR_EMAIL_EXIST);
        }

        User user = saveDto.toEntityForRegister(bCryptPasswordEncoder, Role.ROLE_USER);
        userRepository.save(user);
    }

    public void login(UserLoginRequestDto loginDto){
         String email = loginDto.getEmail();
         User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(USER_EMAIL, USER_NOT_FOUND));

        if(isPasswordValidationFail(loginDto, user)){
            throw new BusinessException(USER_PASSWORD, USER_ID_PASSWORD_CHECK);
        }
    }

    public void editUser(UserEditRequestDto editDto, Long userId, String myUsername){
        if(isChangeUsername(editDto, myUsername)) {
            userRepository.findByUsername(editDto.getUsername())
                    .ifPresent(u -> {
                        throw new BusinessException(USER_NAME, USERNAME_EXIST);
                    });
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_ID, USER_NOT_FOUND));

        user.modifyContents(editDto);
    }

    public boolean signUpValidationForUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public boolean signUpValidationForEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public UserInfo findUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_ID, USER_NOT_FOUND));

        return UserInfo.from(user);
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_ID, USER_NOT_FOUND));

        userRepository.delete(user);
    }

    private void calculateAndSetDtoAge(LocalDate currentYear, List<EventParticipantQueryDto> userEventDtoList) {
        userEventDtoList.forEach((EventParticipantQueryDto dto) -> {
            if (dto.getBirth() == null){
                dto.setAge(null);
            }
            else {
                dto.setAge((int) ChronoUnit.YEARS.between(dto.getBirth(), currentYear) + 1);
            }
        });
    }

    private boolean isPasswordValidationFail(UserLoginRequestDto loginDto, User user) {
        return !bCryptPasswordEncoder.matches(loginDto.getPassword(), user.getPassword());
    }

    private boolean isChangeUsername(UserEditRequestDto editDto, String myUsername) {
        return !editDto.getUsername().equals(myUsername);
    }

    private void setApplicants(List<Tuple> eventIdParticipants, List<BookmarkedEventSimpleInfoQuery> bookmarkList) {
        Map<Long, Long> eventIdParticipantsMap = eventIdParticipants.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(userEvent.event.id), // eventId
                        tuple -> Optional.ofNullable(tuple.get(userEvent.event.id.count())).orElse(0L)
                ));

        bookmarkList
                .forEach(dto -> dto.setApplicants(eventIdParticipantsMap.getOrDefault(dto.getId(), 0L)));
    }

    private List<Long> mapToEventIdList(List<BookmarkedEventSimpleInfoQuery> bookmarkedEventList) {
        return bookmarkedEventList.stream().map(BookmarkedEventSimpleInfoQuery::getId).toList();
    }

    private void setBookmarkStatus(List<ParticipatingEventSimpleInfoQuery> participatedEvents, List<Long> bookmarkedEventIdSet) {
        participatedEvents
                .forEach(dto -> dto.setBookmarkStatus(bookmarkedEventIdSet.contains(dto.getId()) ? BOOKMARK : NOT_BOOKMARK));
    }

    private List<Long> getParticipatedEventIds(List<ParticipatingEventSimpleInfoQuery> participatedEvents) {
        return participatedEvents.stream()
                .map(ParticipatingEventSimpleInfoQuery::getId).toList();
    }

}