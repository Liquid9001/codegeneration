package nl.codegeneratie.els.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import nl.codegeneratie.els.dtos.AtmSessionDTO;
import nl.codegeneratie.els.service.AtmSessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/atm-sessions")
@Tag(name = "ATM Sessions", description = "ATM session management endpoints")
public class AtmSessionController {

    private final AtmSessionService atmSessionService;

    public AtmSessionController(AtmSessionService atmSessionService) {
        this.atmSessionService = atmSessionService;
    }

    @GetMapping
    @Operation(
            summary = "Get all ATM sessions",
            description = "Retrieve a list of all ATM login sessions in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "ATM sessions retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AtmSessionDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public List<AtmSessionDTO> getAllAtmSessions() {
        return atmSessionService.getAllAtmSessions();
    }

    @PostMapping
    @Operation(
            summary = "Create a new ATM session",
            description = "Create a new ATM login session for a customer"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "ATM session created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AtmSessionDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid ATM session data"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public ResponseEntity<AtmSessionDTO> createAtmSession(@RequestBody AtmSessionDTO atmSessionDTO) {
        AtmSessionDTO createdSession = atmSessionService.createAtmSession(atmSessionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSession);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update an ATM session",
            description = "Update an existing ATM session by ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "ATM session updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AtmSessionDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "ATM session not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public ResponseEntity<AtmSessionDTO> updateAtmSession(@PathVariable Long id, @RequestBody AtmSessionDTO atmSessionDTO) {
        AtmSessionDTO updatedSession = atmSessionService.updateAtmSession(id, atmSessionDTO);
        return ResponseEntity.ok(updatedSession);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete an ATM session",
            description = "Delete an ATM session from the system by ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "ATM session deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "ATM session not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public ResponseEntity<Void> deleteAtmSession(@PathVariable Long id) {
        atmSessionService.deleteAtmSession(id);
        return ResponseEntity.noContent().build();
    }
}

