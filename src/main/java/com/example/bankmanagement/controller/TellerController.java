package com.example.bankmanagement.controller;

import com.example.bankmanagement.model.Teller;
import com.example.bankmanagement.service.TellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/teller")
public class TellerController {

    @Autowired
    private TellerService tellerService;

    // Display a list of tellers on the Teller Dashboard
    @GetMapping
    public String listTellers(Model model) {
        List<Teller> tellers = tellerService.getAllTellers();
        model.addAttribute("tellers", tellers);
        return "teller/teller_list";  // Assumed this is a view listing all tellers
    }

//    // Display the Teller Dashboard
//    @GetMapping("/dashboard")
//    public String showTellerDashboard(Model model) {
//        // Add necessary attributes for the dashboard (e.g., teller stats, transactions, etc.)
//        model.addAttribute("totalTellers", tellerService.getTellerCount());  // Example statistic
//        model.addAttribute("recentTransactions", tellerService.getRecentTellerTransactions());  // Hypothetical recent transaction list
//        return "teller/dashboard";  // Dashboard page for teller
//    }

    // Show form to create a new teller (from dashboard)
    @GetMapping("/new")
    public String showCreateTellerForm(Model model) {
        model.addAttribute("teller", new Teller());
        return "teller/teller_form";  // Form to create a new teller
    }

    // Process the form submission to create a new teller
    @PostMapping
    public String createTeller(@ModelAttribute("teller") Teller teller) {
        tellerService.saveTeller(teller);
        return "redirect:/tellers";  // Redirect to the list of tellers
    }

    // Show form to update an existing teller
    @GetMapping("/edit/{id}")
    public String showEditTellerForm(@PathVariable("id") Long id, Model model) {
        Teller teller = tellerService.getTellerById(id);
        model.addAttribute("teller", teller);
        return "teller/teller_form";  // Form to edit an existing teller
    }

    // Process the form submission to update a teller
    @PostMapping("/{id}")
    public String updateTeller(@PathVariable("id") Long id, @ModelAttribute("teller") Teller teller) {
        teller.setTellerId(id);
        tellerService.saveTeller(teller);
        return "redirect:/tellers";  // Redirect to the list of tellers
    }

    // Delete a teller
    @GetMapping("/delete/{id}")
    public String deleteTeller(@PathVariable("id") Long id) {
        tellerService.deleteTeller(id);
        return "redirect:/tellers";
    }
}
