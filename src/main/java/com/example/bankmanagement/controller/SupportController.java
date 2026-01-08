package com.example.bankmanagement.controller;

import com.example.bankmanagement.model.Support;
import com.example.bankmanagement.model.Ticket;
import com.example.bankmanagement.model.Ticket.TicketStatus;
import com.example.bankmanagement.model.User;
import com.example.bankmanagement.service.SupportService;
import com.example.bankmanagement.service.UserService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/support")
public class SupportController {

    @Autowired
    private SupportService supportService;

    @Autowired
    private UserService userService;

    // Display the form for creating a new support staff
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("support", new Support());
        return "support/support_registration";
    }

    // Register new support staff
    @PostMapping("/register")
    public String registerSupportStaff(@ModelAttribute("support") Support support) {
        supportService.saveSupportStaff(support);
        return "redirect:/support/staff";
    }

    // Show the create ticket form
    @GetMapping("/create_ticket")
    public String showCreateTicketForm(Model model) {
        model.addAttribute("ticket", new Ticket());
        List<Support> supportStaffList = supportService.getAllSupportStaff();
        model.addAttribute("supportStaffList", supportStaffList);
        return "support/create_ticket";
    }

    // Handle the submission of a new ticket
    @PostMapping("/create_ticket")
    public String submitTicket(@Valid @ModelAttribute("ticket") Ticket ticket,
                               BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "support/create_ticket";
        }
        try {
            supportService.createTicket(ticket);
            redirectAttributes.addFlashAttribute("message", "Support ticket created successfully!");
            return "redirect:/support/tickets";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while creating the ticket.");
            return "redirect:/support/create_ticket";
        }
    }


    // Display the list of all tickets
    @GetMapping("/tickets")
    public String listTickets(Model model) {
        List<Ticket> tickets = supportService.getAllTickets();
        model.addAttribute("tickets", tickets);
        return "support/manage_tickets";
    }

    // Search for tickets based on the query parameter (issue details)
    @GetMapping("/search_tickets")
    public String searchTickets(@RequestParam("query") String query, Model model) {
        List<Ticket> searchResults = supportService.searchTickets(query);
        model.addAttribute("tickets", searchResults);
        return "support/manage_tickets";
    }

    // View details of a specific ticket
    @GetMapping("/ticket/{id}")
    public String viewTicket(@PathVariable Long id, Model model) {
        Optional<Ticket> ticket = supportService.getTicketById(id);
        if (ticket.isPresent()) {
            model.addAttribute("ticket", ticket.get());
            return "support/view_ticket";
        } else {
            model.addAttribute("error", "Ticket not found");
            return "redirect:/support/tickets";
        }
    }

    // Update the status of a ticket
    @PostMapping("/ticket/update/{id}")
    public String updateTicketStatus(@PathVariable Long id,
                                     @RequestParam("status") TicketStatus status,
                                     Model model) {
        Optional<Ticket> optionalTicket = supportService.getTicketById(id);
        if (optionalTicket.isPresent()) {
            Ticket ticket = optionalTicket.get();
            ticket.setStatus(status);
            supportService.updateTicket(ticket);
            return "redirect:/support/tickets";
        } else {
            model.addAttribute("error", "Ticket not found");
            return "redirect:/support/tickets";
        }
    }

    // Assign a ticket to a support staff member
    @PostMapping("/tickets/{ticketId}/assign/{supportId}")
    public String assignTicket(@PathVariable Long ticketId, 
                               @PathVariable Long supportId) {
        Optional<Ticket> ticket = supportService.getTicketById(ticketId);
        Optional<Support> support = supportService.getSupportById(supportId);

        if (ticket.isPresent() && support.isPresent()) {
            supportService.assignTicket(support.get(), ticket.get());
        }

        return "redirect:/support/tickets";
    }
    @GetMapping("/knowledge-base")
    public String showKnowledgeBase(Model model) {
        // You can add any additional model attributes here if needed
        return "support/knowledge-base";  // Return the name of the HTML template (knowledge_base.html)
    }
    
    
}
