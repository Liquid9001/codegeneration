package nl.codegeneratie.els.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long from_account_id;
    private Long to_account_id;
    private Long initiated_by_user_id;
    private BigDecimal amount;
    private String transaction_type;
    private String currency;
    private LocalDateTime timestamp;
    private String status;
    private String description;
}

