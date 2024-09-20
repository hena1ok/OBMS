package com.example.bankmanagement.model;  

import jakarta.persistence.*;  
import java.util.Set;  

@Entity  
@Table(name = "roles") // Table for roles  
public class Role {  

    @Id  
    @GeneratedValue(strategy = GenerationType.IDENTITY)  
    private Long id;  

    @Column(nullable = false, unique = true)  
    private String name; // The name of the role (e.g., "ROLE_USER")  

    @ManyToMany(mappedBy = "roles")  
    private Set<User> users; // Set of users that have this role  

    // Constructors  
    public Role() {}  

    public Role(String name) {  
        this.name = name;  
    }  

    // Getters and Setters  
    public Long getId() {  
        return id;  
    }  

    public void setId(Long id) {  
        this.id = id;  
    }  

    public String getName() {  
        return name;  
    }  

    public void setName(String name) {  
        this.name = name;  
    }  

    public Set<User> getUsers() {  
        return users;  
    }  

    public void setUsers(Set<User> users) {  
        this.users = users;  
    }  
}