package com.example.bankmanagement.service;

import com.example.bankmanagement.model.Account;
import com.example.bankmanagement.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    private static AccountRepository accountRepository; // Keep this as an instance variable

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public static Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + id));
    }

    public void createAccount(Account account) {
        accountRepository.save(account);
    }

    public void updateAccount(Long id, Account updatedAccount) {
        if (!accountRepository.existsById(id)) {
            throw new IllegalArgumentException("Account not found with id: " + id);
        }
        updatedAccount.setId(id);
        accountRepository.save(updatedAccount);
    }

    public void deleteAccount(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new IllegalArgumentException("Account not found with id: " + id);
        }
        accountRepository.deleteById(id);
    }

    public Optional<Account> findByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }
}
