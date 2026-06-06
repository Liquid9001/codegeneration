package nl.codegeneratie.els.controller;

import nl.codegeneratie.els.domain.Account;
import nl.codegeneratie.els.domain.Employee.EmployeeDashboardSummary;
import nl.codegeneratie.els.domain.User;
import nl.codegeneratie.els.domain.enums.UserAccountStatus;
import nl.codegeneratie.els.dtos.BankAccount.BankAccountFilterRequest;
import nl.codegeneratie.els.dtos.BankAccount.BankAccountResponse;
import nl.codegeneratie.els.service.AccountService;
import nl.codegeneratie.els.service.EmployeeService;
import nl.codegeneratie.els.service.TransactionService;
import nl.codegeneratie.els.service.UserService;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final UserService userService;
    private final AccountService accountService;
    private final TransactionService transactionService;

    public EmployeeController(EmployeeService employeeService, UserService userService, AccountService accountService, TransactionService transactionService) {
        this.employeeService = employeeService;
        this.userService = userService;
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    @GetMapping("/customers")
    public List<User> getCustomersByStatus(@RequestParam(name = "status") String status) throws BadRequestException {
        UserAccountStatus userStatus;
        try {
            userStatus = UserAccountStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status value");
        }
        return employeeService.getCustomersByStatus(userStatus);
    }

    @PatchMapping("/{id}/daily-limit")
    public void setDailyLimit(@PathVariable Long id, @RequestParam int dailyLimit) {
        employeeService.SetDailyLimit(id, dailyLimit);
    }

    @PatchMapping("/customers/{id}/approve")
    public ResponseEntity<String> approveCustomer(@PathVariable Long id) {
        employeeService.approveCustomer(id);
        return ResponseEntity.ok("Klant goedgekeurd en rekeningen aangemaakt.");
    }

    @GetMapping("/dashboard/summary")
    public ResponseEntity<EmployeeDashboardSummary> getDashboardSummary() {
        // Populate the DTO with the retrieved data
        EmployeeDashboardSummary summary = EmployeeDashboardSummary.builder()
                .totalCustomers(employeeService.getTotalCustomers())
                .activeCustomers(employeeService.getCustomersCountByStatus(UserAccountStatus.ACTIVE))
                .pendingApprovalsCustomers(employeeService.getCustomersCountByStatus(UserAccountStatus.PENDING))
                .newCustomersToday(employeeService.getNewCustomersToday())
                .totalBalance(employeeService.getTotalBalance())
                .activeAccounts(employeeService.getActiveAccountsCount())
                .inactiveAccounts(employeeService.getInactiveAccountsCount())
                .newAccountsToday(employeeService.getNewAccountsTodayCount())
                .accountsBelowZero(employeeService.getAccountsBelowZeroCount())
                .totalBelowZero(employeeService.getTotalBelowZero())
                .build();

        // Return the DTO as the response
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<BankAccountResponse>> getDashboardSummary(@ModelAttribute BankAccountFilterRequest filters) {
        List<Account> accounts = accountService.getAllAccountsByFilter(filters);

        List<BankAccountResponse> response = accounts.stream()
                .map(BankAccountResponse::toDto)
                .toList();

        return ResponseEntity.ok(response);
    }
}