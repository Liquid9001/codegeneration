package nl.codegeneratie.els.config;

import nl.codegeneratie.els.domain.*;
import nl.codegeneratie.els.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final AtmSessionRepository atmSessionRepository;

    public DataInitializer(UserRepository userRepository,
                          AccountRepository accountRepository,
                          TransactionRepository transactionRepository,
                          AtmSessionRepository atmSessionRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.atmSessionRepository = atmSessionRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Only initialize if database is empty
        if (userRepository.count() == 0) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            // Create Users
            User user1 = new User();
            user1.setEmail("john.doe@example.com");
            user1.setFirst_name("John");
            user1.setLast_name("Doe");
            user1.setPhone_number(612345678);
            user1.setBsn(123456789);
            user1.setRole(0);
            user1.setApproved(true);
            user1.setCreated_at(LocalDateTime.parse("2024-01-15 10:30:00", formatter));
            userRepository.save(user1);

            User user2 = new User();
            user2.setEmail("jane.smith@example.com");
            user2.setFirst_name("Jane");
            user2.setLast_name("Smith");
            user2.setPhone_number(621234567);
            user2.setBsn(987654321);
            user2.setRole(0);
            user2.setApproved(true);
            user2.setCreated_at(LocalDateTime.parse("2024-01-16 14:45:00", formatter));
            userRepository.save(user2);

            User user3 = new User();
            user3.setEmail("michael.johnson@example.com");
            user3.setFirst_name("Michael");
            user3.setLast_name("Johnson");
            user3.setPhone_number(698765432);
            user3.setBsn(555666777);
            user3.setRole(0);
            user3.setApproved(true);
            user3.setCreated_at(LocalDateTime.parse("2024-01-17 09:15:00", formatter));
            userRepository.save(user3);

            User user4 = new User();
            user4.setEmail("sara.williams@example.com");
            user4.setFirst_name("Sara");
            user4.setLast_name("Williams");
            user4.setPhone_number(643210987);
            user4.setBsn(111222333);
            user4.setRole(1);
            user4.setApproved(true);
            user4.setCreated_at(LocalDateTime.parse("2024-01-18 11:30:00", formatter));
            userRepository.save(user4);

            User user5 = new User();
            user5.setEmail("admin@example.com");
            user5.setFirst_name("Admin");
            user5.setLast_name("User");
            user5.setPhone_number(600000000);
            user5.setBsn(999999999);
            user5.setRole(2);
            user5.setApproved(true);
            user5.setCreated_at(LocalDateTime.parse("2024-01-01 08:00:00", formatter));
            userRepository.save(user5);

            // Create Accounts
            Account account1 = new Account();
            account1.setCustomer_id(1L);
            account1.setIban("NL91ABNA0417164300");
            account1.setAccount_type("CHECKING");
            account1.setBalance(new BigDecimal("5000.00"));
            account1.setAbsolute_transfer_limit(new BigDecimal("10000.00"));
            account1.setDaily_transfer_limit(new BigDecimal("20000.00"));
            account1.setActive(true);
            account1.setCreated_at(LocalDateTime.parse("2024-01-15 10:30:00", formatter));
            accountRepository.save(account1);

            Account account2 = new Account();
            account2.setCustomer_id(1L);
            account2.setIban("NL47ABNA0123456789");
            account2.setAccount_type("SAVINGS");
            account2.setBalance(new BigDecimal("15000.00"));
            account2.setAbsolute_transfer_limit(new BigDecimal("5000.00"));
            account2.setDaily_transfer_limit(new BigDecimal("10000.00"));
            account2.setActive(true);
            account2.setCreated_at(LocalDateTime.parse("2024-02-01 14:20:00", formatter));
            accountRepository.save(account2);

            Account account3 = new Account();
            account3.setCustomer_id(2L);
            account3.setIban("NL61ABNA0123456790");
            account3.setAccount_type("CHECKING");
            account3.setBalance(new BigDecimal("8500.50"));
            account3.setAbsolute_transfer_limit(new BigDecimal("10000.00"));
            account3.setDaily_transfer_limit(new BigDecimal("20000.00"));
            account3.setActive(true);
            account3.setCreated_at(LocalDateTime.parse("2024-01-16 14:45:00", formatter));
            accountRepository.save(account3);

            Account account4 = new Account();
            account4.setCustomer_id(3L);
            account4.setIban("NL71ABNA0123456791");
            account4.setAccount_type("CHECKING");
            account4.setBalance(new BigDecimal("3200.75"));
            account4.setAbsolute_transfer_limit(new BigDecimal("10000.00"));
            account4.setDaily_transfer_limit(new BigDecimal("20000.00"));
            account4.setActive(true);
            account4.setCreated_at(LocalDateTime.parse("2024-01-17 09:15:00", formatter));
            accountRepository.save(account4);

            Account account5 = new Account();
            account5.setCustomer_id(4L);
            account5.setIban("NL81ABNA0123456792");
            account5.setAccount_type("SAVINGS");
            account5.setBalance(new BigDecimal("50000.00"));
            account5.setAbsolute_transfer_limit(new BigDecimal("50000.00"));
            account5.setDaily_transfer_limit(new BigDecimal("100000.00"));
            account5.setActive(true);
            account5.setCreated_at(LocalDateTime.parse("2024-01-18 11:30:00", formatter));
            accountRepository.save(account5);

            // Create Transactions
            Transaction transaction1 = new Transaction();
            transaction1.setFrom_account_id(1L);
            transaction1.setTo_account_id(3L);
            transaction1.setInitiated_by_user_id(1L);
            transaction1.setAmount(new BigDecimal("250.00"));
            transaction1.setTransaction_type("TRANSFER");
            transaction1.setCurrency("EUR");
            transaction1.setTimestamp(LocalDateTime.parse("2024-01-20 10:15:00", formatter));
            transaction1.setStatus("COMPLETED");
            transaction1.setDescription("Payment for invoice #INV001");
            transactionRepository.save(transaction1);

            Transaction transaction2 = new Transaction();
            transaction2.setFrom_account_id(3L);
            transaction2.setTo_account_id(1L);
            transaction2.setInitiated_by_user_id(2L);
            transaction2.setAmount(new BigDecimal("150.50"));
            transaction2.setTransaction_type("TRANSFER");
            transaction2.setCurrency("EUR");
            transaction2.setTimestamp(LocalDateTime.parse("2024-01-21 12:30:00", formatter));
            transaction2.setStatus("COMPLETED");
            transaction2.setDescription("Reimbursement for expenses");
            transactionRepository.save(transaction2);

            Transaction transaction3 = new Transaction();
            transaction3.setFrom_account_id(1L);
            transaction3.setTo_account_id(4L);
            transaction3.setInitiated_by_user_id(1L);
            transaction3.setAmount(new BigDecimal("500.00"));
            transaction3.setTransaction_type("TRANSFER");
            transaction3.setCurrency("EUR");
            transaction3.setTimestamp(LocalDateTime.parse("2024-01-22 14:00:00", formatter));
            transaction3.setStatus("COMPLETED");
            transaction3.setDescription("Salary payment");
            transactionRepository.save(transaction3);

            Transaction transaction4 = new Transaction();
            transaction4.setFrom_account_id(2L);
            transaction4.setTo_account_id(5L);
            transaction4.setInitiated_by_user_id(1L);
            transaction4.setAmount(new BigDecimal("1000.00"));
            transaction4.setTransaction_type("TRANSFER");
            transaction4.setCurrency("EUR");
            transaction4.setTimestamp(LocalDateTime.parse("2024-01-23 09:45:00", formatter));
            transaction4.setStatus("PENDING");
            transaction4.setDescription("Large transfer pending approval");
            transactionRepository.save(transaction4);

            Transaction transaction5 = new Transaction();
            transaction5.setFrom_account_id(4L);
            transaction5.setTo_account_id(1L);
            transaction5.setInitiated_by_user_id(3L);
            transaction5.setAmount(new BigDecimal("75.25"));
            transaction5.setTransaction_type("TRANSFER");
            transaction5.setCurrency("EUR");
            transaction5.setTimestamp(LocalDateTime.parse("2024-01-24 11:20:00", formatter));
            transaction5.setStatus("COMPLETED");
            transaction5.setDescription("Payment for services rendered");
            transactionRepository.save(transaction5);

            // Create ATM Sessions
            AtmSession session1 = new AtmSession();
            session1.setCustomer_id(1L);
            session1.setLogin_time(LocalDateTime.parse("2024-01-25 08:00:00", formatter));
            session1.setLogout_time(LocalDateTime.parse("2024-01-25 08:15:00", formatter));
            session1.setActive(false);
            atmSessionRepository.save(session1);

            AtmSession session2 = new AtmSession();
            session2.setCustomer_id(2L);
            session2.setLogin_time(LocalDateTime.parse("2024-01-25 10:30:00", formatter));
            session2.setLogout_time(LocalDateTime.parse("2024-01-25 10:45:00", formatter));
            session2.setActive(false);
            atmSessionRepository.save(session2);

            AtmSession session3 = new AtmSession();
            session3.setCustomer_id(3L);
            session3.setLogin_time(LocalDateTime.parse("2024-01-25 14:00:00", formatter));
            session3.setLogout_time(null);
            session3.setActive(true);
            atmSessionRepository.save(session3);

            AtmSession session4 = new AtmSession();
            session4.setCustomer_id(4L);
            session4.setLogin_time(LocalDateTime.parse("2024-01-25 16:20:00", formatter));
            session4.setLogout_time(LocalDateTime.parse("2024-01-25 16:30:00", formatter));
            session4.setActive(false);
            atmSessionRepository.save(session4);

            AtmSession session5 = new AtmSession();
            session5.setCustomer_id(1L);
            session5.setLogin_time(LocalDateTime.parse("2024-01-26 09:00:00", formatter));
            session5.setLogout_time(null);
            session5.setActive(true);
            atmSessionRepository.save(session5);

            System.out.println("✓ Database initialized with sample data!");
            System.out.println("✓ 5 Users, 5 Accounts, 5 Transactions, 5 ATM Sessions created");
        }
    }
}

