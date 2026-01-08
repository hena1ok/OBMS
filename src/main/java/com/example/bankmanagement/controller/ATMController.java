package com.example.bankmanagement.controller;

import com.example.bankmanagement.model.ATM;
import com.example.bankmanagement.service.ATMService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/atm")
public class ATMController {

    private static final Logger logger = LoggerFactory.getLogger(ATMController.class);
    private final ATMService atmService;

    @Autowired
    public ATMController(ATMService atmService) {
        this.atmService = atmService;
    }

    @GetMapping("/list")
    public String listAtms(Model model) {
        List<ATM> atms = atmService.findAll();
        logger.info("Fetched {} ATMs", atms.size());
        model.addAttribute("atms", atms);
        return "atm/atm_list";
    }

    // Display form for adding a new ATM
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/form")
    public String showAddATMForm(Model model) {
        model.addAttribute("atm", new ATM());
        return "atm/atm_form";  // Return to the ATM form view
    }

    // Handle submission of new ATM
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/form")
    public String addATM(@ModelAttribute ATM atm, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            logger.error("Validation errors occurred while adding ATM: {}", result.getAllErrors());
            return "atm/atm_form"; // Return to the form with error messages
        }
        atmService.save(atm);
        redirectAttributes.addFlashAttribute("successMessage", "ATM added successfully!");
        logger.info("Added new ATM with ID: {}", atm.getId());
        return "redirect:/atm/list";  // Redirect to the ATM list after saving
    }

    // Display form for editing an existing ATM
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/form/{id}")
    public String showEditATMForm(@PathVariable Long id, Model model) {
        ATM atm = findATMById(id);
        if (atm == null) {
            return "error/404";  // Redirect to a not found page
        }
        model.addAttribute("atm", atm);
        return "atm/atm_form";  // Return to the ATM form view
    }

    // Handle submission of updated ATM
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/form/{id}")
    public String updateATM(@PathVariable Long id, @ModelAttribute ATM atm, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            logger.error("Validation errors occurred while updating ATM with ID {}: {}", id, result.getAllErrors());
            atm.setId(id); // Ensure the ID is set for the form submission
            return "atm/atm_form"; // Return to the form with error messages
        }
        atm.setId(id);
        atmService.save(atm);
        redirectAttributes.addFlashAttribute("successMessage", "ATM updated successfully!");
        logger.info("Updated ATM with ID: {}", id);
        return "redirect:/atm/list";  // Redirect to the ATM list after updating
    }

    // Handle ATM deletion
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/delete/{id}")
    public String deleteATM(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (atmService.findById(id) != null) {
            atmService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "ATM deleted successfully!");
            logger.info("Deleted ATM with ID: {}", id);
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "ATM not found for deletion!");
            logger.warn("Attempted to delete non-existing ATM with ID: {}", id);
        }
        return "redirect:/atm/list";  // Redirect to the ATM list after deletion
    }

    // Helper method to find an ATM by ID
    private ATM findATMById(Long id) {
        return atmService.findById(id);
    }
}
