package nl.codegeneratie.els.repository;

import nl.codegeneratie.els.domain.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT t FROM Transaction t WHERE " +
            "(:startDate IS NULL OR t.timestamp >= :startDate) AND " +
            "(:endDate IS NULL OR t.timestamp <= :endDate) AND " +
            "(:minAmount IS NULL OR t.amount >= :minAmount) AND " +
            "(:maxAmount IS NULL OR t.amount <= :maxAmount) AND " +
            "(:iban IS NULL OR t.senderIban = :iban OR t.receiverIban = :iban)")
    Page<Transaction> findFilteredTransactions(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount,
            @Param("iban") String iban,
            Pageable pageable
    );

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.senderAccountId = :accountId " +
            "AND t.timestamp >= :startOfDay " +
            "AND t.timestamp <= :endOfDay")
    BigDecimal getTotalTransferredToday(
            @Param("accountId") Long accountId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );
}

