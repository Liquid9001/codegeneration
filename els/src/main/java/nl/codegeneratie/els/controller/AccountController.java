package nl.codegeneratie.els.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import nl.codegeneratie.els.dtos.AccountDTO;
import nl.codegeneratie.els.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@Tag(name = "Accounts", description = "Bank account management endpoints")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    @Operation(
            summary = "Get all accounts",
            description = "Retrieve a list of all bank accounts in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Accounts retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AccountDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public List<AccountDTO> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @PostMapping
    @Operation(
            summary = "Create a new account",
            description = "Create a new bank account for a customer"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Account created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AccountDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid account data"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public ResponseEntity<AccountDTO> createAccount(@RequestBody AccountDTO accountDTO) {
        AccountDTO createdAccount = accountService.createAccount(accountDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update an account",
            description = "Update an existing account by ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Account updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AccountDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public ResponseEntity<AccountDTO> updateAccount(@PathVariable Long id, @RequestBody AccountDTO accountDTO) {
        AccountDTO updatedAccount = accountService.updateAccount(id, accountDTO);
        return ResponseEntity.ok(updatedAccount);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete an account",
            description = "Delete an account from the system by ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Account deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}

