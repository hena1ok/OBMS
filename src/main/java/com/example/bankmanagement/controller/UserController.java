package com.example.bankmanagement.controller;

import com.example.bankmanagement.model.Role;
import com.example.bankmanagement.model.Support;
import com.example.bankmanagement.model.User;
import com.example.bankmanagement.repository.RoleRepository;
import com.example.bankmanagement.security.CustomUserDetails;
import com.example.bankmanagement.service.RoleService;
import com.example.bankmanagement.service.SupportService;
import com.example.bankmanagement.service.UserService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class UserController {
    
    private final UserService userService;

    @Autowired
    private RoleService roleService;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private SupportService supportService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/manage_users")
    public String manageUsers(Model model) {
        List<User> users = userService.getAllUsers();
        List<Role> roles = roleService.getAllRoles();
        model.addAttribute("users", users);
        model.addAttribute("roles", roles);
        return "user/manage_user"; // Thymeleaf template name
    }

    @PostMapping("/updateRole/{id}")
    public String updateUserRole(@PathVariable Long id, @RequestParam("role") String roleName) {
        // Get the user by ID
        User user = userService.getUserById(id);
        
        // Fetch the role by name
        Optional<Role> roleOptional = roleService.getRoleByName(roleName);

        if (roleOptional.isPresent()) {
            // Set the new role for the user
            Role role = roleOptional.get();
            user.getRoles().clear(); // Clear previous roles
            user.getRoles().add(role); // Add the new role

            // Save the updated user with the new role
            userService.saveUser(user);

            // Check if the new role is ROLE_SUPPORT
            if ("ROLE_SUPPORT".equals(roleName)) {
                // Insert the user into the support_staff table if not already present
                if (!supportService.isSupportStaff(user)) {
                    Support support = new Support();
                    support.setUser(user);
                    support.setName(user.getUsername()); // Assuming user has fullName field
                    support.setEmail(user.getEmail());   // Assuming user has email field
                    support.setPhone(user.getPhone());   // Assuming user has phone field
                    support.setAvailability(true);       // Default availability to true

                    // Save support staff record
                    supportService.saveSupportStaff(support);
                }
            }
        } else {
            return "redirect:/user/manage_users?error=RoleNotFound";
        }

        return "redirect:/user/manage_users";
    }


    @GetMapping("/profile")
    public String userProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long userId = userDetails.getId(); // Adjust this method according to your implementation

            Optional<User> optionalUser = userService.findById(userId);
            User user = optionalUser.orElseThrow(() -> new RuntimeException("User not found"));

            model.addAttribute("user", user);
            return "user/profile";
        } else {
            throw new RuntimeException("Authentication not of expected type");
        }
    }

    @GetMapping("/manage_profile")
    public String manageProfile(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long userId = userDetails.getId();
        Optional<User> optionalUser = userService.findById(userId);
        User user = optionalUser.orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        return "user/manage_profile"; // Thymeleaf template for managing user profile
    }

    @PostMapping("/manage_profile")
    public String updateProfile(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        userService.updateUser(user.getId(), user); // Ensure user ID is set correctly
        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        return "redirect:/user/manage_profile"; // Redirect back to the manage profile page
    }

    @GetMapping("/search")
    public String searchUsers(@RequestParam String query, Model model) {
        model.addAttribute("users", userService.searchUsers(query));
        return "user/manage_user";
    }

    @GetMapping("/manage_profile/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        List<Role> roles = roleService.getAllRoles();
        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        return "user/edit_user"; // The Thymeleaf template to render
    }

    @PostMapping("/manage_profile/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute User user) {
        userService.updateUser(id, user);
        return "redirect:/user/manage_users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/user/manage_users";
    }

    @GetMapping("/toggle/{id}")
    public String toggleUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user != null) {
            user.setEnabled(!user.isEnabled());
            userService.updateUser(id, user);
        }
        return "redirect:/user/manage_users";
    }

    @GetMapping("/create")
    public String showCreateUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleService.getAllRoles()); // Fetch roles from the service
        return "user/create"; // Return the view name
    }

    @PostMapping("/create")
    public ResponseEntity<String> createUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam(required = false) Set<Long> roleIds,
            @RequestParam String dob,                // Add Date of Birth
            @RequestParam String email,              // Add Email
            @RequestParam String firstName,          // Add First Name
            @RequestParam String lastName,           // Add Last Name
            @RequestParam String phone,              // Add Phone Number
            @RequestParam String accountType,        // Add Account Type
            @RequestParam String address) {          // Add Address

        try {
            // Log the role IDs being passed for debugging purposes
            if (roleIds != null) {
                System.out.println("Received role IDs: " + roleIds);
            }

            // Create a new user instance and set the username and password
            User userRequest = new User();
            userRequest.setUsername(username);
            userRequest.setPassword(password);
            
            // Set additional user properties
            userRequest.setDob(LocalDate.parse(dob)); // Assuming dob is of type LocalDate
            userRequest.setEmail(email);
            userRequest.setFirstName(firstName);
            userRequest.setLastName(lastName);
            userRequest.setPhone(phone);
            userRequest.setAccountType(accountType);
            userRequest.setAddress(address);
            userRequest.setRegistrationDate(LocalDateTime.now()); // Set registration date

            // Fetch roles from the repository based on the role IDs provided
            Set<Role> userRoles = new HashSet<>();
            if (roleIds != null && !roleIds.isEmpty()) {
                for (Long roleId : roleIds) {
                    Role role = roleRepository.findById(roleId)
                            .orElseThrow(() -> new RuntimeException("Role with ID '" + roleId + "' not found."));
                    userRoles.add(role);
                }
            } else {
                // Assign a default role if no role IDs are provided (e.g., ROLE_USER)
                Role defaultRole = roleRepository.findByName("ROLE_USER")
                        .orElseThrow(() -> new RuntimeException("Default role 'ROLE_USER' not found."));
                userRoles.add(defaultRole);
            }

            // Set the roles for the user
            userRequest.setRoles(userRoles);

            // Convert Set<Role> to Set<String>
            Set<String> roleNames = userRoles.stream()
                                              .map(Role::getName)
                                              .collect(Collectors.toSet());

            // Save the user using the service layer and get the saved User
            User savedUser = userService.saveUser(userRequest, roleNames);

            return new ResponseEntity<>("User created successfully with ID: " + savedUser.getId(), HttpStatus.CREATED);

        } catch (RuntimeException e) {
            // Handle specific exceptions related to role assignment or other issues
            return new ResponseEntity<>("Failed to create user: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // General error handling
            return new ResponseEntity<>("An error occurred while creating the user.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




    
}
