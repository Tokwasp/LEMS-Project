package lems.cowshed.service;

import lems.cowshed.api.controller.dto.user.request.UserEditRequestDto;
import lems.cowshed.api.controller.dto.user.request.UserLoginRequestDto;
import lems.cowshed.api.controller.dto.user.request.UserSaveRequestDto;
import lems.cowshed.api.controller.dto.user.response.UserEventResponseDto;
import lems.cowshed.api.controller.dto.user.response.UserMyPageResponseDto;
import lems.cowshed.api.controller.dto.user.response.UserResponseDto;
import lems.cowshed.domain.event.query.MyPageBookmarkedEventQueryDto;
import lems.cowshed.domain.user.Role;
import lems.cowshed.domain.user.User;
import lems.cowshed.domain.user.UserRepository;
import lems.cowshed.domain.event.query.MyPageParticipatingEventQueryDto;
import lems.cowshed.domain.user.query.EventParticipantQueryDto;
import lems.cowshed.domain.user.query.UserQueryRepository;
import lems.cowshed.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static lems.cowshed.exception.Message.*;
import static lems.cowshed.exception.Reason.*;

@RequiredArgsConstructor
@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserQueryRepository userQueryRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserMyPageResponseDto findMyPage(Long userId) {
        List<Long> participatedEventIdList = userQueryRepository.getParticipatedEvent(userId);
        UserMyPageResponseDto myPageDto = userQueryRepository.findUserForMyPage(userId, participatedEventIdList);
        Set<Long> bookmarkEventIdSet = userQueryRepository.getBookmark(userId, participatedEventIdList);

        List<MyPageParticipatingEventQueryDto> userEventList = myPageDto.getUserEventList();
        checkBookmarked(userEventList, bookmarkEventIdSet);

        List<MyPageBookmarkedEventQueryDto> bookmarkList = myPageDto.getBookmarkList();
        List<Long> bookmarkEventIdList = bookmarkList.stream().map(MyPageBookmarkedEventQueryDto::getId).toList();
        Map<Long, Long> eventIdParticipantsMap = userQueryRepository.getParticipatedEventIdSet(bookmarkEventIdList);
        setApplicants(bookmarkList, eventIdParticipantsMap);
        return myPageDto;
    }

    public UserEventResponseDto findUserParticipatingInEvent(LocalDate currentYear, Long userId){
        List<EventParticipantQueryDto> userEventDtoList = userQueryRepository.findUserParticipatingInEvent(userId);
        calculateAndSetDtoAge(currentYear, userEventDtoList);

        return new UserEventResponseDto(userEventDtoList);
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

    public UserResponseDto findUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_ID, USER_NOT_FOUND));

        return UserResponseDto.from(user);
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

    private void checkBookmarked(List<MyPageParticipatingEventQueryDto> userEventList, Set<Long> bookmarkEventIdSet) {
        userEventList.stream().forEach(dto -> {
            if(bookmarkEventIdSet.contains(dto.getId())){
                dto.statusBookmark();
            }
            else{
                dto.statusNotBookmark();
            }
        });
    }

    private void setApplicants(List<MyPageBookmarkedEventQueryDto> bookmarkList, Map<Long, Long> eventIdParticipantsMap) {
        bookmarkList.stream().forEach(
                dto -> dto.changeApplicants(eventIdParticipantsMap.getOrDefault(dto.getId(), 0L))
        );
    }
}