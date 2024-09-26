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

    // View all accounts
    @GetMapping("/account_list")
    public String getAllAccounts(Model model) {
        List<Account> accounts = accountService.getAllAccounts();
        model.addAttribute("accounts", accounts);
        return "account/account_list"; // Assuming there is an HTML template for the account list
    }

    // Show account creation form
    @GetMapping("/account_form")
    public String showAccountForm(Model model) {
        model.addAttribute("account", new Account());
        return "account/account_form"; // Assuming there is an HTML template for the account form
    }

    // Handle account form submission
    @PostMapping("/account_form")
    public String createAccount(@ModelAttribute Account account) {
        accountService.createAccount(account);
        return "redirect:/account/account_list"; // Redirect to the account list after creation
    }

 // Show edit form
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Account account = accountService.getAccountById(id); // Fetch the account directly
        model.addAttribute("account", account);
        return "account/account_form"; // Return to the same form for editing
    }


    // Update account
    @PostMapping("/update/{id}")
    public String updateAccount(@PathVariable Long id, @ModelAttribute Account account) {
        accountService.updateAccount(id, account);
        return "redirect:/account/account_list"; // Redirect to the account list after updating
    }

    // Delete an account
    @GetMapping("/delete/{id}")
    public String deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return "redirect:/account/account_list"; // Redirect to the account list after deletion
    }
}
