package com.javacode.service.operations;

import com.javacode.entity.Wallet;
import java.util.UUID;

public interface WalletOperation {
    boolean supports(String operationType);
    Wallet execute(UUID walletId, long amount);
}
