package lems.cowshed;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;

public class SwaggerConfig {

    @Bean
    public GroupedOpenApi testAdminApi() {
        return GroupedOpenApi.builder()
                .group("user")
                .pathsToMatch("/user/**")
                .build();
    }

    @Bean
    public GroupedOpenApi testController() {
        return GroupedOpenApi.builder()
                .group("club")
                .pathsToMatch("/clubs/**")
                .build();
    }
}
