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
@Schema(description = "Bank Account DTO")
public class AccountDTO {
    @NotNull
    @Schema(description = "Account ID", example = "1")
    private Long id;

    @NotNull
    @Schema(description = "Account Owner ID", example = "1")
    private Long customerId;

    @Schema(description = "IBAN", example = "NL91 ABNA 0417 1643 00")
    @NotBlank(message = "IBAN is required")
    private String iban;

    @NotNull
    @Schema(description = "Account Type (SAVINGS, CHECKING, etc.)", example = "CHECKING")
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @NotNull
    @Schema(description = "Account balance", example = "1500.50")
    private BigDecimal balance;

    @NotNull
    @PositiveOrZero
    @Schema(description = "Transaction absolute transfer limit", example = "5000.00")
    private BigDecimal absoluteTransferLimit;

    @NotNull
    @PositiveOrZero
    @Schema(description = "Daily transfer limit", example = "10000.00")
    private BigDecimal dailyTransferLimit;

    @Schema(description = "Account activity status", example = "true")
    private boolean active;

    @NotNull
    @Schema(description = "Account creation timestamp")
    private LocalDateTime createdAt;

    @NotNull
    @Schema(description = "First name of account owner")
    private String firstName;

    @NotNull
    @Schema(description = "Last name of account owner")
    private String lastName;
}