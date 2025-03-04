package com.javacode.service.operations;

import com.javacode.entity.Wallet;
import com.javacode.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class DepositOperation implements WalletOperation {

    private final WalletRepository walletRepository;

    public DepositOperation(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Override
    public boolean supports(String operationType) {
        return "DEPOSIT".equalsIgnoreCase(operationType);
    }

    @Override
    @Transactional
    public Wallet execute(UUID walletId, long amount) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseGet(() -> walletRepository.save(new Wallet(walletId, 0)));

        wallet.setBalance(wallet.getBalance() + amount);
        return walletRepository.save(wallet);
    }
}
