package nl.codegeneratie.els.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Token response")
public class TokenResponseDTO {
    @Schema(description = "Generated bearer token", example = "eyf5315jio3980f90950i129k")
    private String token;
}

