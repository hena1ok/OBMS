package com.example.bankmanagement.service;

import com.example.bankmanagement.model.Support;
import com.example.bankmanagement.model.SupportStats;
import com.example.bankmanagement.model.Ticket;
import com.example.bankmanagement.model.User;
import com.example.bankmanagement.repository.SupportRepository;
import com.example.bankmanagement.repository.TicketRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SupportService {

    private final SupportRepository supportRepository;
    private final TicketRepository ticketRepository;

    @Autowired
    public SupportService(SupportRepository supportRepository, TicketRepository ticketRepository) {
        this.supportRepository = supportRepository;
        this.ticketRepository = ticketRepository;
    }

    // Retrieve all support staff members
    public List<Support> getAllSupportStaff() {
        return supportRepository.findAll();
    }

   

    // Add or update support staff
    public Support saveSupportStaff(Support support) {
        return supportRepository.save(support);
    }
    
 // Assuming TicketStatus is an enum
    public void updateTicketStatus(Long id, String status) {
        Optional<Ticket> optionalTicket = getTicketById(id);
        if (optionalTicket.isPresent()) {
            Ticket ticket = optionalTicket.get();
            ticket.setStatus(Ticket.TicketStatus.valueOf(status.toUpperCase())); // Ensure proper conversion
            updateTicket(ticket);
        }
    }


    // Remove support staff
    public void deleteSupportStaff(Long id) {
        supportRepository.deleteById(id);
    }

    public boolean isSupportStaff(User user) {
        return supportRepository.existsById(user.getId());
    }

    public void removeSupportStaffByUser(User user) {
        supportRepository.deleteByUser(user);
    }

    // Resolve a ticket
    public void resolveTicket(Support support, Ticket ticket) {
        support.closeTicket(ticket);
        supportRepository.save(support);
    }

    // Get statistics of a support staff (like number of tickets resolved)
    public SupportStats getSupportStats(Support support) {
        return support.getSupportStats();
    }

    public int countTotalTickets() {
        return (int) ticketRepository.count(); 
    }

    public int countResolvedTickets() {
        return ticketRepository.countByStatus("Resolved");
    }

    public int countTotalEscalations() {
        return ticketRepository.countByStatus("Escalated");
    }

   

	public void saveTicket(Ticket ticket) {
		
		ticketRepository.save(ticket);
	}

	

    // Create a new ticket
    public void createTicket(Ticket ticket) {
        ticketRepository.save(ticket);
    }

    // Get all tickets
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    // Get ticket by ID
    public Optional<Ticket> getTicketById(Long id) {
        return ticketRepository.findById(id);
    }

    // Search tickets based on issue details
    public List<Ticket> searchTickets(String query) {
        // Implement search logic here
        // This could involve using a method in your TicketRepository
        return ticketRepository.findByIssueDetailsContaining(query);
    }

    // Update a ticket
    public void updateTicket(Ticket ticket) {
        ticketRepository.save(ticket);
    }

    // Assign a ticket to support staff
    public void assignTicket(Support support, Ticket ticket) {
        ticket.setSupportStaff(support);
        ticket.setStatus(Ticket.TicketStatus.OPEN); // Assuming you have an ASSIGNED status
        ticketRepository.save(ticket);
    }

    // Get support staff by ID
    public Optional<Support> getSupportById(Long id) {
        return supportRepository.findById(id);
    }
}
