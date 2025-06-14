package com.barbershop.barbershop.service;

import com.barbershop.barbershop.model.Transaction;
import java.util.List;

public interface TransactionService {
    Transaction saveTransaction(Transaction tx);
    List<Transaction> getAllTransactions();
} 