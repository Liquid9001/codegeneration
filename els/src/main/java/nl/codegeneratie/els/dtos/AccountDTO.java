package nl.codegeneratie.els.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "Account Data Transfer Object - Represents a bank account")
public class AccountDTO {
    @Schema(description = "Unique identifier for the account", example = "1")
    private Long id;

    @Schema(description = "Customer ID (owner of the account)", example = "1")
    private Long customer_id;

    @Schema(description = "International Bank Account Number (IBAN)", example = "NL91 ABNA 0417 1643 00")
    private String iban;

    @Schema(description = "Type of account (SAVINGS, CHECKING, etc.)", example = "CHECKING")
    private String account_type;

    @Schema(description = "Current account balance", example = "1500.50")
    private BigDecimal balance;

    @Schema(description = "Absolute transfer limit per transaction", example = "5000.00")
    private BigDecimal absolute_transfer_limit;

    @Schema(description = "Daily transfer limit", example = "10000.00")
    private BigDecimal daily_transfer_limit;

    @Schema(description = "Whether the account is active", example = "true")
    private boolean active;

    @Schema(description = "Account creation timestamp")
    private LocalDateTime created_at;
}

