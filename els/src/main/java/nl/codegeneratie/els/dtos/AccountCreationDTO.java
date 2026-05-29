package nl.codegeneratie.els.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import nl.codegeneratie.els.domain.enums.AccountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "Bank Account Creation DTO")
public class AccountCreationDTO {
    @NotNull
    @Schema(description = "Account Type (SAVINGS, CHECKING, etc.)", example = "CHECKING")
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @NotNull
    @PositiveOrZero
    @Schema(description = "Transaction absolute transfer limit", example = "5000.00")
    private BigDecimal absoluteTransferLimit;

    @NotNull
    @PositiveOrZero
    @Schema(description = "Daily transfer limit", example = "10000.00")
    private BigDecimal dailyTransferLimit;
}