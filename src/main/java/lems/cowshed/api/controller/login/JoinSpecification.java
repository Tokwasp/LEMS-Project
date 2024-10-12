package lems.cowshed.api.controller.login;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lems.cowshed.api.controller.dto.user.join.JoinDto;
import lems.cowshed.api.controller.dto.user.join.JoinResult;
import org.springframework.web.bind.annotation.RequestBody;

public interface JoinSpecification {

    @Operation(summary = "회원 가입", description = "이메일/비밀번호 + 닉네임을 통해 회원 가입을 합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "⭕ SUCCESS 회원 가입에 성공 했습니다.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = JoinResult.class))),
            })
    JoinResult joinRequest(@RequestBody JoinDto joinDto);
}
