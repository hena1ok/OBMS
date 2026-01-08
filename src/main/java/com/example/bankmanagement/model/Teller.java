package com.example.bankmanagement.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "tellers")
public class Teller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "teller_id")
    private Long tellerId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "phone", nullable = false, length = 15)
    private String phone;

    @Column(name = "branch", nullable = false, length = 100)
    private String branch;

    @OneToMany(mappedBy = "teller", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions;

    // Constructors
    public Teller() {
    }

    public Teller(String name, String email, String phone, String branch) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.branch = branch;
    }

    public Teller(Long tellerId) {
        this.tellerId = tellerId;
    }

    // Getters and Setters
    public Long getTellerId() {
        return tellerId;
    }

    public void setTellerId(Long tellerId) {
        this.tellerId = tellerId;
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

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }


	

	

	
}
