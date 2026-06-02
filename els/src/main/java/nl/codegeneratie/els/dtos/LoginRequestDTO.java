package nl.codegeneratie.els.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Login request payload")
public class LoginRequestDTO {
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Schema(description = "User email", example = "john.doe@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "Password is required")
    @Schema(description = "Plain text password", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}

