package lems.cowshed.api.controller.dto.user.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lems.cowshed.domain.user.query.UserEventQueryDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "유저에 속한 이벤트")
@AllArgsConstructor
public class UserEventResponseDto {

    private List<UserEventQueryDto> user;
}
