package lems.cowshed.api.controller.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Result {

    OK(200, "성공"),
    FAIL(-1, "실패");

    private final int code;
    private final String message;
}
