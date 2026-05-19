package nl.codegeneratie.els.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User user;

    @Column(unique = true)
    private String iban;

    @Column(name = "account_type")
    private String accountType;
    private BigDecimal balance;

    @Column(name = "absolute_transfer_limit")
    private BigDecimal absoluteTransferLimit;

    @Column(name = "daily_transfer_limit")
    private BigDecimal dailyTransferLimit;
    private boolean active;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

