package com.example.bankmanagement.model;

public enum TicketStatus {
    OPEN,
    IN_PROGRESS,
    ESCALATED,
    CLOSED;
	
	public String getDisplayName() {
        return name().replace("_", " ").toLowerCase(); // Example for better display
    }
}
