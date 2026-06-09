package nl.codegeneratie.els.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import nl.codegeneratie.els.dtos.*;
import nl.codegeneratie.els.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "User registration, login, approval, and user details")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(
            summary = "Get all users with their accounts",
            description = "Retrieve all users including their accounts (secured)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of users with accounts",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserWithAccountsDTO.class)
                    )
            )
    })
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public List<UserWithAccountsDTO> getAllUsers(
            @RequestParam(required = false) Integer offset,
            @RequestParam(required = false) Integer limit
    ) {
        return userService.getAllUsers(offset, limit);
    }

    @PostMapping
    @Operation(
            summary = "Register a new customer",
            description = "Customer signup with essential information; password hashing handled by backend"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Customer registered successfully, pending approval",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input or email already used"
            )
    })
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        UserDTO createdUser = userService.createUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PostMapping("/login")
    @Operation(
            summary = "User login",
            description = "User login to obtain JWT token; single login endpoint for all users"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful login with JWT token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TokenResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid credentials"
            )
    })
    public ResponseEntity<TokenResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(userService.login(request.getEmail(), request.getPassword()));
    }

    @GetMapping("/{userId}")
    @Operation(
            summary = "Get user details with accounts",
            description = "Retrieve user information including accounts (secured)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User details retrieved",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserWithAccountsDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    public ResponseEntity<UserWithAccountsDTO> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PostMapping("/{userId}/approve")
    @Operation(
            summary = "Approve a user (employee only)",
            description = "Approve a registered user and automatically generate bank accounts",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User approved successfully and accounts created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserWithAccountsDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<UserWithAccountsDTO> approveUser(@PathVariable Long userId, @Valid @RequestBody UserTransferLimitsDTO userTransferLimitsDTO) {
        return ResponseEntity.ok(userService.approveUser(userId, userTransferLimitsDTO));
    }

    @GetMapping("/paginated")
    @Operation(
            summary = "Get all users with their accounts (paginated)",
            description = "Retrieve all users including their accounts (secured)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of users with accounts",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserWithAccountsDTO.class)
                    )
            )
    })
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<Page<UserWithAccountsDTO>> getAllUsersPaginated(
            @RequestParam(required = false) Boolean approved,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsersPaginated(approved, pageable));
    }
}

