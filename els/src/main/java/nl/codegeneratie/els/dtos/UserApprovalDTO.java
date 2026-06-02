package nl.codegeneratie.els.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "User Approval DTO")
public class UserApprovalDTO {
    @NotNull
    @Schema(description = "Savings account", example = "Iban, transfer limits")
    private AccountCreationDTO savingsAccount;

    @NotNull
    @Schema(description = "Checking account", example = "Iban, transfer limits")
    private AccountCreationDTO checkingAccount;

}