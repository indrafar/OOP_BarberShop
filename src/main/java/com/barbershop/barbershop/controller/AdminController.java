package com.barbershop.barbershop.controller;

import com.barbershop.barbershop.model.Transaction;
import com.barbershop.barbershop.model.User;
import com.barbershop.barbershop.repository.TransactionRepository;
import com.barbershop.barbershop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @GetMapping
    public String adminDashboard(Model model) {
        List<User> users = userRepository.findAll();
        List<Transaction> transactions = transactionRepository.findAllByOrderByCreatedAtDesc();
        
        model.addAttribute("users", users);
        model.addAttribute("transactions", transactions);
        return "admin/dashboard";
    }

    @GetMapping("/transactions")
    public String viewTransactions(Model model) {
        List<Transaction> transactions = transactionRepository.findAllByOrderByCreatedAtDesc();
        model.addAttribute("transactions", transactions);
        return "admin/transactions";
    }

    @GetMapping("/customers")
    public String viewCustomers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "admin/customers";
    }

    @PostMapping("/transactions/{id}/update-status")
    @ResponseBody
    public String updateTransactionStatus(@PathVariable Long id, @RequestParam String status) {
        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Transaction not found"));
        transaction.setStatus(status);
        transactionRepository.save(transaction);
        return "Status updated successfully";
    }
} 