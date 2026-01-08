package com.example.bankmanagement.service;

import com.example.bankmanagement.model.Ticket;
import com.example.bankmanagement.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    // Retrieve all open tickets
    public List<Ticket> getOpenTickets() {
        return ticketRepository.findByStatus(Ticket.TicketStatus.OPEN);
    }

    // Retrieve a ticket by its ID
    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id).orElse(null);
    }

    // Save or update a ticket
    public void saveTicket(Ticket ticket) {
        ticketRepository.save(ticket);
    }

    // Retrieve all tickets
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    // Retrieve tickets assigned to a specific support staff
    public List<Ticket> getTicketsBySupportStaff(Long supportId) {
        return ticketRepository.findBySupportStaffSupportId(supportId);
    }

    // Retrieve tickets based on their status
    public List<Ticket> getTicketsByStatus(Ticket.TicketStatus status) {
        return ticketRepository.findByStatus(status);
    }
}
