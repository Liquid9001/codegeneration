package nl.codegeneratie.els.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
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

        // Add OAuth2 security scheme
        SecurityScheme oauthScheme = new SecurityScheme()
                .type(SecurityScheme.Type.OAUTH2)
                .flows(new OAuthFlows()
                        .clientCredentials(new OAuthFlow()
                                .tokenUrl("http://example.com/oauth/token")
                                .scopes(new Scopes()
                                        .addString("read", "allows reading resources")
                                        .addString("write", "allows modifying resources"))));

        if (openAPI.getComponents() == null) {
            openAPI.setComponents(new io.swagger.v3.oas.models.Components());
        }
        openAPI.getComponents().addSecuritySchemes("application", oauthScheme);
        openAPI.addSecurityItem(new SecurityRequirement().addList("application", Arrays.asList("read", "write")));

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

