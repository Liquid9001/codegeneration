package nl.codegeneratie.els.service;

import nl.codegeneratie.els.domain.Account;
import nl.codegeneratie.els.domain.User;
import nl.codegeneratie.els.domain.enums.UserRole;
import nl.codegeneratie.els.dtos.*;
import nl.codegeneratie.els.exceptions.ForbiddenException;
import nl.codegeneratie.els.exceptions.InvalidCredentialsException;
import nl.codegeneratie.els.mappers.AccountMapper;
import nl.codegeneratie.els.exceptions.UserNotFoundException;
import nl.codegeneratie.els.exceptions.UserRegistrationException;
import nl.codegeneratie.els.repository.AccountRepository;
import nl.codegeneratie.els.repository.UserRepository;
import nl.codegeneratie.els.security.JwtService;
import nl.codegeneratie.els.security.SecurityUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static final String EMAIL_PATTERN = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AccountMapper accountMapper;

    public UserService(
            UserRepository userRepository,
            AccountRepository accountRepository,
            AccountService accountService,
            JwtService jwtService,
            AccountMapper accountMapper,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.accountService = accountService;
        this.jwtService = jwtService;
        this.accountMapper = accountMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserWithAccountsDTO> getAllUsers(Integer offset, Integer limit) {
        List<User> users = userRepository.findAll();
        int safeOffset = offset == null ? 0 : Math.max(offset, 0);
        int safeLimit = limit == null ? users.size() : Math.max(limit, 1);
        int end = Math.min(safeOffset + safeLimit, users.size());
        if (safeOffset >= users.size()) {
            return List.of();
        }
        return users.subList(safeOffset, end)
                .stream()
                .map(this::convertToUserWithAccountsDTO)
                .collect(Collectors.toList());
    }

    public UserDTO createUser(UserDTO userDTO) {
        validateRegistration(userDTO);
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new UserRegistrationException("Email is already in use");
        }
        if (userRepository.existsByBsn(userDTO.getBsn())) {
            throw new UserRegistrationException("BSN is already in use");
        }

        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        user.setPasswordHash(passwordEncoder.encode(userDTO.getPassword()));
        user.setApproved(false);
        user.setRole(UserRole.CUSTOMER);
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }
        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    public UserWithAccountsDTO getUserById(Long userId) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        if (!SecurityUtil.isEmployeeOrAdmin() && !currentUserId.equals(userId)) {
            throw new ForbiddenException();
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        return convertToUserWithAccountsDTO(user);
    }

    public TokenResponseDTO login(String email, String password) {
        if (isBlank(email) || isBlank(password)) {
            throw new InvalidCredentialsException();
        }

        User user = userRepository.findByEmail(email).orElseThrow(InvalidCredentialsException::new);
        boolean valid = passwordEncoder.matches(password, user.getPasswordHash());

        if (!valid) {
            throw new InvalidCredentialsException();
        }

        String token = jwtService.generateToken(user);
        return new TokenResponseDTO(token);
    }

    public UserWithAccountsDTO approveUser(Long userId, UserTransferLimitsDTO userApprovalDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        user.setApproved(true);
        userRepository.save(user);
        accountService.createDefaultAccountsIfNeeded(user, userApprovalDTO.getCheckingAccount(), userApprovalDTO.getSavingsAccount());
        return convertToUserWithAccountsDTO(user);
    }

    public List<CustomerSearchDTO> searchCustomers(String firstName, String lastName) {
        List<User> users = userRepository.findByFirstAndLastName(firstName, lastName);
        List<CustomerSearchDTO> results = new ArrayList<>();
        for (User user : users) {
            List<Account> accounts = accountRepository.findByUser_Id(user.getId());
            for (Account account : accounts) {
                results.add(new CustomerSearchDTO(user.getId(), user.getFirstName(), user.getLastName(), account.getIban()));
            }
        }
        return results;
    }

    private UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        userDTO.setPassword(null);
        return userDTO;
    }

    private UserWithAccountsDTO convertToUserWithAccountsDTO(User user) {
        UserWithAccountsDTO dto = new UserWithAccountsDTO();
        BeanUtils.copyProperties(user, dto);
        dto.setPassword(null);
        List<AccountDTO> accounts = accountRepository.findByUser_IdAndActiveTrue(user.getId())
                .stream()
                .map(accountMapper::toAccountDTO)
                .collect(Collectors.toList());
        dto.setAccounts(accounts);
        return dto;
    }

    private void validateRegistration(UserDTO userDTO) {
        if (userDTO == null) {
            throw new UserRegistrationException("Registration request is required");
        }
        if (isBlank(userDTO.getEmail()) || !userDTO.getEmail().matches(EMAIL_PATTERN)) {
            throw new UserRegistrationException("Email must be valid");
        }
        if (isBlank(userDTO.getPassword())) {
            throw new UserRegistrationException("Password is required");
        }
        if (isBlank(userDTO.getFirstName())) {
            throw new UserRegistrationException("First name is required");
        }
        if (isBlank(userDTO.getLastName())) {
            throw new UserRegistrationException("Last name is required");
        }
        if (userDTO.getPhoneNumber() == null) {
            throw new UserRegistrationException("Phone number is required");
        }
        if (userDTO.getBsn() == null) {
            throw new UserRegistrationException("BSN is required");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
