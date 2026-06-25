package nl.codegeneratie.els.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Set Transfer Limits of one Account DTO")
public class AccountTransferLimitsDTO {
    @NotNull
    @PositiveOrZero
    @Schema(description = "Transaction absolute transfer limit", example = "5000.00")
    private BigDecimal absoluteTransferLimit;

    @NotNull
    @PositiveOrZero
    @Schema(description = "Daily transfer limit", example = "10000.00")
    private BigDecimal dailyTransferLimit;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(
            description = "4 digit PIN for checking account creation",
            example = "1234",
            accessMode = Schema.AccessMode.WRITE_ONLY
    )
    private String pin;
}
