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
import org.springframework.web.bind.annotation.*;

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
            security = @SecurityRequirement(name = "application", scopes = {"read"})
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
    public List<TransactionDTO> getAllTransactions(
            @RequestParam(required = false) Integer offset,
            @RequestParam(required = false) Integer limit,
            @RequestParam(name = "start_date", required = false) LocalDateTime startDate,
            @RequestParam(name = "end_date", required = false) LocalDateTime endDate,
            @RequestParam(name = "min_amount", required = false) Double minAmount,
            @RequestParam(name = "max_amount", required = false) Double maxAmount,
            @RequestParam(required = false) String iban
    ) {
        return transactionService.getAllTransactions(offset, limit, startDate, endDate, minAmount, maxAmount, iban);
    }

    @PostMapping
    @Operation(
            summary = "Create a transaction (customer or employee)",
            security = @SecurityRequirement(name = "application", scopes = {"write"})
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Transaction created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TransactionDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input or limits exceeded"
            )
    })
    public ResponseEntity<TransactionDTO> createTransaction(@RequestBody TransactionDTO transactionDTO) {
        TransactionDTO createdTransaction = transactionService.createTransaction(transactionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTransaction);
    }

    @GetMapping("/{transactionId}")
    @Operation(
            summary = "Get transaction details",
            security = @SecurityRequirement(name = "application", scopes = {"read"})
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
}

