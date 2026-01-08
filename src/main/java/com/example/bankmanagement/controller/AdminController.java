package com.example.bankmanagement.controller;

import com.example.bankmanagement.model.Account;
import com.example.bankmanagement.model.User;
import com.example.bankmanagement.service.AccountService;
import com.example.bankmanagement.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final AccountService accountService;

    @Autowired
    public AdminController(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

//    // Admin Dashboard
//    @GetMapping("/dashboard")
//    public String adminDashboard(Model model) {
//        model.addAttribute("userCount", userService.getAllUsers().size());
//        model.addAttribute("accountCount", accountService.getAllAccounts().size());
//        return "admin/dashboard";
//    }

    // View all users
    @GetMapping("/users")
    public String viewAllUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/user_list";
    }

    // View single user by ID
    @GetMapping("/user/{id}")
    public String viewUserById(@PathVariable Long id, Model model) {
        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            List<Account> accounts = userService.getAccountsByUserId(id);
            model.addAttribute("accounts", accounts);
            return "admin/user_view";
        } else {
            model.addAttribute("error", "User not found");
            return "admin/error";
        }
    }

    // Create new user form
    @GetMapping("/user/create")
    public String showCreateUserForm(Model model) {
        model.addAttribute("user", new User());
        return "admin/user_form";
    }

    // Handle creating a new user
    @PostMapping("/user/create")
    public String createUser(@ModelAttribute User user, @RequestParam("roles") Set<String> roles, Model model) {
        userService.saveUser(user, roles);
        model.addAttribute("message", "User created successfully!");
        return "redirect:/admin/users";
    }

    // Edit user form
    @GetMapping("/user/edit/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            return "admin/user_form";
        } else {
            model.addAttribute("error", "User not found");
            return "admin/error";
        }
    }

    // Update user
    @PostMapping("/user/edit/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute User user, @RequestParam("roles") Set<String> roles, Model model) {
        userService.updateUserProfile(id, user);
        userService.saveUser(user, roles); // Reassign roles while updating user
        model.addAttribute("message", "User updated successfully!");
        return "redirect:/admin/users";
    }

    // Delete user
    @PostMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable Long id, Model model) {
        userService.deleteUser(id);
        model.addAttribute("message", "User deleted successfully!");
        return "redirect:/admin/users";
    }

    // Admin can view all accounts
    @GetMapping("/accounts")
    public String viewAllAccounts(Model model) {
        List<Account> accounts = accountService.getAllAccounts();
        model.addAttribute("accounts", accounts);
        return "admin/account_list";
    }

    // View single account by ID
    @GetMapping("/account/{id}")
    public String viewAccountById(@PathVariable Long id, Model model) {
        Optional<Account> account = accountService.findById(id);
        if (account.isPresent()) {
            model.addAttribute("account", account.get());
            return "admin/account_view";
        } else {
            model.addAttribute("error", "Account not found");
            return "admin/error";
        }
    }
}
