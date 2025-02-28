package com.javacode.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javacode.entity.Wallet;
import com.javacode.model.WalletOperationRequest;
import com.javacode.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
public class WalletControllerTest {

    @Mock
    private WalletService walletService;

    @InjectMocks
    private WalletController walletController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(walletController)
                .setControllerAdvice(new com.javacode.exception.GlobalExceptionHandler()) // Добавляем глобальный обработчик
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void createOrUpdateWalletOk() throws Exception {
        Wallet wallet = new Wallet();
        wallet.setWalletId(UUID.randomUUID());
        wallet.setBalance(1000L);

        WalletOperationRequest request = new WalletOperationRequest();
        request.setWalletId(wallet.getWalletId());
        request.setOperationType("DEPOSIT");
        request.setAmount(500L);

        when(walletService.deposit(any(UUID.class), any(Long.class))).thenReturn(wallet);

        mockMvc.perform(post("/api/v1/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.walletId").value(wallet.getWalletId().toString()))
                .andExpect(jsonPath("$.balance").value(wallet.getBalance()));
    }

    @Test
    void createOrUpdateWalletInvalidJson() throws Exception {
        String invalidJson = "{ \"walletId\": \"not-a-uuid\", \"operationType\": \"DEPOSIT\", \"amount\": 500 }";

        mockMvc.perform(post("/api/v1/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid JSON format"));
    }

    @Test
    void createOrUpdateWalletBadOperationType() throws Exception {
        WalletOperationRequest request = new WalletOperationRequest();
        request.setWalletId(UUID.randomUUID());
        request.setOperationType("INVALID");
        request.setAmount(500L);

        mockMvc.perform(post("/api/v1/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid operation type"));
    }

    @Test
    void createOrUpdateWalletConflict() throws Exception {
        WalletOperationRequest request = new WalletOperationRequest();
        request.setWalletId(UUID.randomUUID());
        request.setOperationType("WITHDRAW");
        request.setAmount(500L);

        when(walletService.withdraw(any(UUID.class), any(Long.class)))
                .thenThrow(new OptimisticLockingFailureException("Concurrent modification detected"));

        mockMvc.perform(post("/api/v1/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Concurrent modification detected. Please try again."));
    }

    @Test
    void getWalletBalance() throws Exception {
        UUID walletId = UUID.randomUUID();
        Wallet wallet = new Wallet();
        wallet.setWalletId(walletId);
        wallet.setBalance(1000L);

        when(walletService.getWalletBalance(walletId)).thenReturn(wallet);

        mockMvc.perform(get("/api/v1/wallets/{walletId}", walletId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.walletId").value(walletId.toString()))
                .andExpect(jsonPath("$.balance").value(wallet.getBalance()));
    }

    @Test
    void getWalletBalanceNotFound() throws Exception {
        UUID walletId = UUID.randomUUID();
        when(walletService.getWalletBalance(walletId)).thenThrow(new IllegalArgumentException("Wallet not found"));

        mockMvc.perform(get("/api/v1/wallets/{walletId}", walletId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Wallet not found"));
    }
}
