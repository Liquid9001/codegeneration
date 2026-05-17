package nl.codegeneratie.els.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Login request payload")
public class LoginRequestDTO {
    @Schema(description = "User email", example = "john.doe@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Schema(description = "Plain text password", example = "StrongPass123!", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}

