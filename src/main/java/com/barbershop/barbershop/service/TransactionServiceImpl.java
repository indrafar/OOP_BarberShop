package com.barbershop.barbershop.service;

import com.barbershop.barbershop.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionServiceImpl {

    @Autowired
    private TransactionRepository transactionRepository;

    public void createTransaction(Transaction tx) {
        transactionRepository.save(tx);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}
