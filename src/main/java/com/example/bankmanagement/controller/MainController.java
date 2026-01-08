package com.example.bankmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.bankmanagement.model.Transaction;
import com.example.bankmanagement.model.User;
import com.example.bankmanagement.service.ATMService;
import com.example.bankmanagement.service.LoanService;
import com.example.bankmanagement.service.RestartService;
import com.example.bankmanagement.service.TransactionService;
import com.example.bankmanagement.service.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class MainController {
    private final UserService userService;
    private final TransactionService transactionService;
    private final LoanService loanService;
    private final ATMService atmService;

    // Autowire all the necessary services in the constructor
    @Autowired
    public MainController(UserService userService, TransactionService transactionService,
                          LoanService loanService, ATMService atmService) {
        this.userService = userService;
        this.transactionService = transactionService;
        this.loanService = loanService;
        this.atmService = atmService;
    }
    @Autowired
    private RestartService restartService;


    @GetMapping("/logout")
    public String showLogoutPage() {
    	 restartService.restart();
    	return "logout";
    }

    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", true); // This will be used to display an error message in the login form
        }
        return "login"; // Renders login.html
    }

    @GetMapping("/")
    public String showHomePage(Authentication authentication) {
        if (authentication != null) {
            // Get roles of the authenticated user
            String roles = authentication.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .collect(Collectors.joining(", "));

            // Redirect based on user roles
            return redirectToDashboard(roles);
        }
        return "/login"; // Default page if no user is authenticated
    }

    private String redirectToDashboard(String roles) {
        if (roles.contains("ROLE_ADMIN")) {
            return "redirect:/admin/dashboard";
        } else if (roles.contains("ROLE_TELLER")) {
            return "redirect:/teller/dashboard";
        } else if (roles.contains("ROLE_SUPPORT")) {
            return "redirect:/support/dashboard";
        } else if (roles.contains("ROLE_USER")) {
            return "redirect:/customer/dashboard"; // Updated to match user role
        } else {
            return "redirect:/access-denied";
        }
    }

    @GetMapping("/signup")
    public String showSignupPage(Model model) {
        model.addAttribute("user", new User()); // Create an empty User object
        return "signup"; // Renders signup.html
    }

    // Handles signup form submission
    @PostMapping("/signup")
    public String registerUser(@ModelAttribute("user") User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "signup"; // Return to signup page with errors
        }

        if (userService.existsByUsername(user.getUsername())) {
            result.rejectValue("username", "error.user", "Username is already taken");
            return "signup";
        }

        if (userService.existsByEmail(user.getEmail())) {
            result.rejectValue("email", "error.user", "Email is already in use");
            return "signup";
        }

        // Save the user with the default role "ROLE_USER"
        userService.saveUser(user, null);

        return "redirect:/login"; // Redirect to login page after successful signup
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        List<User> recentUsers = userService.getRecentUsers(); // Fetch recent users
        List<Transaction> recentTransactions = transactionService.getRecentTransactions(); // Fetch recent transactions
     
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); 
      model.addAttribute("username", username);
       
      Optional<User> user = userService.getCurrentUser(); // Or whatever method you use
        model.addAttribute("user", user);
        model.addAttribute("recentUsers", recentUsers);
        model.addAttribute("recentTransactions", recentTransactions);
        model.addAttribute("totalUsers", userService.getTotalUsers()); // Example for total users
        model.addAttribute("totalLoans", loanService.getTotalLoans()); // Example for total loans
        model.addAttribute("totalTransactions", transactionService.getTotalTransactions()); // Example for total transactions
        model.addAttribute("totalATMs", atmService.getTotalATMs()); // Example for total ATMs

        return "admin/dashboard"; // Return admin dashboard view
    }

    @GetMapping("/teller/dashboard")
    public String tellerDashboard(Model model) {
    	  Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
          String username = authentication.getName(); 
        model.addAttribute("username", username);
         
        return "teller/dashboard"; // Return teller dashboard view
    }

    @GetMapping("/support/dashboard")
    public String supportDashboard(Model model) {
    	  Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
          String username = authentication.getName(); 
        model.addAttribute("username", username);
         
        return "support/dashboard"; // Return support dashboard view
    }

    @GetMapping("/customer/dashboard")
    public String dashboard(Model model) {
    	  Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
          String username = authentication.getName(); 
        model.addAttribute("username", username);
         
        Optional<User> user = userService.getCurrentUser(); // Or whatever method you use
        model.addAttribute("user", user);
        return "customer/dashboard";
    }

    // Optionally, handle access denied
    @GetMapping("/access-denied")
    public String accessDenied(Model model) {
        model.addAttribute("errorMessage", "You do not have permission to access this page.");
        return "access-denied"; // Return access denied view
    }
}
