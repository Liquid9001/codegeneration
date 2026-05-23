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
    private Long customer_id;
    private String first_name;
    private String last_name;
    private String iban;
}

