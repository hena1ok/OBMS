package com.example.bankmanagement.controller;

import com.example.bankmanagement.model.Loan;
import com.example.bankmanagement.model.User;
import com.example.bankmanagement.security.CustomUserDetails;
import com.example.bankmanagement.service.LoanService;
import com.example.bankmanagement.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/loan")
public class LoanController {

	@Autowired
	private LoanService loanService;
	@Autowired
	private UserService userService;

	// Display a list of all loans
	@GetMapping("/list")
	public String listLoans(Model model) {
		List<Loan> loans = loanService.getAllLoans();
		model.addAttribute("loans", loans);
		return "loan/loan_list"; // Thymeleaf template for loan list
	}

	// Show the loan application form
	@GetMapping("/apply")
	public String showLoanForm(Model model) {
	    model.addAttribute("loan", new Loan()); // Create a new Loan object for the form
	    model.addAttribute("loanTypes", loanService.getAllLoanTypes()); // Add loan types for selection
	   return "loan/loan_form"; // Thymeleaf template for loan form
	}


	// Handle loan application submission
	@PostMapping("/apply")
	public String applyForLoan(@ModelAttribute("loan") Loan loan,
			BindingResult bindingResult,
	                           @AuthenticationPrincipal CustomUserDetails currentUser,
	                           Model model,
	                           RedirectAttributes redirectAttributes) {
		 if (bindingResult.hasErrors()) {
		        // Validation errors exist

		        // Re-populate loan types so dropdown works
		        model.addAttribute("loanTypes", loanService.getAllLoanTypes());

		        // Populate user emails if needed
		        if (currentUser.hasRole("ADMIN") || currentUser.hasRole("TELLER")) {
		            model.addAttribute("userEmails", userService.getAllUserEmails());
		        }

		        // Return to form to show errors
		        return "loan/loan_form";
		    }
		try {
	        // Check if the current user is an ADMIN or TELLER
	        if (currentUser.hasRole("ADMIN") || currentUser.hasRole("TELLER")) {
	            // Associate the loan with the selected user (email)
	            Optional<User> selectedUserOptional = userService.findByEmail(loan.getUser().getEmail());
	            if (selectedUserOptional.isPresent()) {
	                // Extract the User from Optional and set it
	                loan.setUser(selectedUserOptional.get());
	            } else {
	                throw new Exception("Selected user not found.");
	            }
	        } else {
	            // Default behavior for USER role
	            loan.setUser(currentUser.getUser());
	        }

	        // Apply for the loan and associate it with the current user
	        Loan savedLoan = loanService.applyForLoan(loan, loan.getUser());

	        // Add success message to redirect attributes so it can be displayed after redirect
	        redirectAttributes.addFlashAttribute("successMessage", "Loan application submitted successfully!");

	        // Redirect to the loan list or dashboard (as per role or preference) on success
	        return "redirect:/loan/list"; 

	    } catch (Exception e) {
	        // Handle any exceptions and display error messages
	        model.addAttribute("errorMessage", e.getMessage());

	        // Re-populate loan types in case of error
	        model.addAttribute("loanTypes", loanService.getAllLoanTypes());

	        // Populate user emails if the user is ADMIN or TELLER
	        if (currentUser.hasRole("ADMIN") || currentUser.hasRole("TELLER")) {
	            model.addAttribute("userEmails", userService.getAllUserEmails());
	        }

	        // Redirect back to the form with existing loan details and error message
	        return "loan/loan_form";
	    }
	}

	



	// Approve a specific loan application
	@PostMapping("/approve/{id}")
	public String approveLoan(@PathVariable Long id, Model model) {
		try {
			loanService.acceptLoan(id); // Service method to approve the loan
			model.addAttribute("successMessage", "Loan application approved successfully.");
		} catch (IllegalArgumentException e) {
			model.addAttribute("errorMessage", e.getMessage()); // Capture error messages
		}
		return "redirect:/loan/list"; // Redirect to the loan list after attempting approval
	}

	// Reject a specific loan application
	@PostMapping("/reject/{id}")
	public String rejectLoan(@PathVariable Long id, Model model) {
		try {
			loanService.rejectLoan(id); // Assuming this method handles the rejection logic
			model.addAttribute("successMessage", "Loan rejected successfully!");
		} catch (Exception e) {
			model.addAttribute("errorMessage", "Error rejecting loan: " + e.getMessage());
		}
		return "redirect:/loan/list"; // Redirect back to the loan list
	}

	// Show the loan edit form
	@GetMapping("/edit/{id}")
	public String editLoan(@PathVariable Long id, Model model) {
		Loan loan = loanService.getLoanById(id);
		model.addAttribute("loan", loan);
		model.addAttribute("loanTypes", loanService.getAllLoanTypes()); // Add loan types for selection
		return "loan/loan_form"; // Thymeleaf template for loan form
	}

	// Handle loan edit submission
	@PostMapping("/edit/{id}")
	public String updateLoan(@PathVariable Long id, @ModelAttribute("loan") Loan loan, Model model) {
		try {
			loanService.updateLoan(id, loan); // Service method to update the loan
			model.addAttribute("successMessage", "Loan updated successfully.");
		} catch (Exception e) {
			model.addAttribute("errorMessage", e.getMessage());
			model.addAttribute("loanTypes", loanService.getAllLoanTypes());
			return "loan/loan_form"; // Redirect back to the form on error
		}
		return "redirect:/loan/list"; // Redirect to the loan list after successful update
	}

	// View loan details
	@GetMapping("/details/{id}")
	public String viewLoanDetails(@PathVariable Long id, Model model) {
		Loan loan = loanService.getLoanById(id);
		model.addAttribute("loan", loan);
		return "loan/loan_details"; // Thymeleaf template for loan details
	}
}
