package com.example.bankmanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Optional;

@Entity
@Table(name = "support_tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Long ticketId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)  // Ensure this is not null
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "support_id", nullable = true)
    private Support supportStaff;


    @Column(name = "issue_details", nullable = false, length = 500)
    private String issueDetails;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING) // Store enum as a string in the database
    @Column(name = "status", nullable = false)
    private TicketStatus status;

    @Column(name = "responses", length = 1000)
    private String responses;

    // Enum for TicketStatus
    public enum TicketStatus {
        OPEN,
        IN_PROGRESS,
        CLOSED,
        ESCALATED
    }

    // Constructors
    public Ticket() {
        this.createdAt = LocalDateTime.now();
        this.status = TicketStatus.OPEN; // Default to OPEN when created
    }

    public Ticket(User user, Support supportStaff, String issueDetails) {
        this();
        this.user = user;
        this.supportStaff = supportStaff;
        this.issueDetails = issueDetails;
    }

    // Getters and Setters
    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Support getSupportStaff() {
        return supportStaff;
    }

    public void setSupportStaff(Support supportStaff) {
        this.supportStaff = supportStaff;
    }

    public String getIssueDetails() {
        return issueDetails;
    }

    public void setIssueDetails(String issueDetails) {
        this.issueDetails = issueDetails;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public String getResponses() {
        return responses;
    }

    public void setResponses(String responses) {
        this.responses = responses;
    }

    // Methods for ticket management
    public void addResponse(String response) {
        if (this.responses == null || this.responses.isEmpty()) {
            this.responses = response;
        } else {
            this.responses += "\n" + response;
        }
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();  // Ensure createdAt is set when the entity is persisted
    }



	
}
