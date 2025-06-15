package com.barbershop.barbershop.controller;

import com.barbershop.barbershop.model.Transaction;
import com.barbershop.barbershop.model.User;
import com.barbershop.barbershop.repository.TransactionRepository;
import com.barbershop.barbershop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

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
        List<User> users = userRepository.findAllCustomers();
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

    @GetMapping("/customers/{id}/edit")
    public String editCustomer(@PathVariable("id") Long id, Model model) {
        Optional<User> customerOpt = userRepository.findById(id);
        
        if (customerOpt.isEmpty()) {
            throw new RuntimeException("Customer not found with id: " + id);
        }
        
        model.addAttribute("customer", customerOpt.get());
        return "admin/edit-customer";
    }

    @PostMapping("/customers/{id}/update")
    public String updateCustomer(@PathVariable("id") Long id, @ModelAttribute User customer, Model model) {
        Optional<User> existingUserOpt = userRepository.findById(id);
        
        if (existingUserOpt.isEmpty()) {
            throw new RuntimeException("Customer not found with id: " + id);
        }
        
        User existingUser = existingUserOpt.get();
        
        // Update user properties
        existingUser.setUsername(customer.getUsername());
        existingUser.setEmail(customer.getEmail());
        existingUser.setPhone(customer.getPhone());
        existingUser.setRole(customer.getRole());
        
        userRepository.save(existingUser);
        
        model.addAttribute("successMessage", "Customer updated successfully");
        model.addAttribute("customer", existingUser);
        return "admin/edit-customer";
    }

    @GetMapping("/customers/add")
    public String showAddCustomerForm(Model model) {
        model.addAttribute("customer", new User());
        return "admin/add-customer";
    }

    @PostMapping("/customers/add")
    public String addCustomer(@ModelAttribute User customer, Model model) {
        // Tambahkan logging
        Optional<User> existingUserOpt = userRepository.findByUsername(customer.getUsername());
        System.out.println("Checking username: " + customer.getUsername());
        System.out.println("Result: " + (existingUserOpt.isPresent() ? "exists" : "not exists"));
        
        if (existingUserOpt.isPresent()) {
            model.addAttribute("errorMessage", "Username already exists");
            model.addAttribute("customer", customer);
            return "admin/add-customer";
        }
        
        Optional<User> existingEmailOpt = userRepository.findByEmail(customer.getEmail());
        if (existingEmailOpt.isPresent()) {
            model.addAttribute("errorMessage", "Email already exists");
            model.addAttribute("customer", customer);
            return "admin/add-customer";
        }
        
        // Set default password
        customer.setPassword(new BCryptPasswordEncoder().encode("defaultPassword"));
        
        userRepository.save(customer);
        
        model.addAttribute("successMessage", "Customer added successfully");
        model.addAttribute("customer", new User());
        return "admin/add-customer";
    }

    @PostMapping("/customers/{id}/delete")
    public String deleteCustomer(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = userRepository.findById(id);
        
        if (userOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Customer not found");
            return "redirect:/admin/customers";
        }
        
        User user = userOpt.get();
        
        // Prevent deleting the logged-in admin (optional safety check)
        // Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // if (auth != null && auth.getName().equals(user.getUsername())) {
        //     redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete your own account");
        //     return "redirect:/admin/customers";
        // }
        
        try {
            userRepository.delete(user);
            redirectAttributes.addFlashAttribute("successMessage", "Customer successfully deleted");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting customer: " + e.getMessage());
        }
        
        return "redirect:/admin/customers";
    }
}