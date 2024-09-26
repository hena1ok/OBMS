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

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
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
        return transactionRepository.findByAccountId(accountId); // Assuming you create this method in the repository
    }

    public List<Transaction> getTransactionsByDateRange(LocalDate startDate, LocalDate endDate) {
        return transactionRepository.findByDateBetween(startDate, endDate); // You need to create this method in the repository
    }
    public Transaction createTransaction(Transaction transaction, Long userId, Long accountId) {
        // You may want to load the User and Account entities based on IDs
        User user = UserService.getUserById(userId); // Assuming you have a UserService
        Account account = AccountService.getAccountById(accountId); // Assuming you have an AccountService

        transaction.setUser(user);
        transaction.setAccount(account);
        return transactionRepository.save(transaction);
    }


}
