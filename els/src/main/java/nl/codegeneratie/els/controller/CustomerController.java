package nl.codegeneratie.els.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import nl.codegeneratie.els.dtos.CustomerSearchDTO;
import nl.codegeneratie.els.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/customers")
@Tag(name = "Customers", description = "Customer search endpoints")
public class CustomerController {

    private final UserService userService;

    public CustomerController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/search")
    @Operation(
            summary = "Search customers by first and last name to find IBANs",
            security = {@SecurityRequirement(name = "bearerAuth"), @SecurityRequirement(name = "basicAuth")}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of customers with their IBANs",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomerSearchDTO.class)
                    )
            )
    })
    @PreAuthorize("isAuthenticated()")
    public List<CustomerSearchDTO> searchCustomers(
            @RequestParam(name = "firstName") String firstName,
            @RequestParam(name = "lastName") String lastName
    ) {
        return userService.searchCustomers(firstName, lastName);
    }
}

