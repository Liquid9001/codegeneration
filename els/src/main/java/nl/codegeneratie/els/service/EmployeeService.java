package nl.codegeneratie.els.service;

import nl.codegeneratie.els.domain.User;
import nl.codegeneratie.els.domain.enums.AccountType;
import nl.codegeneratie.els.domain.enums.UserAccountStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Service
public class EmployeeService {

    void SetDailyLimit(Long id, int dailyLimit);

    List<User> getCustomersByStatus(UserAccountStatus status);

    int getCustomersCountByStatus(UserAccountStatus status);

    void updateAbsoluteLimit(Long id, BigDecimal newLimit);

    void approveCustomer(Long userId);

    void createBankAccount(User user, AccountType type);

    int getTotalCustomers();

    int getNewCustomersToday();

    BigDecimal getTotalBalance();

    int getActiveAccountsCount();

    int getInactiveAccountsCount();

    int getNewAccountsTodayCount();

    int getAccountsBelowZeroCount();

    BigDecimal getTotalBelowZero();
}