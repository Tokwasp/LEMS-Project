package lems.cowshed.api.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
public class Response {

    @Schema(example = "HttpStatus.OK")
    private HttpStatus httpStatus;
    @Schema(example = "200")
    private int code;
    @Schema(example = "OK")
    private String message;

    public Response(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.code = httpStatus.value();
        this.message = message;
    }

}