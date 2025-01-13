package lems.cowshed.api.controller.user;

import jakarta.validation.Valid;
import lems.cowshed.api.controller.SecurityContextUtil;
import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.api.controller.dto.user.request.UserEditRequestDto;
import lems.cowshed.api.controller.dto.user.request.UserLoginRequestDto;
import lems.cowshed.api.controller.dto.user.request.UserSaveRequestDto;
import lems.cowshed.api.controller.dto.user.response.UserEventResponseDto;
import lems.cowshed.api.controller.dto.user.response.UserMyPageResponseDto;
import lems.cowshed.service.BookmarkService;
import lems.cowshed.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserApiController implements UserSpecification{

    private final UserService userService;
    private final Long userId = SecurityContextUtil.getUserId();

    @GetMapping("/myPage")
    public CommonResponse<UserMyPageResponseDto> findMyPage() {
        UserMyPageResponseDto myPage = userService.findMyPage(userId);
        return CommonResponse.success(myPage);
    }

    @GetMapping("/{event-id}")
    public CommonResponse<UserEventResponseDto> findUserParticipatingInEvent(@PathVariable("event-id") long eventId){
        LocalDate now = LocalDate.now();
        UserEventResponseDto userEvent = userService.findUserParticipatingInEvent(now);
        return CommonResponse.success(userEvent);
    }

    @PostMapping("/signUp")
    public CommonResponse<Void> signUp(@Valid @RequestBody UserSaveRequestDto userSaveRequestDto) {
        userService.signUp(userSaveRequestDto);
        return CommonResponse.success();
    }

    @PostMapping("/login")
    public CommonResponse<Void> login(@Valid @RequestBody UserLoginRequestDto userLoginRequestDto){
        userService.login(userLoginRequestDto);
        return CommonResponse.success();
    }

    @PatchMapping
    public CommonResponse<Void> editUser(@RequestBody UserEditRequestDto userEditRequestDto){
        userService.editUser(userEditRequestDto, userId);
        return CommonResponse.success();
    }

}