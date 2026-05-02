package nl.codegeneratie.els.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import nl.codegeneratie.els.dtos.TransactionDTO;
import nl.codegeneratie.els.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            summary = "Get all transactions",
            description = "Retrieve a list of all transactions in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Transactions retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TransactionDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public List<TransactionDTO> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    @PostMapping
    @Operation(
            summary = "Create a new transaction",
            description = "Create a new financial transaction (transfer, deposit, withdrawal)"
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
                    description = "Invalid transaction data"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public ResponseEntity<TransactionDTO> createTransaction(@RequestBody TransactionDTO transactionDTO) {
        TransactionDTO createdTransaction = transactionService.createTransaction(transactionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTransaction);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a transaction",
            description = "Update an existing transaction by ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Transaction updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TransactionDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Transaction not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public ResponseEntity<TransactionDTO> updateTransaction(@PathVariable Long id, @RequestBody TransactionDTO transactionDTO) {
        TransactionDTO updatedTransaction = transactionService.updateTransaction(id, transactionDTO);
        return ResponseEntity.ok(updatedTransaction);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a transaction",
            description = "Delete a transaction from the system by ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Transaction deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Transaction not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}

