package nl.codegeneratie.els.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "from_account_id")
    private Long fromAccountId;
    @Column(name = "to_account_id")
    private Long toAccountId;
    @Column(name = "initiated_by_user_id")
    private Long initiatedByUserId;
    private BigDecimal amount;
    @Column(name = "transaction_type")
    private String transactionType;
    private String currency;
    private LocalDateTime timestamp;
    private String status;
    private String description;
}

