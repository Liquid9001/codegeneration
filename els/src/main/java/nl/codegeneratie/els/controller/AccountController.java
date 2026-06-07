package nl.codegeneratie.els.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import nl.codegeneratie.els.annotations.AccountOwnerEmployeeOnly;
import nl.codegeneratie.els.dtos.AccountDTO;
import nl.codegeneratie.els.dtos.AccountTransferLimitsDTO;
import nl.codegeneratie.els.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
@Tag(name = "Accounts", description = "Bank account management endpoints")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/{accountId}")
    @Operation(
            summary = "Get account details",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Account details retrieved",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AccountDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found"
            )
    })
    @AccountOwnerEmployeeOnly
    public ResponseEntity<AccountDTO> getAccountById(@PathVariable Long accountId) {
        return ResponseEntity.ok(accountService.getAccountById(accountId));
    }

    @PatchMapping("/{accountId}")
    @Operation(
            summary = "Update account transfer limits (employee only)",
            security = @SecurityRequirement(name = "bearerAuth")
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
            )
    })
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<AccountDTO> updateAccountTransferLimits(@PathVariable Long accountId, @Valid @RequestBody AccountTransferLimitsDTO limitsDTO) {
        AccountDTO updatedAccount = accountService.updateAccountTransferLimits(accountId, limitsDTO);
        return ResponseEntity.ok(updatedAccount);
    }

    @DeleteMapping("/{accountId}")
    @Operation(
            summary = "Close a customer account (employee only)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Account closed successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found"
            )
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long accountId) {
        accountService.deleteAccount(accountId);
        return ResponseEntity.noContent().build();
    }
}
