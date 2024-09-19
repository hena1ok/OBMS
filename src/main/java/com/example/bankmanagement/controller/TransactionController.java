package com.example.bankmanagement.controller;

import com.example.bankmanagement.model.Transaction;
import com.example.bankmanagement.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // View all transactions (HTML page)
    @GetMapping
    public String getAllTransactions(Model model) {
        List<Transaction> transactions = transactionService.getAllTransactions();
        model.addAttribute("transactions", transactions);
        return "transaction_list";  // This refers to transaction_list.html
    }

    // Show transaction creation form
    @GetMapping("/new")
    public String showTransactionForm(Model model) {
        model.addAttribute("transaction", new Transaction());
        return "transaction_form";  // Refers to transaction_form.html
    }

    // Handle transaction form submission
    @PostMapping
    public String createTransaction(@ModelAttribute Transaction transaction) {
        transactionService.createTransaction(transaction);
        return "redirect:/transactions";
    }

    // Edit a transaction form
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Transaction transaction = transactionService.getTransactionById(id);
        model.addAttribute("transaction", transaction);
        return "transaction_form";
    }

    // Update transaction
    @PostMapping("/update/{id}")
    public String updateTransaction(@PathVariable Long id, @ModelAttribute Transaction transaction) {
        transactionService.updateTransaction(id, transaction);
        return "redirect:/transactions";
    }

    // Delete a transaction
    @GetMapping("/delete/{id}")
    public String deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return "redirect:/transactions";
    }
}
