package lems.cowshed.service;

import lems.cowshed.api.controller.UserUtils;
import lems.cowshed.api.controller.dto.user.request.UserEditRequestDto;
import lems.cowshed.api.controller.dto.user.request.UserSaveRequestDto;
import lems.cowshed.api.controller.dto.user.response.UserEventResponseDto;
import lems.cowshed.domain.user.User;
import lems.cowshed.domain.user.UserRepository;
import lems.cowshed.domain.user.query.UserEventQueryDto;
import lems.cowshed.domain.user.query.UserQueryRepository;
import lems.cowshed.exception.DuplicateUsernameException;
import lems.cowshed.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserQueryRepository userQueryRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void JoinProcess(UserSaveRequestDto joinDto) {
        String email = joinDto.getEmail();
        String username = joinDto.getUsername();
        String password = joinDto.getPassword();

        if(userRepository.existsByEmailOrUsername(email, username)){
            throw new UserNotFoundException("username or email already exists");
        }

        User user = User.registerUser(username, bCryptPasswordEncoder.encode(password), email, "ROLE_USER");
        userRepository.save(user);
    }

    public void editProcess(UserEditRequestDto userEditRequestDto){
        checkDuplicateName(userEditRequestDto.getUsername());
        User user = getUserById();

        user.modifyDetails(userEditRequestDto);
    }

    public UserEventResponseDto getUserEvent(LocalDate localDate){
        List<UserEventQueryDto> userEventDto = userQueryRepository.findUserWithEvent(UserUtils.getUserId());
        computeAge(userEventDto, localDate.getYear());

        return new UserEventResponseDto(userEventDto);
    }

    private void checkDuplicateName(String username) {
        userRepository.findByUsername(username).ifPresent(u -> {
            throw new DuplicateUsernameException("duplicate user name exist");
        });
    }

    private User getUserById() {
        return userRepository.findById(UserUtils.getUserId()).orElseThrow(() -> new UserNotFoundException("user not found"));
    }

    private void computeAge(List<UserEventQueryDto> userEventDto, int currentYear) {
        userEventDto.forEach(dto -> dto.setAge(currentYear - dto.getBirth().getYear() + 1));
    }

}
