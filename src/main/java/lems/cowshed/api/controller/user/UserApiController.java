package lems.cowshed.api.controller.user;

import lems.cowshed.api.controller.dto.CommonResponse;
import lems.cowshed.api.controller.dto.user.request.UserEditRequestDto;
import lems.cowshed.api.controller.dto.user.request.UserLoginRequestDto;
import lems.cowshed.api.controller.dto.user.request.UserSaveRequestDto;
import lems.cowshed.api.controller.dto.user.response.UserEventResponseDto;
import lems.cowshed.api.controller.dto.user.response.UserMyPageResponseDto;
import lems.cowshed.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserApiController implements UserSpecification{

    private final UserService userService;

    @PostMapping("/login")
    public CommonResponse<Void> login(@RequestBody UserLoginRequestDto userLoginRequestDto){
        return CommonResponse.customMessage("로그인 성공");
    }

    @GetMapping
    public CommonResponse<UserMyPageResponseDto> userMyPage(){
        return null;
    }

    @PostMapping("/register")
    public CommonResponse<Void> saveUser(@RequestBody UserSaveRequestDto userSaveRequestDto) {
        userService.JoinProcess(userSaveRequestDto);
        return CommonResponse.success();
    }

    @PatchMapping
    public CommonResponse<Void> editUser(@RequestBody UserEditRequestDto userEditRequestDto){
        userService.editProcess(userEditRequestDto);
        return CommonResponse.customMessage("유저 변경 성공");
    }

    @GetMapping("/events")
    public CommonResponse<UserEventResponseDto> findUserEvent(){
        LocalDate now = LocalDate.now();
        UserEventResponseDto userEvent = userService.getUserEvent(now);
        return CommonResponse.customMessage(userEvent, "유저 이벤트 조회 성공");
    }
}

