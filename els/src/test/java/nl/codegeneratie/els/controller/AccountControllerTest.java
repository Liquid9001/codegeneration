package nl.codegeneratie.els.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.codegeneratie.els.dtos.AccountDTO;
import nl.codegeneratie.els.dtos.AccountTransferLimitsDTO;
import nl.codegeneratie.els.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AccountControllerTest {

    private MockMvc mockMvc;
    private AccountService accountService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        accountService = mock(AccountService.class);
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(new AccountController(accountService)).build();
    }

    @Test
    void getAccountById() throws Exception {
        AccountDTO dto = new AccountDTO();
        dto.setIban("NL00TEST123");
        when(accountService.getAccountById(1L)).thenReturn(dto);
        mockMvc.perform(get("/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.iban").value("NL00TEST123"));
    }

    @Test
    void updateLimits() throws Exception {
        AccountTransferLimitsDTO req = new AccountTransferLimitsDTO();
        req.setDailyTransferLimit(new BigDecimal("1000"));
        req.setAbsoluteTransferLimit(new BigDecimal("5000"));
        AccountDTO res = new AccountDTO();
        res.setIban("NL00TEST123");
        when(accountService.updateAccountTransferLimits(eq(1L), any())).thenReturn(res);
        mockMvc.perform(patch("/accounts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.iban").value("NL00TEST123"));
    }

    @Test
    void deleteAccount() throws Exception {
        mockMvc.perform(delete("/accounts/1")).andExpect(status().isNoContent());
    }
}