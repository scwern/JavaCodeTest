package com.javacode.repository;

import com.javacode.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface WalletRepository extends JpaRepository<Wallet, UUID> {
}
