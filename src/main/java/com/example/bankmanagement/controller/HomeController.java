package com.example.bankmanagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index"; // Ensure there is a template named "index.html" in the src/main/resources/templates directory
    }
}
