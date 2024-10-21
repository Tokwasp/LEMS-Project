package lems.cowshed.api.controller.user;

import lems.cowshed.api.controller.dto.CommonResponse;
import lems.cowshed.api.controller.dto.user.request.UserEditRequestDto;
import lems.cowshed.api.controller.dto.user.request.UserSaveRequestDto;
import lems.cowshed.api.controller.dto.user.response.UserMyPageResponseDto;
import lems.cowshed.api.controller.dto.user.query.UserEventQueryDto;
import lems.cowshed.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserApiController implements UserSpecification{

    private final UserService userService;

    @GetMapping
    public CommonResponse<UserMyPageResponseDto> userMyPage(){
        return null;
    }

    @PostMapping("/register")
    public CommonResponse<Void> saveUser(@RequestBody UserSaveRequestDto userSaveRequestDto) {
        userService.JoinProcess(userSaveRequestDto);
        return null;
    }

    @PatchMapping
    public CommonResponse<Void> editUser(@RequestBody UserEditRequestDto UserEditRequestDto){
        return null;
    }

    @GetMapping("/{eventId}")
    public CommonResponse<List<UserEventQueryDto>> getUserByEvent(@PathVariable("eventID") Long eventId){
        return null;
    }
}

