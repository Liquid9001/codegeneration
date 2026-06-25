package nl.codegeneratie.els.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Debit card response")
public class DebitCardDTO {
    @Schema(description = "Debit card ID", example = "1")
    private Long id;

    @Schema(description = "Account ID this debit card belongs to", example = "5")
    private Long accountId;

    @Schema(description = "Public card number for this account", example = "1")
    private int publicCardNumber;

    @Schema(description = "Debit card creation timestamp")
    private LocalDateTime createdAt;
}
