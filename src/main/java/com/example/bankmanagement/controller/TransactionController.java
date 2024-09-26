package com.example.bankmanagement.controller;

import com.example.bankmanagement.model.Transaction;
import com.example.bankmanagement.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
        return "transaction/transaction_list";  // Specify the path to transaction_list.html
    }

    // Show transaction creation form
    @GetMapping("/new")
    public String showTransactionForm(Model model) {
        model.addAttribute("transaction", new Transaction());
        return "transaction/transaction_form";  // Specify the path to transaction_form.html
    }

    // Handle transaction form submission
    @PostMapping
    public String createTransaction(@ModelAttribute @Valid Transaction transaction, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "transaction/transaction_form";  // Return to form if there are validation errors
        }
        transactionService.createTransaction(transaction);
        return "redirect:/transactions";  // Redirect to transactions list after successful creation
    }

    // Edit a transaction form
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Transaction transaction = transactionService.getTransactionById(id);
        model.addAttribute("transaction", transaction);
        return "transaction/transaction_form";  // Specify the path to transaction_form.html
    }

    // Update transaction
    @PostMapping("/update/{id}")
    public String updateTransaction(@PathVariable Long id, @ModelAttribute @Valid Transaction transaction, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "transaction/transaction_form";  // Return to form if there are validation errors
        }
        transactionService.updateTransaction(id, transaction);
        return "redirect:/transactions";  // Redirect to transactions list after successful update
    }

    // Delete a transaction
    @GetMapping("/delete/{id}")
    public String deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return "redirect:/transactions";  // Redirect to transactions list after deletion
    }
}
