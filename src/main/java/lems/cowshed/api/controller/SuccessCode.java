package lems.cowshed.api.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {

    SUCCESS(HttpStatus.OK, HttpStatus.OK.value(), "OK");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
