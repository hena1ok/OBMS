package com.example.bankmanagement.model;  

import jakarta.persistence.*;  
import java.util.Set;  

@Entity  
@Table(name = "users")  
public class User {  
    @Id  
    @GeneratedValue(strategy = GenerationType.IDENTITY)  
    private Long id;  

    @Column(nullable = false, unique = true)  
    private String username;  

    @Column(nullable = false)  
    private String password;  
    
    @Column(nullable = false, unique = true)  
    private String email;  

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)  
    private Set<Account> accounts;  

    @ManyToMany(fetch = FetchType.EAGER)  // Use EAGER or LAZY as needed  
    @JoinTable(  
        name = "user_roles",  
        joinColumns = @JoinColumn(name = "user_id"),  
        inverseJoinColumns = @JoinColumn(name = "role_id")  
    )  
    private Set<Role> roles; // Set of roles associated with the user  

    // Default constructor  
    public User() {  
        // Default constructor necessary for JPA  
    }  

    // Getters and Setters  
    public Long getId() {  
        return id;  
    }  

    public void setId(Long id) {  
        this.id = id;  
    }  

    public String getUsername() {  
        return username;  
    }  

    public void setUsername(String username) {  
        this.username = username;  
    }  

    public String getPassword() {  
        return password;  
    }  

    public void setPassword(String password) {  
        this.password = password;  
    }  

    public String getEmail() {  
        return email;  
    }  

    public void setEmail(String email) {  
        this.email = email;  
    }  

    public Set<Account> getAccounts() {  
        return accounts;  
    }  

    public void setAccounts(Set<Account> accounts) {  
        this.accounts = accounts;  
    }  

    public Set<Role> getRoles() {  
        return roles;  
    }  

    public void setRoles(Set<Role> roles) {  
        this.roles = roles;  
    }  

    // You may want to add a constructor with parameters  
    public User(String username, String password, String email, Set<Account> accounts, Set<Role> roles) {  
        this.username = username;  
        this.password = password;  
        this.email = email;  
        this.accounts = accounts;  
        this.roles = roles;  
    }  
}