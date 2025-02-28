package com.javacode.controller;

import com.javacode.entity.Wallet;
import com.javacode.model.WalletOperationRequest;
import com.javacode.service.WalletService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wallets")
public class WalletController {
    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping
    public ResponseEntity<?> createOrUpdateWallet(@RequestBody WalletOperationRequest request) {
        Wallet wallet;
        if ("DEPOSIT".equalsIgnoreCase(request.getOperationType())) {
            wallet = walletService.deposit(request.getWalletId(), request.getAmount());
        } else if ("WITHDRAW".equalsIgnoreCase(request.getOperationType())) {
            wallet = walletService.withdraw(request.getWalletId(), request.getAmount());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"Invalid operation type\"}");
        }
        return ResponseEntity.ok(wallet);
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<?> getWalletBalance(@PathVariable UUID walletId) {
        Wallet wallet = walletService.getWalletBalance(walletId);
        return ResponseEntity.ok(wallet);
    }
}

