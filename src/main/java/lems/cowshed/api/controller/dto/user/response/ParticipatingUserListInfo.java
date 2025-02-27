package lems.cowshed.api.controller.dto.user.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lems.cowshed.domain.user.query.EventParticipantQueryDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "모임에 참여한 회원")
@AllArgsConstructor
public class ParticipatingUserListInfo {

    private List<EventParticipantQueryDto> userList;
}
