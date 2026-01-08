package com.example.bankmanagement.model;

public class SupportStats {

    private int totalTicketsResolved;
    private double averageResponseTime;
    private int totalTicketsAssigned;

    // Constructors, Getters, and Setters
    public SupportStats() {
    }

    public SupportStats(int totalTicketsResolved, double averageResponseTime, int totalTicketsAssigned) {
        this.totalTicketsResolved = totalTicketsResolved;
        this.averageResponseTime = averageResponseTime;
        this.totalTicketsAssigned = totalTicketsAssigned;
    }

    // Getters and Setters
    public int getTotalTicketsResolved() {
        return totalTicketsResolved;
    }

    public void setTotalTicketsResolved(int totalTicketsResolved) {
        this.totalTicketsResolved = totalTicketsResolved;
    }

    public double getAverageResponseTime() {
        return averageResponseTime;
    }

    public void setAverageResponseTime(double averageResponseTime) {
        this.averageResponseTime = averageResponseTime;
    }

    public int getTotalTicketsAssigned() {
        return totalTicketsAssigned;
    }

    public void setTotalTicketsAssigned(int totalTicketsAssigned) {
        this.totalTicketsAssigned = totalTicketsAssigned;
    }
}
