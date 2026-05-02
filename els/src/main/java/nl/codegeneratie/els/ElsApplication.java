package nl.codegeneratie.els;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(info = @Info(title = "ELS API", version = "1.0", description = "API for ELS"))
@SpringBootApplication
public class ElsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ElsApplication.class, args);
	}
}
