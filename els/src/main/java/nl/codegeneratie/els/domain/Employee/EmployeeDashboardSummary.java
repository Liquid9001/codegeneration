package nl.codegeneratie.els.domain.Employee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDashboardSummary {
    private int newCustomersToday;
    private int newAccountsToday;

    private int activeCustomers;
    private int pendingApprovalsCustomers;
    private int totalCustomers;

    private int activeAccounts;
    private int inactiveAccounts;
    private int totalAccounts;

    private int accountsBelowZero;
    private BigDecimal totalBelowZero;

    private BigDecimal totalBalance;
    private BigDecimal highestBalance;
    private double lowestBalance;

    private int totalTransactions;

    private int totalTransactionsToday;
    private BigDecimal totalTransactionVolumeToday;
    private double averageTransactionAmountToday;

    private int transactionsLast7Days;
    private BigDecimal totalTransactionVolumeLast7Days;

    private int peakTransactionHourToday;

    private long atmDepositsToday;
    private long atmWithdrawalsToday;
    private BigDecimal atmDepositsVolumeToday;
    private BigDecimal atmWithdrawalsVolumeToday;


    public double getAverageBalance() {
        if (totalAccounts == 0) return 0;
        return totalBalance.divide(BigDecimal.valueOf(totalAccounts), 2, RoundingMode.HALF_UP).doubleValue();
    }

    public BigDecimal getAverageTransactionAmountLast7Days() {
        if (transactionsLast7Days == 0) return BigDecimal.ZERO;
        return totalTransactionVolumeLast7Days
                .divide(BigDecimal.valueOf(transactionsLast7Days), 2, RoundingMode.HALF_UP);
    }

}