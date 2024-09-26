package com.example.bankmanagement.controller;

import com.example.bankmanagement.model.Account;
import com.example.bankmanagement.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // View all accounts (HTML page)
    @GetMapping("/account_list")
    public String getAllAccounts(Model model) {
        List<Account> accounts = accountService.getAllAccounts();
        model.addAttribute("accounts", accounts);
        return "account/account_list";  // This refers to account_list.html
    }

    // Show account creation form
    @GetMapping("/account_form")
    public String showAccountForm(Model model) {
        model.addAttribute("account", new Account());
        return "account/account_form";  // Refers to account_form.html
    }

    // Handle account form submission
    @PostMapping("/account_form")  // Make sure this matches your form action URL
    public String createAccount(@ModelAttribute Account account) {
        accountService.createAccount(account);
        return "redirect:/account/account_list";  // Redirect to the accounts list after creation
    }

    // Edit an account form
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Account account = accountService.getAccountById(id);
        model.addAttribute("account", account);
        return "account/account_form";  // Refers to account_form.html for editing
    }

    // Update account
    @PostMapping("/update/{id}")
    public String updateAccount(@PathVariable Long id, @ModelAttribute Account account) {
        accountService.updateAccount(id, account);
        return "redirect:/account/account_list";  // Redirect to the accounts list after update
    }

    // Delete an account
    @GetMapping("/delete/{id}")
    public String deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return "redirect:/account/account_list";  // Redirect to the accounts list after deletion
    }
}
