package nl.codegeneratie.els.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "ATM Session Data Transfer Object - Represents an ATM login session")
public class AtmSessionDTO {
    @Schema(description = "Unique identifier for the ATM session", example = "1")
    private Long id;

    @Schema(description = "Customer ID (user who logged in)", example = "1")
    private Long customer_id;

    @Schema(description = "Login timestamp")
    private LocalDateTime login_time;

    @Schema(description = "Logout timestamp")
    private LocalDateTime logout_time;

    @Schema(description = "Whether the session is currently active", example = "true")
    private boolean active;
}

