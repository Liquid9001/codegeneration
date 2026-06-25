package nl.codegeneratie.els.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "Update checking account PIN request")
public class AccountOverwritePinDTO {
    @NotBlank(message = "PIN is required.")
    @Pattern(regexp = "\\d{4}", message = "PIN must be exactly 4 digits.")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(
            description = "New 4 digit PIN for a checking account",
            example = "4321",
            accessMode = Schema.AccessMode.WRITE_ONLY
    )
    private String pin;
}
