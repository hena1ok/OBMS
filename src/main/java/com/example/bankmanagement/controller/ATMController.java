package com.example.bankmanagement.controller;

import com.example.bankmanagement.model.ATM;
import com.example.bankmanagement.service.ATMService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/atm")
public class ATMController {

    private final ATMService atmService;

    public ATMController(ATMService atmService) {
        this.atmService = atmService;
    }

    // Display list of all ATMs
    @GetMapping("/list")
    public String listATMs(Model model) {
        List<ATM> atms = atmService.getAllATMs();
        model.addAttribute("atms", atms);
        return "atm/atm_list"; // Path to ATM list HTML page
    }

    // Show form for creating or editing an ATM
    @GetMapping("/form")
    public String showATMForm(@RequestParam(required = false) Long id, Model model) {
        ATM atm;
        if (id != null) {
            atm = atmService.getATMById(id); // Get existing ATM for editing
        } else {
            atm = new ATM(); // Create new ATM if no ID is provided
        }
        model.addAttribute("atm", atm);
        return "atm/atm_form"; // Path to ATM form HTML page
    }

    // Handle the creation or update of an ATM
    @PostMapping("/createOrUpdate")
    public String createOrUpdateATM(@ModelAttribute @Valid ATM atm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "atm/atm_form"; // Return to form if there are validation errors
        }
        atmService.createOrUpdateATM(atm);
        return "redirect:/atm/list"; // Redirect to ATM list after creation/update
    }

    // Show ATM details
    @GetMapping("/{id}")
    public String showATMDetails(@PathVariable Long id, Model model) {
        ATM atm = atmService.getATMById(id);
        model.addAttribute("atm", atm);
        return "atm/atm_details"; // Path to ATM details HTML page
    }

    // Delete ATM
    @GetMapping("/delete/{id}")
    public String deleteATM(@PathVariable Long id) {
        atmService.deleteATM(id);
        return "redirect:/atm/list"; // Redirect to ATM list after deletion
    }
}
