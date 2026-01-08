package com.example.bankmanagement.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.bankmanagement.model.Role;
import com.example.bankmanagement.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
public class RoleInitializer {

    private static final Logger logger = LoggerFactory.getLogger(RoleInitializer.class);

    @Autowired
    private RoleRepository roleRepository;

    @PostConstruct
    public void init() {
        createRoleIfNotFound("ROLE_USER");
        createRoleIfNotFound("ROLE_ADMIN");
        createRoleIfNotFound("ROLE_TELLER");
        createRoleIfNotFound("ROLE_SUPPORT");
    }

    private void createRoleIfNotFound(String roleName) {
        try {
            Optional<Role> existingRole = roleRepository.findByName(roleName);
            
            if (existingRole.isEmpty()) {
                Role role = new Role();
                role.setName(roleName);
                roleRepository.save(role);
                logger.info("Role '{}' created and saved to the database.", roleName);
            } else {
                logger.info("Role '{}' already exists in the database.", roleName);
            }
        } catch (Exception e) {
            logger.error("Error creating role '{}': {}", roleName, e.getMessage());
        }
    }
}
