package nl.codegeneratie.els.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Set Transfer Limits of both Accounts of a User DTO")
public class UserTransferLimitsDTO {
    @NotNull
    @Schema(description = "Savings account", example = "Transfer limits")
    private AccountTransferLimitsDTO savingsAccount;

    @NotNull
    @Schema(description = "Checking account", example = "Transfer limits")
    private AccountTransferLimitsDTO checkingAccount;
}