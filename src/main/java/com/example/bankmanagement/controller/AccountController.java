package com.example.bankmanagement.controller;

import com.example.bankmanagement.model.Account;
import com.example.bankmanagement.model.User;
import com.example.bankmanagement.security.CustomUserDetails;
import com.example.bankmanagement.service.AccountService;
import com.example.bankmanagement.service.UserService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Autowired
    private UserService userService;

    // View all accounts
    @GetMapping("/account_list")
    public String listAccounts(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            List<Account> accounts;
            if (userDetails.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"))) {
                accounts = accountService.findAll();
            } else {
                Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
                if (optionalUser.isEmpty()) {
                    throw new UsernameNotFoundException("User not found: " + userDetails.getUsername());
                }
                User currentUser = optionalUser.get();
                accounts = accountService.findByUser(currentUser);
                model.addAttribute("user", currentUser);
            }
            model.addAttribute("accounts", accounts);
            return "account/account_list";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error/generic_error"; // Create this error page in templates
        }
    }

    // Show account creation form
    @GetMapping("/create")
    public String showCreateAccountForm(Model model) {
        try {
            List<User> users = userService.getAllUsers();
            model.addAttribute("users", users);
            model.addAttribute("account", new Account());
            return "account/account_form";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load account creation form.");
            return "error/generic_error";
        }
    }

    @PostMapping("/create")
    public String createAccount(@ModelAttribute("account") @Valid Account account,
                                BindingResult result,
                                @AuthenticationPrincipal UserDetails userDetails,
                                Model model) {
        if (account.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            result.rejectValue("balance", "error.balance", "Balance must not be negative.");
        }

        if (result.hasErrors()) {
            List<User> users = userService.getAllUsers();
            model.addAttribute("users", users);
            return "account/account_form";
        }

        Long userId = getUserIdBasedOnRole(userDetails, account);
        accountService.createAccount(account, userId);

        return "redirect:/account/account_list";
    }
    private Long getUserIdBasedOnRole(UserDetails userDetails, Account account) {
        if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_TELLER"))) {
            return account.getUser().getId();
        } else {
            return getUserIdFromUserDetails(userDetails);
        }
    }



    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            Account account = accountService.getAccountById(id);
            model.addAttribute("account", account);
            return "account/account_form";
        } catch (Exception e) {
            model.addAttribute("error", "Account not found.");
            return "error/generic_error";
        }
    }

    @PostMapping("/update/{id}")
    public String updateAccount(@PathVariable Long id, @ModelAttribute Account account, RedirectAttributes redirectAttributes) {
        try {
            accountService.updateAccount(id, account);
            
            redirectAttributes.addFlashAttribute("successMessage", "Account updated successfully!");
            
            return "redirect:/account/account_list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update account.");
            return "redirect:/error/generic_error";
        }
    }


    @GetMapping("/delete/{id}")
    public String deleteAccount(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            accountService.deleteAccount(id);
            redirectAttributes.addFlashAttribute("successMessage", "Account deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete account.");
        }
        return "redirect:/account/account_list";
    }


    @GetMapping("/search")
    public String searchAccounts(@RequestParam String searchTerm, Model model) {
        try {
            List<Account> accounts = accountService.searchAccounts(searchTerm);
            model.addAttribute("accounts", accounts);
            return "account/account_list";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to perform search.");
            return "error/generic_error";
        }
    }

    // Helper method
    private Long getUserIdFromUserDetails(UserDetails userDetails) {
        if (userDetails instanceof CustomUserDetails) {
            return ((CustomUserDetails) userDetails).getId();
        } else {
            throw new IllegalArgumentException("Invalid user details implementation");
        }
    }
}
