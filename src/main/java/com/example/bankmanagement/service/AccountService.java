package com.example.bankmanagement.service;

import com.example.bankmanagement.model.Account;
import com.example.bankmanagement.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    

    // Get all accounts
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    // Get account by ID
    public Account getAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + accountId));
    }


    // Create a new account
    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    // Update an existing account
    public Account updateAccount(Long id, Account accountDetails) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new RuntimeException("Account not found"));
        account.setAccountNumber(accountDetails.getAccountNumber());
        account.setAccountType(accountDetails.getAccountType());
        account.setBalance(accountDetails.getBalance());
        return accountRepository.save(account);
    }

    // Delete an account
    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }
    

}
