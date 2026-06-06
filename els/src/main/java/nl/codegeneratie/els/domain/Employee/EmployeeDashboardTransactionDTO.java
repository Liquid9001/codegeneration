package nl.codegeneratie.els.domain.Employee;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmployeeDashboardTransactionDTO {
    private String time;
    private String from;
    private String to;
    private double amount;
    private String initiator;
}

