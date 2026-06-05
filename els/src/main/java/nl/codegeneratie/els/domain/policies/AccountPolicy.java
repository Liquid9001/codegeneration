package nl.codegeneratie.els.domain.policies;

import nl.codegeneratie.els.domain.Account;
import nl.codegeneratie.els.dtos.AccountTransferLimitsDTO;
import nl.codegeneratie.els.exceptions.AccountNotFoundException;
import nl.codegeneratie.els.exceptions.InvalidTransferLimitsException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AccountPolicy {

    private boolean areTransferLimitsValid(AccountTransferLimitsDTO limitsDTO) {
        BigDecimal dailyTransferLimit = limitsDTO.getDailyTransferLimit();
        BigDecimal absoluteTransferLimit = limitsDTO.getAbsoluteTransferLimit();
        // Daily limit has to be higher than absolute limit, and both need to be higher than zero
        return dailyTransferLimit.compareTo(absoluteTransferLimit) >= 0
                && dailyTransferLimit.compareTo(BigDecimal.ZERO) > 0
                && absoluteTransferLimit.compareTo(BigDecimal.ZERO) > 0;
    }

    public void enforceTransferLimitsMustBeValid(AccountTransferLimitsDTO dto) {
        if (!areTransferLimitsValid(dto)) {
            throw new InvalidTransferLimitsException(dto.getDailyTransferLimit(), dto.getAbsoluteTransferLimit());
        }
    }

    public void enforceAccountMustBeActive(Account account) {
        if (!account.isActive()) {
            throw new AccountNotFoundException(account.getId());
            // for security reasons, inactive (soft-deleted) accounts return "not found" instead of "inactive account" exception
        }
    }
}
