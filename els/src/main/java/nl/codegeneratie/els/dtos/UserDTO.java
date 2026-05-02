package nl.codegeneratie.els.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "User Data Transfer Object - Represents a user in the ELS system")
public class UserDTO {
    @Schema(description = "Unique identifier for the user", example = "1")
    private Long id;

    @Schema(description = "User's email address (unique)", example = "john.doe@example.com")
    private String email;

    @Schema(description = "User's first name", example = "John")
    private String first_name;

    @Schema(description = "User's last name", example = "Doe")
    private String last_name;

    @Schema(description = "User's phone number", example = "0612345678")
    private Integer phone_number;

    @Schema(description = "Dutch BSN (Burgerservicenummer)", example = "123456789")
    private Integer bsn;

    @Schema(description = "User role (0=customer, 1=employee, 2=admin)", example = "0")
    private Integer role;

    @Schema(description = "Whether the user has been approved", example = "true")
    private boolean approved;

    @Schema(description = "Account creation timestamp")
    private LocalDateTime created_at;
}

