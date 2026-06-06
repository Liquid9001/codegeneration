
package nl.codegeneratie.els.domain.Employee;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmployeeDashboardActionItems {
    private int pendingApprovals;
    private int overLimitToday;
    private int nearAbsoluteLimit;
}