package lems.cowshed.api.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.user.Gender;
import lems.cowshed.domain.user.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name="user-controller", description="회원 API")
@RestController
@RequestMapping("/users")
public class UserApiController implements UserSpecification{

    @GetMapping("/event/{eventId}")
    public List<EventUserDto> getUserByEvent(@Parameter(name="eventId", description = "모임 ID", example = "1") @PathVariable("eventID") Long eventId){
        //return List<EventUserDto>
        EventUserDto eventUserDto = new EventUserDto();
        eventUserDto.setName("kim");
        eventUserDto.setGender(Gender.W);
        eventUserDto.setIntroduction("안녕하세요 kim 입니다.");

        List<EventUserDto> list = new ArrayList<>();
        list.add(eventUserDto);

        return list;
    }

    @GetMapping("/my-page/{userId}")
    public void getMyInfoById(@Parameter(name = "userId", description = "회원 ID", example = "1") @PathVariable("userId") Long userId){
        //return MyInfoDto
    }

    @PostMapping("/")
    public void saveUser(@RequestBody UserSaveRequestDto userSaveRequestDto){
        //return Long
    }

   @PatchMapping("/{userId}")
    public void editUser(@Parameter(name = "userId", description = "회원 ID", example = "1") @PathVariable("userId") Long userId, @RequestBody UserUpdateRequestDto userUpdateDto){
        //return Long
    }

}
