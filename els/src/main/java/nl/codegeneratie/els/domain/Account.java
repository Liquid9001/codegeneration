package nl.codegeneratie.els.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import nl.codegeneratie.els.domain.enums.AccountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User user;

    @Column(unique = true)
    private String iban;

    @Column(name = "account_type")
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    private BigDecimal balance;

    @Column(name = "absolute_transfer_limit")
    private BigDecimal absoluteTransferLimit;

    @Column(name = "daily_transfer_limit")
    private BigDecimal dailyTransferLimit;
    private boolean active;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

