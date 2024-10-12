package lems.cowshed.api.controller.login;

import lems.cowshed.api.controller.dto.user.join.JoinDto;
import lems.cowshed.api.controller.dto.user.join.JoinResult;
import lems.cowshed.service.JoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JoinController implements JoinSpecification{

    private final JoinService joinservice;

    @PostMapping("/join")
    public JoinResult joinRequest(@RequestBody JoinDto joinDto){
        joinservice.JoinProcess(joinDto);
        return new JoinResult("회원 가입 성공 했습니다");
    }

}
