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

    @Schema(description = "IBAN of the sender account", example = "NL91ABNA0417164300")
    private String senderIban;

    @Schema(description = "IBAN of the receiver account", example = "NL91ABNA0417164301")
    private String receiverIban;

    @Schema(description = "User who initiated the transaction", example = "1")
    private Long initiatedByUserId;

    @Schema(description = "Transaction amount", example = "100.50")
    private BigDecimal amount;

    @Schema(description = "Type of transaction (TRANSFER, DEPOSIT, WITHDRAWAL, etc.)", example = "TRANSFER")
    private String transactionType;

    @Schema(description = "Currency code", example = "EUR")
    private String currency;

    @Schema(description = "Transaction timestamp")
    private LocalDateTime timestamp;

    @Schema(description = "Transaction status (PENDING, COMPLETED, FAILED, REJECTED)", example = "COMPLETED")
    private String status;

    @Schema(description = "Transaction description or notes", example = "Payment for invoice #12345")
    private String description;
}

