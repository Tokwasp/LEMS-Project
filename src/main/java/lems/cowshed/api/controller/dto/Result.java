package lems.cowshed.api.controller.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Result {

    OK("성공");

    private final String message;
}
