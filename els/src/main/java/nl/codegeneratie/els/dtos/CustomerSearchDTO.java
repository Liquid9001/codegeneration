package nl.codegeneratie.els.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Customer search result")
public class CustomerSearchDTO {
    private Long customerId;
    private String firstName;
    private String lastName;
    private String iban;
}

