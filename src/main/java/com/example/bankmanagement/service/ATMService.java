package com.example.bankmanagement.service;

import com.example.bankmanagement.model.ATMTransaction;
import com.example.bankmanagement.model.Account;
import com.example.bankmanagement.repository.AccountRepository;
import com.example.bankmanagement.repository.ATMTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ATMService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ATMTransactionRepository transactionRepository;

    public Optional<Account> getAccount(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    public boolean withdraw(String accountNumber, double amount) {
        Optional<Account> account = getAccount(accountNumber);
        if (account.isPresent() && account.get().getBalance() >= amount) {
            account.get().withdraw(amount); // Withdraw from account
            ATMTransaction transaction = new ATMTransaction(account.get().getId(), "WITHDRAWAL", amount, LocalDateTime.now());
            transactionRepository.save(transaction); // Save the transaction
            return true; // Withdrawal successful
        }
        return false; // Insufficient funds or account not found
    }


    public boolean deposit(String accountNumber, double amount) {
    	Optional<Account> account = getAccount(accountNumber);
        if (account.isPresent()) {
            account.get().deposit(amount);
            transactionRepository.save(new ATMTransaction(account.get().getId(), "DEPOSIT", amount, LocalDateTime.now()));
            return true; // Deposit successful
        }
        return false; // Account not found
    }

    public double getBalance(String accountNumber) {
        Optional<Account> account = accountRepository.findByAccountNumber(accountNumber);
        return account.map(Account::getBalance).orElse(0.0); // Return balance or 0 if account not found
    }


    // Implement other ATM operations...
}