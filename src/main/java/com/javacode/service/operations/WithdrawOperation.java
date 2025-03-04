package com.javacode.service.operations;

import com.javacode.entity.Wallet;
import com.javacode.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class WithdrawOperation implements WalletOperation {

    private final WalletRepository walletRepository;

    public WithdrawOperation(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Override
    public boolean supports(String operationType) {
        return "WITHDRAW".equalsIgnoreCase(operationType);
    }

    @Override
    @Transactional
    public Wallet execute(UUID walletId, long amount) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));

        if (wallet.getBalance() < amount) {
            throw new IllegalStateException("Insufficient funds");
        }

        wallet.setBalance(wallet.getBalance() - amount);
        return walletRepository.save(wallet);
    }
}
