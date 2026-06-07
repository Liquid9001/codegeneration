package nl.codegeneratie.els.mappers;

import nl.codegeneratie.els.domain.Account;
import nl.codegeneratie.els.dtos.AccountDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {
    public AccountDTO toAccountDTO(Account account) {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setFirstName(account.getUser().getFirstName());
        accountDTO.setLastName(account.getUser().getLastName());
        BeanUtils.copyProperties(account, accountDTO);
        return accountDTO;
    }
}
