package lems.cowshed.service;

import lems.cowshed.api.controller.dto.user.request.UserSaveRequestDto;
import lems.cowshed.domain.user.User;
import lems.cowshed.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void JoinProcess(UserSaveRequestDto joinDto) {
        String email = joinDto.getEmail();
        String username = joinDto.getUsername();
        String password = joinDto.getPassword();

        if(userRepository.findByName(username).isPresent()){
            throw new IllegalArgumentException("User name equals exist");
        }

        if(userRepository.findByEmail(email).isPresent()){
            throw new IllegalArgumentException("User email equals exist ");
        }

        User data = User.createUser(email, username, bCryptPasswordEncoder.encode(password),"USER");
        userRepository.save(data);
    }
}
