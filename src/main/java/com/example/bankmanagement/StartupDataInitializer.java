package com.example.bankmanagement;

import com.example.bankmanagement.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupDataInitializer implements CommandLineRunner {

    private final RoleService roleService;

    @Autowired
    public StartupDataInitializer(RoleService roleService) {
        this.roleService = roleService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Create default roles at startup
        roleService.createDefaultRoles();
    }
}
