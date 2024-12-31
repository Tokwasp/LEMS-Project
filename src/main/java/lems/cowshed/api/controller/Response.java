package lems.cowshed.api.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
public class Response {

    @Schema(example = "HttpStatus.OK")
    private final HttpStatus httpStatus;
    @Schema(example = "200")
    private final int code;
    @Schema(example = "OK")
    private final String message;

    public Response(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.code = httpStatus.value();
        this.message = message;
    }

}