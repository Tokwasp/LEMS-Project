package lems.cowshed.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.servers.Server;
import lems.cowshed.api.controller.*;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static lems.cowshed.api.controller.ErrorCode.*;

@Configuration
public class SwaggerConfig {
    //metadata for API documentation
    @Value("${lems.openapi.dev-url}")
    private String devUrl;

    @Value("${lems.openapi.prod-url}")
    private String prodUrl;

    @Bean
    public OpenAPI metaData() {
        var info = new Info()
                .title("LEMS API")
                .description("This API exposes endpoints of LEMS application")
                .contact(new Contact()
                        .email("affectionmej@gmail.com")
                        .name("LEMS")
                        .url(prodUrl));
        var devServer = new Server()
                .url(devUrl)
                .description("Server URL in Development environment");
        var prodServer = new Server()
                .url(prodUrl) //null cuz haven't deployed yet
                .description("Server URL in Production environment");

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer, prodServer));
    }

    @Bean
    public GlobalOpenApiCustomizer testHeaderCustom() {
        var parameters = List.of(
                buildParameter("Authorizetion", "JWT 토큰", null)
        );

        // 제외할 경로를 리스트로 정의
        var excludedPaths = List.of("/users/login", "/users/signUp", "/verification/.*", "/users/password-reset");

        return openApi -> {
            var paths = openApi.getPaths();
            for (var entry : paths.entrySet()) {

                String pathName = entry.getKey(); // 경로 이름 가져오기
                var pathItem = entry.getValue();
                var operations = pathItem.readOperations();

                for (var operation : operations) {
                    ApiResponses responses = operation.getResponses();

                    if(excludedPaths.stream().noneMatch(pathName::matches)) {
                        ErrorCode errorCode = CERTIFICATION_ERROR;
                        ExampleHolder exampleHolder = ExampleHolder.builder()
                                .holder(getSwaggerExample(errorCode))
                                .code(errorCode.getHttpStatus().value())
                                .name(errorCode.name())
                                .build();

                        Content content = new Content();
                        MediaType mediaType = new MediaType();
                        ApiResponse apiResponse = new ApiResponse();

                        mediaType.addExamples(
                                exampleHolder.getName(),
                                exampleHolder.getHolder());

                        content.addMediaType("application/json", mediaType);
                        apiResponse.setContent(content);
                        responses.addApiResponse(String.valueOf(errorCode.getCode()), apiResponse);
                    }

                    // 경로가 제외 리스트에 포함되어 있는지 확인
                    if (excludedPaths.stream().noneMatch(pathName::matches)) {
                        for (var parameter : parameters) {
                            operation.addParametersItem(parameter);
                        }
                    }
                }
            }
        };
    }

    @Bean
    public OperationCustomizer customize() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            ApiErrorCodeExamples apiErrorCodeExamples = handlerMethod.getMethodAnnotation(
                    ApiErrorCodeExamples.class);

            // @ApiErrorCodeExamples 어노테이션이 붙어있다면
            if (apiErrorCodeExamples != null) {
                generateErrorCodeResponseExample(operation, apiErrorCodeExamples.value());
            } else {
                ApiErrorCodeExample apiErrorCodeExample = handlerMethod.getMethodAnnotation(
                        ApiErrorCodeExample.class);

                // @ApiErrorCodeExamples 어노테이션이 붙어있지 않고
                // @ApiErrorCodeExample 어노테이션이 붙어있다면
                if (apiErrorCodeExample != null) {
                    generateErrorCodeResponseExample(operation, apiErrorCodeExample.value());
                }
            }

            return operation;
        };
    }

    // 여러 개의 에러 응답값 추가
    private void generateErrorCodeResponseExample(Operation operation, ErrorCode[] errorCodes) {
        ApiResponses responses = operation.getResponses();

        // ExampleHolder(에러 응답값) 객체를 만들고 에러 코드별로 그룹화
        Map<Integer, List<ExampleHolder>> statusWithExampleHolders = Arrays.stream(errorCodes)
                .map(
                        errorCode -> ExampleHolder.builder()
                                .holder(getSwaggerExample(errorCode))
                                .code(errorCode.getHttpStatus().value())
                                .name(errorCode.name())
                                .build()
                )
                .collect(Collectors.groupingBy(ExampleHolder::getCode));

        // ExampleHolders를 ApiResponses에 추가
        addExamplesToResponses(responses, statusWithExampleHolders);
    }

    // 단일 에러 응답값 예시 추가
    private void generateErrorCodeResponseExample(Operation operation, ErrorCode errorCode) {
        ApiResponses responses = operation.getResponses();

        // ExampleHolder 객체 생성 및 ApiResponses에 추가
        ExampleHolder exampleHolder = ExampleHolder.builder()
                .holder(getSwaggerExample(errorCode))
                .name(errorCode.name())
                .code(errorCode.getHttpStatus().value())
                .build();
        addExamplesToResponses(responses, exampleHolder);
    }

    // ErrorResponseDto 형태의 예시 객체 생성
    private Example getSwaggerExample(ErrorCode errorCode) {
        ErrorResponseDto errorResponseDto = ErrorResponseDto.from(errorCode);
        Example example = new Example();
        example.setValue(errorResponseDto);

        return example;
    }

    // exampleHolder를 ApiResponses에 추가
    private void addExamplesToResponses(ApiResponses responses,
                                        Map<Integer, List<ExampleHolder>> statusWithExampleHolders) {
        statusWithExampleHolders.forEach(
                (status, v) -> {
                    Content content = new Content();
                    MediaType mediaType = new MediaType();
                    ApiResponse apiResponse = new ApiResponse();

                    v.forEach(
                            exampleHolder -> mediaType.addExamples(
                                    exampleHolder.getName(),
                                    exampleHolder.getHolder()
                            )
                    );
                    content.addMediaType("application/json", mediaType);
                    apiResponse.setContent(content);
                    responses.addApiResponse(String.valueOf(status), apiResponse);
                }
        );
    }

    private void addExamplesToResponses(ApiResponses responses, ExampleHolder exampleHolder) {
        Content content = new Content();
        MediaType mediaType = new MediaType();
        ApiResponse apiResponse = new ApiResponse();

        mediaType.addExamples(exampleHolder.getName(), exampleHolder.getHolder());
        content.addMediaType("application/json", mediaType);
        apiResponse.content(content);
        responses.addApiResponse(String.valueOf(exampleHolder.getCode()), apiResponse);
    }

    private Parameter buildParameter(String name, String description, String example) {
        return new Parameter()
                .name(name)
                .description(description)
                .in("header") // or "query" depending on where the parameter should be used
                .schema(new StringSchema().example(example));
    }

    private Example toExample(String value) {
        return new Example().value(value);
    }

}
