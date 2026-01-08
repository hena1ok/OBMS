package com.example.bankmanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "support_staff")
public class Support {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "support_id")
    private Long supportId;

    @NotBlank(message = "Name is required")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Pattern(regexp = "^\\+?[0-9. ()-]{7,15}$", message = "Invalid phone number format")
    @NotBlank(message = "Phone number is required")
    @Column(name = "phone", nullable = false, length = 15)
    private String phone;

    @Column(name = "specialization", length = 100)
    private String specialization;

    @Column(name = "availability", nullable = false)
    private boolean availability = true;

    @OneToMany(mappedBy = "supportStaff", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Ticket> supportTickets = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	// Constructors
    public Support() {}

    public Support(String name, String email, String phone, String specialization) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.specialization = specialization;
        this.availability = true;
    }

    // Getters and Setters
    public Long getSupportId() {
        return supportId;
    }

    public void setSupportId(Long supportId) {
        this.supportId = supportId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    public List<Ticket> getSupportTickets() {
        return supportTickets;
    }

    public void setSupportTickets(List<Ticket> supportTickets) {
        this.supportTickets = supportTickets;
    }

    // Additional Methods
    public void openTicket(User user, String issueDetails) {
        Ticket newTicket = new Ticket(user, this, issueDetails);
        supportTickets.add(newTicket);
    }

    public void closeTicket(Ticket ticket) {
        ticket.setStatus(Ticket.TicketStatus.CLOSED);
    }

    public void assignTicket(Ticket ticket) {
        ticket.setSupportStaff(this);
        supportTickets.add(ticket);
    }

    public void respondToTicket(Ticket ticket, String response) {
        ticket.addResponse(response);
    }

    public void escalateTicket(Ticket ticket, Support higherLevelSupport) {
        ticket.setSupportStaff(higherLevelSupport);
        supportTickets.remove(ticket);
    }

    public SupportStats getSupportStats() {
        // Placeholder for calculating statistics such as resolved tickets, etc.
        return new SupportStats();
    }

    @Override
    public String toString() {
        return "Support [supportId=" + supportId + ", name=" + name + ", email=" + email + 
               ", phone=" + phone + ", specialization=" + specialization + 
               ", availability=" + availability + "]";
    }
}
