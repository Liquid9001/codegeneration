package nl.codegeneratie.els.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "Transaction Data Transfer Object - Represents a financial transaction")
public class TransactionDTO {
    @Schema(description = "Unique transaction identifier", example = "1")
    private Long id;

    @Schema(description = "Source account ID", example = "1")
    private Long from_account_id;

    @Schema(description = "Destination account ID", example = "2")
    private Long to_account_id;

    @Schema(description = "User who initiated the transaction", example = "1")
    private Long initiated_by_user_id;

    @Schema(description = "Transaction amount", example = "100.50")
    private BigDecimal amount;

    @Schema(description = "Type of transaction (TRANSFER, DEPOSIT, WITHDRAWAL, etc.)", example = "TRANSFER")
    private String transaction_type;

    @Schema(description = "Currency code", example = "EUR")
    private String currency;

    @Schema(description = "Transaction timestamp")
    private LocalDateTime timestamp;

    @Schema(description = "Transaction status (PENDING, COMPLETED, FAILED, REJECTED)", example = "COMPLETED")
    private String status;

    @Schema(description = "Transaction description or notes", example = "Payment for invoice #12345")
    private String description;
}

