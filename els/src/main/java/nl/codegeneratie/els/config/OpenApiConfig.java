package nl.codegeneratie.els.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ELS - Electronic Banking System API")
                        .version("1.0.0")
                        .description("Comprehensive API documentation for the Electronic Banking System (ELS). " +
                                "This system manages users, accounts, transactions, and ATM sessions for electronic banking.")
                        .contact(new Contact()
                                .name("Support Team")
                                .email("support@els.nl")
                                .url("https://els.nl/support"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(Arrays.asList(
                        new Server()
                                .url("http://localhost:8081")
                                .description("Development Server")
                ));
    }
}

