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
    @Schema(description = "Generated bearer token", example = "bearer_123e4567-e89b-12d3-a456-426614174000")
    private String token;
}

