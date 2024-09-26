package com.example.bankmanagement.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.bankmanagement.model.User;
import com.example.bankmanagement.service.UserService;

@Controller("/")
@RequestMapping
public class MainController {

    private final UserService userService;

    @Autowired
    public MainController(UserService userService) {
        this.userService = userService;
    }

    // Displays the login page
    @GetMapping("login")
    public String showLoginPage(Model model) {
        return "login"; // Renders login.html
    }

    // Displays the signup page
    @GetMapping("signup")
    public String showSignupPage(Model model) {
        model.addAttribute("user", new User()); // Create an empty User object
        return "signup"; // Renders signup.html
    }

    // Handles signup form submission
    @PostMapping("/signup")
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
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
        userService.saveUser(user);

        return "redirect:/login"; // Redirect to login page after successful signup
    }

    // Displays the homepage after login
    @GetMapping("/")
    public String showHomePage() {
        return "home"; // Renders home.html
    }
}
