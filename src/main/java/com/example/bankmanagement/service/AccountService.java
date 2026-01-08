package com.example.bankmanagement.service;

import com.example.bankmanagement.model.Account;
import com.example.bankmanagement.model.User;
import com.example.bankmanagement.repository.AccountRepository;
import com.example.bankmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Service class for managing bank accounts.
 */
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    /**
     * Retrieves all bank accounts.
     * @return a list of all accounts.
     */
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }
    public List<Account> findAccountsByUserId(Long userId) {
        // Fetch accounts for the logged-in user
        return accountRepository.findByUserId(userId);
    }
    /**
     * Retrieves a bank account by its ID.
     * @param accountId the ID of the account to retrieve.
     * @return the account with the specified ID.
     */
    public Account getAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + accountId));
    }

    /**
     * Retrieves a user by their username.
     * This method finds a `User` entity using the username, which is a unique identifier for authentication.
     * @param username the username of the user to retrieve.
     * @return the `User` object corresponding to the provided username.
     */
    public List<Account> findAccountsByUser(String username) {
        // Fetch the user by username
        Optional<User> user = userRepository.findByUsername(username);
        
        if (user.isEmpty()) {
            return Collections.emptyList(); // Return an empty list if the user is not found
        }
        
        // Fetch accounts linked to the user
        return accountRepository.findByUserId(user.get().getId());
    }



    /**
     * Creates a new bank account for a specified user.
     * @param account the account details to create.
     * @param userId the ID of the user for whom the account is created.
     * @return the created account.
     */
    public Account createAccount(Account account, Long userId) {
        // Retrieve the user associated with the provided userId
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        
        // Associate the user with the new account
        account.setUser(user);
        
        // Set the account creation date
        account.setDateOpened(LocalDate.now());
        
        // Optionally generate a random account number for the new account
        account.setAccountNumber(generateAccountNumber());
        
        return accountRepository.save(account);
    }

    /**
     * Updates an existing bank account with new details.
     * @param accountId the ID of the account to update.
     * @param accountDetails the new details for the account.
     * @return the updated account.
     */
    public Account updateAccount(Long accountId, Account accountDetails) {
        // Retrieve the existing account or throw an exception if it doesn't exist
        Account existingAccount = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + accountId));

        // Update the account fields with the provided details
        existingAccount.setAccountName(accountDetails.getAccountName());
        existingAccount.setAccountType(accountDetails.getAccountType());

        // Validate the balance before updating
        if (accountDetails.getBalance() == null || accountDetails.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Balance must be positive");
        }

        // Update the balance if validation passes
        existingAccount.setBalance(accountDetails.getBalance());

        // Save the updated account back to the repository
        return accountRepository.save(existingAccount);
    }
    public Optional<Account> findById(Long accountId) {
        return accountRepository.findById(accountId);
    }

    /**
     * Deletes a bank account by its ID.
     * @param accountId the ID of the account to delete.
     */
    public void deleteAccount(Long accountId) {
        // Verify if the account exists before deleting it
        if (!accountRepository.existsById(accountId)) {
            throw new IllegalArgumentException("Account not found with id: " + accountId);
        }
        accountRepository.deleteById(accountId);
    }

    /**
     * Saves the account.
     * @param account the account to save.
     */
    public void save(Account account) {
        accountRepository.save(account);
    }

    /**
     * Generates a unique account number.
     * This method generates a 10-digit random account number.
     * @return the generated account number.
     */
    public String generateAccountNumber() {
        // Generate a random 10-digit number (formatted to always be 10 digits)
        return String.format("%010d", new Random().nextInt(1_000_000_000));
    }
   
    public List<Account> findByUser(User user) {
        return accountRepository.findByUser(user); // Assuming you have a method in the repository
    }
	public List<Account> findAll() {
		// TODO Auto-generated method stub
		return accountRepository.findAll();
	}

	 public List<Account> searchAccounts(String searchTerm) {
	        return accountRepository.findByUserUsernameContainingOrUserPhoneContaining(searchTerm, searchTerm);
	    }

}
