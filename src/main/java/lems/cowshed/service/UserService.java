package lems.cowshed.service;

import lems.cowshed.api.controller.UserUtils;
import lems.cowshed.api.controller.dto.user.request.UserEditRequestDto;
import lems.cowshed.api.controller.dto.user.request.UserSaveRequestDto;
import lems.cowshed.api.controller.dto.user.response.UserEventResponseDto;
import lems.cowshed.domain.user.User;
import lems.cowshed.domain.user.UserRepository;
import lems.cowshed.domain.user.query.UserEventQueryDto;
import lems.cowshed.domain.user.query.UserQueryRepository;
import lems.cowshed.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserQueryRepository userQueryRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void JoinProcess(UserSaveRequestDto joinDto) {
        String email = joinDto.getEmail();
        String username = joinDto.getUsername();
        String password = joinDto.getPassword();

        if(userRepository.findByEmail(email).isPresent() || userRepository.findByName(username).isPresent()){
            throw new UserNotFoundException("User name or email equals exist ");
        }

        User user = User.registerUser(username, bCryptPasswordEncoder.encode(password), email, "ROLE_USER");
        userRepository.save(user);
    }

    public void editProcess(UserEditRequestDto userEditRequestDto){
        User findUser = userRepository.findById(UserUtils.getUserId()).orElseThrow(() -> new UserNotFoundException("user not found"));
        findUser.setEditUser(userEditRequestDto);
    }

    public UserEventResponseDto getUserEvent(){
        List<UserEventQueryDto> userEventDto = userQueryRepository.findUserWithEvent(UserUtils.getUserId());
        int currentYear = LocalDate.now().getYear();
        userEventDto.forEach(dto -> dto.setAge(currentYear - dto.getBirth().getYear() + 1));

        return new UserEventResponseDto(userEventDto);
    }

}
