package com.barbershop.barbershop.service;

import com.barbershop.barbershop.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}