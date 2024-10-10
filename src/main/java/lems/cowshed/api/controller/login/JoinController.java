package lems.cowshed.api.controller.login;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JoinController {

    private final JoinService joinservice;

    @PostMapping("/join")
    public String joinProcess(@RequestBody JoinDto joinDto){
        joinservice.JoinProcess(joinDto);
        return "ok";
    }

}
