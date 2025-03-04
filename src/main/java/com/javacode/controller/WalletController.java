package com.javacode.controller;

import com.javacode.entity.Wallet;
import com.javacode.model.WalletOperationRequest;
import com.javacode.service.WalletService;
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
    public ResponseEntity<Wallet> createOrUpdateWallet(@RequestBody WalletOperationRequest request) {
        Wallet wallet = walletService.processOperation(request);
        return ResponseEntity.ok(wallet);
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<Wallet> getWalletBalance(@PathVariable UUID walletId) {
        Wallet wallet = walletService.getWalletBalance(walletId);
        return ResponseEntity.ok(wallet);
    }
}
