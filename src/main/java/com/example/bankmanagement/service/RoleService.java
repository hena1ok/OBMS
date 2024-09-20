package com.example.bankmanagement.service;  

import com.example.bankmanagement.model.Role;  
import com.example.bankmanagement.repository.RoleRepository;  
import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.stereotype.Service;  

import java.util.List;  
import java.util.Optional;  

@Service  
public class RoleService {  

    private final RoleRepository roleRepository;  

    @Autowired  
    public RoleService(RoleRepository roleRepository) {  
        this.roleRepository = roleRepository;  
    }  

    // Create a new role  
    public Role createRole(Role role) {  
        return roleRepository.save(role);  
    }  

    // Get a role by its ID  
    public Optional<Role> getRoleById(Long id) {  
        return roleRepository.findById(id);  
    }  

    // Get all roles  
    public List<Role> getAllRoles() {  
        return roleRepository.findAll();  
    }  

    // Update an existing role  
    public Optional<Role> updateRole(Long id, Role updatedRole) {  
        return roleRepository.findById(id).map(role -> {  
            role.setName(updatedRole.getName());  
            return roleRepository.save(role);  
        });  
    }  

    // Delete a role by its ID  
    public void deleteRole(Long id) {  
        roleRepository.deleteById(id);  
    }  
}