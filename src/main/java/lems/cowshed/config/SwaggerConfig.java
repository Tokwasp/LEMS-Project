package lems.cowshed.config;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
                buildParameter("Authentication", "JWT 토큰", null)
        );

        // 제외할 경로를 리스트로 정의
        var excludedPaths = List.of("/login", "/join");
        var registerPath = List.of("/user/register");

        return openApi -> {
            var paths = openApi.getPaths();
            for (var entry : paths.entrySet()) {

                String pathName = entry.getKey(); // 경로 이름 가져오기
                var pathItem = entry.getValue();
                var operations = pathItem.readOperations();

                for (var operation : operations) {
                    ApiResponses responses = operation.getResponses();

                    responses.addApiResponse("403", new ApiResponse().description("인증 되지 않은 사용자 입니다."));

                    // 경로가 제외 리스트에 포함되어 있는지 확인
                    if (excludedPaths.stream().noneMatch(pathName::startsWith)) {
                        for (var parameter : parameters) {
                            operation.addParametersItem(parameter);
                        }
                    }
                }
            }
        };
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
