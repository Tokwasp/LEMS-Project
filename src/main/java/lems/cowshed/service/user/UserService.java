package lems.cowshed.service.user;

import lems.cowshed.domain.mail.Mail;
import lems.cowshed.domain.mail.code.CodeFinder;
import lems.cowshed.domain.mail.code.CodeType;
import lems.cowshed.dto.user.request.UserModifyRequest;
import lems.cowshed.dto.user.request.UserLoginRequest;
import lems.cowshed.dto.user.request.UserSaveRequest;
import lems.cowshed.dto.user.response.UserMyPageInfo;
import lems.cowshed.dto.user.response.UserInfo;
import lems.cowshed.global.exception.BusinessException;
import lems.cowshed.global.exception.NotFoundException;
import lems.cowshed.repository.event.query.BookmarkedEventSimpleInfoQuery;
import lems.cowshed.repository.event.query.EventQueryRepository;
import lems.cowshed.repository.event.query.ParticipatingEventSimpleInfoQuery;
import lems.cowshed.domain.user.Role;
import lems.cowshed.domain.user.User;
import lems.cowshed.repository.user.UserRepository;
import lems.cowshed.repository.user.query.MyPageUserQueryDto;
import lems.cowshed.repository.user.query.UserQueryRepository;
import lems.cowshed.service.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

import static lems.cowshed.domain.bookmark.BookmarkStatus.*;
import static lems.cowshed.global.exception.Message.*;
import static lems.cowshed.global.exception.Reason.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {

    private final MailService mailService;
    private final CodeFinder codeFinder;
    private final UserRepository userRepository;
    private final UserQueryRepository userQueryRepository;
    private final EventQueryRepository eventQueryRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public void signUp(UserSaveRequest request) {
        if(userRepository.existsByEmailOrUsername(request.getEmail(), request.getUsername())){
            throw new BusinessException(USERNAME_OR_EMAIL, USERNAME_OR_EMAIL_EXIST);
        }

        User user = request.toEntityForRegister(bCryptPasswordEncoder, Role.ROLE_USER);
        userRepository.save(user);
    }

    public void login(UserLoginRequest request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException(USER_EMAIL, USER_NOT_FOUND));

        if(isPasswordValidationFail(request, user)){
            throw new BusinessException(USER_PASSWORD, USER_ID_PASSWORD_CHECK);
        }
    }

    //TODO 회원 수정 정보 JWT 반환
    @Transactional
    public void editUser(UserModifyRequest request, Long userId){
        checkDuplicatedUsername(request);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_ID, USER_NOT_FOUND));

        user.modify(
                request.getUsername(),
                request.getIntroduction(),
                request.getLocation(),
                request.getBirth(),
                request.getMbti()
        );
    }

    @Transactional
    public void sendTemporaryPasswordToEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(USER_EMAIL, USER_EMAIL_NOT_FOUND));

        String password = codeFinder.findCodeFrom(CodeType.PASSWORD);
        mailService.sendCodeToMail(CodeType.PASSWORD, Mail.of(email, password));
        user.modifyPassword(bCryptPasswordEncoder.encode(password));
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_ID, USER_NOT_FOUND));

        userRepository.delete(user);
    }

    public boolean isDuplicatedUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public boolean isExistEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public UserMyPageInfo findMyPage(Long userId) {
        MyPageUserQueryDto userDto = userQueryRepository.findUser(userId);

        List<ParticipatingEventSimpleInfoQuery> participatedEvents = eventQueryRepository
                .findEventsParticipatedByUserWithApplicants(eventQueryRepository.getEventIdsParticipatedByUser(userId, PageRequest.of(0, 5)));
        setBookmarkStatus(participatedEvents, eventQueryRepository.getBookmarkedEventIdSet(userId, getParticipatedEventIds(participatedEvents)));

        List<BookmarkedEventSimpleInfoQuery> bookmarkedEventList = eventQueryRepository
                .findBookmarkedEventsFromUser(userId, PageRequest.of(0, 5));

        Map<Long, Long> participantsCountByGroupId = eventQueryRepository.findEventParticipantCountByEventIds(mapToEventIdList(bookmarkedEventList));
        setApplicants(participantsCountByGroupId, bookmarkedEventList);

        return UserMyPageInfo.of(userDto, participatedEvents, bookmarkedEventList);
    }

    public UserInfo findUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_ID, USER_NOT_FOUND));

        return UserInfo.from(user);
    }

    private void checkDuplicatedUsername(UserModifyRequest request) {
        userRepository.findByUsername(request.getUsername())
                .ifPresent(u -> {
                    throw new BusinessException(USER_NAME, USERNAME_EXIST);
                });
    }

    private boolean isPasswordValidationFail(UserLoginRequest loginDto, User user) {
        return !bCryptPasswordEncoder.matches(loginDto.getPassword(), user.getPassword());
    }

    private void setApplicants(Map<Long,Long> participantsCountByGroupId, List<BookmarkedEventSimpleInfoQuery> bookmarkList) {
        bookmarkList
                .forEach(dto -> dto.setApplicants(participantsCountByGroupId.getOrDefault(dto.getId(), 0L)));
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