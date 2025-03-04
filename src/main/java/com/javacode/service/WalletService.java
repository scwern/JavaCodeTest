package com.javacode.service;

import com.javacode.entity.Wallet;
import com.javacode.model.WalletOperationRequest;
import com.javacode.repository.WalletRepository;
import com.javacode.service.operations.WalletOperation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class WalletService {

    private final List<WalletOperation> walletOperations;
    private final WalletRepository walletRepository;

    public WalletService(List<WalletOperation> walletOperations, WalletRepository walletRepository) {
        this.walletOperations = walletOperations;
        this.walletRepository = walletRepository;
    }
    public Wallet getWalletBalance(UUID walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));
    }

    public Wallet processOperation(WalletOperationRequest request) {
        return walletOperations.stream()
                .filter(op -> op.supports(request.getOperationType()))
                .findFirst()
                .map(op -> op.execute(request.getWalletId(), request.getAmount()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid operation type"));
    }
}
