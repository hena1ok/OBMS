package com.example.bankmanagement.service;

import com.example.bankmanagement.model.Account;
import com.example.bankmanagement.model.Transaction;
import com.example.bankmanagement.model.User;
import com.example.bankmanagement.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserService userService; // Inject UserService to fetch User
    private final AccountService accountService; // Inject AccountService to fetch Account

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, UserService userService, AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.userService = userService; // Initialize UserService
        this.accountService = accountService; // Initialize AccountService
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found with id: " + id));
    }

    public void createTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    public void updateTransaction(Long id, Transaction updatedTransaction) {
        if (!transactionRepository.existsById(id)) {
            throw new IllegalArgumentException("Transaction not found with id: " + id);
        }
        updatedTransaction.setId(id);
        transactionRepository.save(updatedTransaction);
    }

    public void deleteTransaction(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new IllegalArgumentException("Transaction not found with id: " + id);
        }
        transactionRepository.deleteById(id);
    }

    public List<Transaction> getTransactionsByUserId(Long userId) {
        return transactionRepository.findByUserId(userId); // Assuming you create this method in the repository
    }

    public List<Transaction> getTransactionsByAccountId(Long accountId) {
        // You can directly call the repository method with accountId
        return transactionRepository.findByAccountId(accountId);
    }



    public List<Transaction> getTransactionsByDateRange(LocalDate startDate, LocalDate endDate) {
        return transactionRepository.findByDateBetween(startDate, endDate); // You need to create this method in the repository
    }
}
