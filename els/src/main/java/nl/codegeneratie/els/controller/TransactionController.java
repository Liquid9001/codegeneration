package nl.codegeneratie.els.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import nl.codegeneratie.els.dtos.TransactionDTO;
import nl.codegeneratie.els.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/transactions")
@Tag(name = "Transactions", description = "Financial transaction management endpoints")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    @Operation(
            summary = "Get all transactions (employee only)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of transactions",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TransactionDTO.class)
                    )
            )
    })
    @PreAuthorize("isAuthenticated()")
    public List<TransactionDTO> getAllTransactions(
            @RequestParam(required = false) Integer offset,
            @RequestParam(required = false) Integer limit,
            @RequestParam(name = "start_date", required = false) LocalDateTime startDate,
            @RequestParam(name = "end_date", required = false) LocalDateTime endDate,
            @RequestParam(name = "min_amount", required = false) BigDecimal minAmount,
            @RequestParam(name = "max_amount", required = false) BigDecimal maxAmount,
            @RequestParam(required = false) String iban
    ) {
        return transactionService.getAllTransactions(offset, limit, startDate, endDate, minAmount, maxAmount, iban);
    }

    @GetMapping("/{transactionId}")
    @Operation(
            summary = "Get transaction details",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Transaction details retrieved",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TransactionDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Transaction not found"
            )
    })
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable Long transactionId) {
        return ResponseEntity.ok(transactionService.getTransactionById(transactionId));
    }
    @PostMapping("/transfer")
    @Operation(summary = "Transfer money between two accounts", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<TransactionDTO> transfer(@RequestBody TransactionDTO transactionDTO) {

        transactionDTO.setTransactionType("TRANSFER");
        TransactionDTO createdTransaction = transactionService.transfer(transactionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTransaction);
    }

    @PostMapping("/deposit")
    @Operation(summary = "Deposit cash into an ATM (Requires receiverIban)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<TransactionDTO> deposit(@RequestBody TransactionDTO transactionDTO) {
        transactionDTO.setTransactionType("DEPOSIT");
        TransactionDTO createdTransaction = transactionService.deposit(transactionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTransaction);
    }

    @PostMapping("/withdrawal")
    @Operation(summary = "Withdraw cash from an ATM (Requires senderIban)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<TransactionDTO> withdrawal(@RequestBody TransactionDTO transactionDTO) {
        transactionDTO.setTransactionType("WITHDRAWAL");
        TransactionDTO createdTransaction = transactionService.withdrawal(transactionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTransaction);
    }
}

