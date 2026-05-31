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

    private Long senderAccountId;
    private Long receiverAccountId;
    private Long initiatedByUserId;
    private String senderIban;
    private String receiverIban;
    private BigDecimal amount;
    private String transactionType;
    private String currency;
    private LocalDateTime timestamp;
    private String status;
    private String description;
}

