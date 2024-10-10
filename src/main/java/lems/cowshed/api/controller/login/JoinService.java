package lems.cowshed.api.controller.login;

import lems.cowshed.domain.user.User;
import lems.cowshed.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class JoinService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void JoinProcess(JoinDto joinDto) {
        String username = joinDto.getUsername();
        String password = joinDto.getPassword();

        if(userRepository.findByName(username).isPresent()){
            new IllegalArgumentException("User name equals exist");
        };

        User data = User.createUser(username, bCryptPasswordEncoder.encode(password),"ROLE_ADMIN");
        userRepository.save(data);
    }
}
