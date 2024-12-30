package lems.cowshed.config.swagger;

import io.swagger.v3.oas.models.examples.Example;
import lems.cowshed.api.controller.ErrorCode;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ExampleHolder {

    private Example holder;
    private ErrorCode errorCode;
    private int code;
    private String name;

    @Builder
    private ExampleHolder(Example holder, ErrorCode errorCode, int code, String name) {
        this.holder = holder;
        this.errorCode = errorCode;
        this.code = code;
        this.name = name;
    }
}
