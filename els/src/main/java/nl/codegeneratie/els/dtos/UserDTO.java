package nl.codegeneratie.els.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import nl.codegeneratie.els.domain.enums.UserRole;

import java.time.LocalDateTime;

@Data
@Schema(description = "User Data Transfer Object - Represents a user in the ELS system")
public class UserDTO {
    @Schema(description = "Unique identifier for the user", example = "1")
    private Long id;

    @Schema(description = "User's email address (unique)", example = "john.doe@example.com")
    private String email;

    @Schema(
            description = "Plain text password; hashing done by backend",
            accessMode = Schema.AccessMode.WRITE_ONLY,
            example = "password123"
    )
    private String password;

    @Schema(description = "User's first name", example = "John")
    private String firstName;

    @Schema(description = "User's last name", example = "Doe")
    private String lastName;

    @Schema(description = "User's phone number", example = "0612345678")
    private Integer phoneNumber;

    @Schema(description = "Dutch BSN (Burgerservicenummer)", example = "123456789")
    private Integer bsn;

    @Schema(description = "User role (customer, employee, admin)", example = "CUSTOMER")
    private UserRole role;

    @Schema(description = "Whether the user has been approved", example = "true")
    private boolean approved;

    @Schema(description = "Account creation timestamp")
    private LocalDateTime createdAt;
}

