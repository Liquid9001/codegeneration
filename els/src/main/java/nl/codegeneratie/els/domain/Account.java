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

    private Long customer_id;

    @Column(unique = true)
    private String iban;

    private String account_type;
    private BigDecimal balance;
    private BigDecimal absolute_transfer_limit;
    private BigDecimal daily_transfer_limit;
    private boolean active;
    private LocalDateTime created_at;
}

