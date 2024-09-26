package com.example.bankmanagement.controller;

import com.example.bankmanagement.model.Account;
import com.example.bankmanagement.model.Transaction;
import com.example.bankmanagement.service.AccountService;
import com.example.bankmanagement.service.TransactionService;
import com.example.bankmanagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/transaction")
public class TransactionController {

    private final TransactionService transactionService;
    private final AccountService accountService;  // Inject AccountService
    private final UserService userService;        // Inject UserService

    public TransactionController(TransactionService transactionService, AccountService accountService, UserService userService) {
        this.transactionService = transactionService;
        this.accountService = accountService;     // Initialize AccountService
        this.userService = userService;           // Initialize UserService
    }

    // View all transactions (HTML page)
    @GetMapping("/transaction_list")
    public String getAllTransactions(Model model) {
        List<Transaction> transactions = transactionService.getAllTransactions();
        model.addAttribute("transactions", transactions);
        return "transaction/transaction_list";  // Specify the path to transaction_list.html
    }

    // Show transaction creation form
    @GetMapping("/transaction_form")
    public String showTransactionForm(Model model) {
        model.addAttribute("transaction", new Transaction());
        model.addAttribute("accounts", accountService.getAllAccounts());  // Fetch all accounts from the service
        model.addAttribute("users", userService.getAllUsers());          // Fetch all users from the service
        return "transaction/transaction_form";
    }

    // Handle transaction creation
    @PostMapping("/create")
    public String createTransaction(@ModelAttribute @Valid Transaction transaction, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return showTransactionFormWithErrors(transaction, bindingResult);  // Return to form if there are validation errors
        }

        transaction.setDate(LocalDate.now()); // Set the current date
        transactionService.createTransaction(transaction); // Create the transaction
        return "redirect:/transaction/transaction_list"; // Redirect to the transaction list
    }

    // Edit a transaction form
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Transaction transaction = transactionService.getTransactionById(id);
        model.addAttribute("transaction", transaction);
        model.addAttribute("accounts", accountService.getAllAccounts());  // Fetch accounts for editing
        model.addAttribute("users", userService.getAllUsers());          // Fetch users for editing
        return "transaction/transaction_form";  // Specify the path to transaction_form.html
    }

    // Update transaction
    @PostMapping("/update/{id}")
    public String updateTransaction(@PathVariable Long id, @ModelAttribute @Valid Transaction transaction, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return showTransactionFormWithErrors(transaction, bindingResult);  // Return to form if there are validation errors
        }

        transactionService.updateTransaction(id, transaction);
        return "redirect:/transaction/transaction_list";  // Redirect to transactions list after successful update
    }

    // Delete a transaction
    @GetMapping("/delete/{id}")
    public String deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return "redirect:/transaction/transaction_list";  // Redirect to transactions list after deletion
    }

    // Helper method to show the transaction form with errors
    private String showTransactionFormWithErrors(Transaction transaction, BindingResult bindingResult) {
        Model model = new ExtendedModelMap();  // Create a new Model for error handling
        model.addAttribute("transaction", transaction);
        model.addAttribute("accounts", accountService.getAllAccounts());  // Fetch all accounts from the service
        model.addAttribute("users", userService.getAllUsers());          // Fetch all users from the service
        return "transaction/transaction_form";  // Return to the transaction form
    }
}
