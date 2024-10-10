package lems.cowshed.api.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
/*import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.user.Gender;
import lems.cowshed.domain.user.User;*/
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

    @Getter
    @Setter
    @Schema(description = "마이페이지 회원 정보")
    public static class MyInfoDto{
        @Schema(description = "이름", example = "김철수", required=true)
        private String name;
        @Schema(description = "성별", example = "M", required=true)
        private Gender gender;
        @Schema(description = "생년월일", example = "1999-05-22", required=true)
        private String birth;
        @Schema(description = "성격유형", example = "ISTP")
        private String character;
        @Schema(description = "참여 모임", example = "")
        private List<Event> joinEvents;
        @Schema(description = "북마크 모임", example = "김철수")
        private List<Event> bookmarkEvents;
    }

    @Getter
    @Setter
    @Schema(description = "회원 등록")
    public static class UserSaveRequestDto {
        @Schema(description = "이름", example = "김철수", required=true)
        private String name;
        @Schema(description = "성별", example = "M", required=true)
        private Gender gender;
        @Schema(description = "이메일", example = "cheolsukim@lems.com")
        private String email;
        @Schema(description = "생년월일", example = "1999-05-22", required=true)
        private String birth;
        @Schema(description = "지역명", example = "서울시", required=true)
        private String local_name;
        @Schema(description = "성격유형", example = "ISTP")
        private String character;
        @Schema(description = "소개", example = "성남시 분당구에 사는 직장인입니다.")
        private String introduction;
    }

    @Getter
    @Setter
    @Schema(description = "회원 수정")
    public static class UserUpdateRequestDto {
        @Schema(description = "소개", example = "성남시 분당구에 사는 직장인입니다.")
        private String introduction;
        @Schema(description = "지역명", example = "서울시", required=true)
        private String local_name;
        @Schema(description = "생년월일", example = "1999-05-22", required=true)
        private String birth;
        @Schema(description = "성격유형", example = "ISTP")
        private String character;
    }
}
