package nl.codegeneratie.els.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "User with related accounts")
public class UserWithAccountsDTO extends UserDTO {
	private List<AccountDTO> accounts;
}

