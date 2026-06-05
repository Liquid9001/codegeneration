package nl.codegeneratie.els.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        OpenAPI openAPI = new OpenAPI()
                .info(new Info()
                        .title("Project Code Generation")
                        .version("1.0.0")
                        .description("API Design Project Code Generation"));

        // Add HTTP Bearer token security scheme
        SecurityScheme bearerScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT token obtained from /users/login endpoint (use email and password)");

        if (openAPI.getComponents() == null) {
            openAPI.setComponents(new io.swagger.v3.oas.models.Components());
        }
        openAPI.getComponents().addSecuritySchemes("bearerAuth", bearerScheme);

        // Add tags
        openAPI.tags(Arrays.asList(
                new Tag().name("Users").description("User registration, login, approval, and user details"),
                new Tag().name("Accounts").description("Bank account management endpoints"),
                new Tag().name("Transactions").description("Financial transaction management endpoints"),
                new Tag().name("Customers").description("Customer search endpoints")
        ));

        return openAPI;
    }
}

