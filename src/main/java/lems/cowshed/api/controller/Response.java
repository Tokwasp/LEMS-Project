package lems.cowshed.api.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
public class Response {

    private final HttpStatus httpStatus;
    private final int code;
    private String message;

    public Response(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.code = httpStatus.value();
        this.message = message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}