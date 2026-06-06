package nl.codegeneratie.els.controller;

import nl.codegeneratie.els.domain.enums.UserRole;
import nl.codegeneratie.els.dtos.TokenResponseDTO;
import nl.codegeneratie.els.dtos.UserDTO;
import nl.codegeneratie.els.exceptions.GlobalExceptionHandler;
import nl.codegeneratie.els.exceptions.InvalidCredentialsException;
import nl.codegeneratie.els.exceptions.UserRegistrationException;
import nl.codegeneratie.els.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest {

    private UserService userService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void createUserWithInvalidInputReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "",
                                  "password": "password123",
                                  "firstName": "New",
                                  "lastName": "Customer",
                                  "phoneNumber": 612345678,
                                  "bsn": 123456789
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void createUserWithDuplicateEmailReturnsBadRequest() throws Exception {
        when(userService.createUser(any(UserDTO.class)))
                .thenThrow(new UserRegistrationException("Email is already in use"));

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validRegistrationJson("CUSTOMER")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Email is already in use")));
    }

    @Test
    void createUserWithDuplicateBsnReturnsBadRequest() throws Exception {
        when(userService.createUser(any(UserDTO.class)))
                .thenThrow(new UserRegistrationException("BSN is already in use"));

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validRegistrationJson("CUSTOMER")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("BSN is already in use")));
    }

    @Test
    void createUserCannotCreatePrivilegedRoleThroughResponse() throws Exception {
        UserDTO response = new UserDTO();
        response.setId(10L);
        response.setEmail("new.customer@example.com");
        response.setFirstName("New");
        response.setLastName("Customer");
        response.setPhoneNumber(612345678);
        response.setBsn(123456789);
        response.setRole(UserRole.CUSTOMER);

        when(userService.createUser(any(UserDTO.class))).thenReturn(response);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRegistrationJson("ADMIN")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role", is("CUSTOMER")));
    }

    @Test
    void createUserCannotCreateEmployeeRoleThroughResponse() throws Exception {
        UserDTO response = new UserDTO();
        response.setId(10L);
        response.setEmail("new.customer@example.com");
        response.setFirstName("New");
        response.setLastName("Customer");
        response.setPhoneNumber(612345678);
        response.setBsn(123456789);
        response.setRole(UserRole.CUSTOMER);

        when(userService.createUser(any(UserDTO.class))).thenReturn(response);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRegistrationJson("EMPLOYEE")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role", is("CUSTOMER")));
    }

    @Test
    void loginWithInvalidCredentialsReturnsUnauthorized() throws Exception {
        when(userService.login(eq("customer@example.com"), eq("wrong")))
                .thenThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "customer@example.com",
                                  "password": "wrong"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", is("Invalid credentials")));
    }

    @Test
    void loginWithValidCredentialsReturnsToken() throws Exception {
        when(userService.login(eq("customer@example.com"), eq("password123")))
                .thenReturn(new TokenResponseDTO("header.payload.signature"));

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "customer@example.com",
                                  "password": "password123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("header.payload.signature")));
    }

    private String validRegistrationJson(String role) {
        return """
                {
                  "email": "new.customer@example.com",
                  "password": "password123",
                  "firstName": "New",
                  "lastName": "Customer",
                  "phoneNumber": 612345678,
                  "bsn": 123456789,
                  "role": "%s"
                }
                """.formatted(role);
    }
}
