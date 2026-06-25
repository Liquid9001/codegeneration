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
import nl.codegeneratie.els.dtos.AccountOverwritePinDTO;
import nl.codegeneratie.els.dtos.AccountTransferLimitsDTO;
import nl.codegeneratie.els.dtos.DebitCardDTO;
import nl.codegeneratie.els.service.AccountService;
import nl.codegeneratie.els.service.DebitCardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
@Tag(name = "Accounts", description = "Bank account management endpoints")
public class AccountController {

    private final AccountService accountService;
    private final DebitCardService debitCardService;

    public AccountController(AccountService accountService, DebitCardService debitCardService) {
        this.accountService = accountService;
        this.debitCardService = debitCardService;
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

    @PatchMapping("/{accountId}/pin")
    @Operation(
            summary = "Update checking account PIN (employee only)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "PIN updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AccountDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid PIN or account type"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found"
            )
    })
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<AccountDTO> updateCheckingAccountPin(@PathVariable Long accountId, @Valid @RequestBody AccountOverwritePinDTO overwritePinDTO) {
        AccountDTO updatedAccount = accountService.updateCheckingAccountPin(accountId, overwritePinDTO);
        return ResponseEntity.ok(updatedAccount);
    }

    @PostMapping("/{accountId}/cards")
    @Operation(
            summary = "Create debit card for checking account (employee only)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Debit card created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DebitCardDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Debit cards can only be created for checking accounts"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found"
            )
    })
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<DebitCardDTO> createDebitCard(@PathVariable Long accountId) {
        return ResponseEntity.ok(debitCardService.createDebitCard(accountId));
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
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long accountId) {
        accountService.deleteAccount(accountId);
        return ResponseEntity.noContent().build();
    }
}
