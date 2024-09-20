package com.example.bankmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.bankmanagement.model.User;
import com.example.bankmanagement.service.UserService;

import java.util.Optional;

@Controller
@RequestMapping("/")
public class MainController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder; // Add PasswordEncoder

    @Autowired
    public MainController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder; // Initialize PasswordEncoder
    }

    // Redirects to the login page
    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/login"; // Redirects to the login page
    }

    // Displays the login page
    @GetMapping("login")
    public String showLoginPage(Model model) {
        return "login"; // Renders login.html
    }

    @PostMapping("login")
    public String login(@RequestParam String email, @RequestParam String password, Model model) {
        Optional<User> userOptional = userService.findByEmail(email); // Fetch user from the database

        // Check if user exists and password matches
        if (userOptional.isPresent() && passwordEncoder.matches(password, userOptional.get().getPassword())) {
            return "redirect:/home"; // Redirect to home page on successful login
        } else {
            model.addAttribute("error", "Invalid email or password");
            return "login"; // Return to login page with error
        }
    }


    // Displays the signup page
    @GetMapping("signup")
    public String showSignupPage(Model model) {
        model.addAttribute("user", new User()); // Create an empty User object
        return "signup"; // Renders signup.html
    }

    // Handles signup form submission
    @PostMapping("signup")
    public String registerUser(@ModelAttribute User user, BindingResult result) {
        if (result.hasErrors()) {
            return "signup"; // Return to signup page with errors
        }
        userService.saveUser(user); // Save the user
        return "redirect:/login"; // Redirect to login page after successful signup
    }

    // Displays the homepage after login
    @GetMapping("home")
    public String showHomePage() {
        return "home"; // Renders home.html
    }
}
