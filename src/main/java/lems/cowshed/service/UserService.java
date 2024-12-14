package lems.cowshed.service;

import lems.cowshed.api.controller.SecurityContextUtil;
import lems.cowshed.api.controller.dto.user.request.UserEditRequestDto;
import lems.cowshed.api.controller.dto.user.request.UserLoginRequestDto;
import lems.cowshed.api.controller.dto.user.request.UserSaveRequestDto;
import lems.cowshed.api.controller.dto.user.response.UserEventResponseDto;
import lems.cowshed.domain.user.Role;
import lems.cowshed.domain.user.User;
import lems.cowshed.domain.user.UserRepository;
import lems.cowshed.domain.user.query.UserEventQueryDto;
import lems.cowshed.domain.user.query.UserQueryRepository;
import lems.cowshed.exception.UserEditException;
import lems.cowshed.exception.UserRegisterException;
import lems.cowshed.exception.UserLoginException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserQueryRepository userQueryRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void login(UserLoginRequestDto loginDto){
         String email = loginDto.getEmail();
         User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserLoginException(NOT_FOUND, email, email + " 유저를 찾을수 없습니다."));

        if(isPasswordValidationFail(loginDto, user)){
            throw new UserLoginException(BAD_REQUEST, "password", "아이디와 비밀번호를 다시 확인 해주세요");
        }
    }

    public void JoinProcess(UserSaveRequestDto saveDto) {
        if(isExistDuplicateEmailOrUsername(saveDto)){
            throwException(saveDto);
        }

        final User user = saveDto.toEntityForRegister(bCryptPasswordEncoder, Role.ROLE_USER);
        userRepository.save(user);
    }

    public void editProcess(UserEditRequestDto editDto, Long userId){
        userRepository.findByUsername(editDto.getUsername())
                .ifPresent(u -> throwException(editDto));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserEditException(NOT_FOUND, String.valueOf(userId), "해당하는 유저를 찾을수 없습니다."));

        user.modifyContents(editDto);
    }

    public UserEventResponseDto getUserEvent(LocalDate currentYear){
        List<UserEventQueryDto> userEventDtoList = userQueryRepository.findUserWithEvent(SecurityContextUtil.getUserId());
        computeAge(userEventDtoList, currentYear.getYear());

        return new UserEventResponseDto(userEventDtoList);
    }

    private boolean isPasswordValidationFail(UserLoginRequestDto loginDto, User user) {
        System.out.println(loginDto.getPassword() + "정보 " + user.getPassword());
        return !bCryptPasswordEncoder.matches(loginDto.getPassword(), user.getPassword());
    }

    private boolean isExistDuplicateEmailOrUsername(UserSaveRequestDto saveDto) {
        return userRepository.existsByEmailOrUsername(saveDto.getEmail(), saveDto.getUsername());
    }

    private void throwException(UserSaveRequestDto saveDto) {
        throw new UserRegisterException(BAD_REQUEST, "usernameOrEmail",saveDto.getEmail() + " " + saveDto.getUsername() + "은 이미 존재 하는 이름 혹은 이메일 입니다.");
    }

    private void throwException(UserEditRequestDto editDto) {
        throw new UserEditException(BAD_REQUEST, "username", editDto.getUsername() + " 는 이미 존재 하는 닉네임 입니다.");
    }

    private void computeAge(List<UserEventQueryDto> userEventDto, int currentYear) {
        userEventDto.forEach(dto -> dto.setAge(currentYear - dto.getBirth().getYear() + 1));
    }
}
