package com.example.bankmanagement.repository;

import com.example.bankmanagement.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
    // Find tickets by their status (OPEN, CLOSED, etc.)
    List<Ticket> findByStatus(Ticket.TicketStatus status);

    // Find tickets assigned to a specific support staff by supportId
    List<Ticket> findBySupportStaffSupportId(Long supportId);

    int countByStatus(String string);

    // Change method name to match the actual property name
    List<Ticket> findByIssueDetailsContaining(String issueDetails);
}

