package com.example.bankmanagement.service;

import com.example.bankmanagement.model.Account;
import com.example.bankmanagement.model.Teller;
import com.example.bankmanagement.model.Transaction;
import com.example.bankmanagement.model.Transaction.TransactionStatus;
import com.example.bankmanagement.model.Transaction.TransactionType;
import com.example.bankmanagement.model.User;
import com.example.bankmanagement.repository.AccountRepository;
import com.example.bankmanagement.repository.TransactionRepository;
import com.example.bankmanagement.repository.TellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TellerRepository tellerRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository, TellerRepository tellerRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.tellerRepository = tellerRepository;
    }

    public List<Transaction> getRecentTransactions() {
        return transactionRepository.findTop10ByOrderByTransactionDateDesc(); // Adjust query as necessary
    }

    public long getTotalTransactions() {
        return transactionRepository.count();
    }

    @Transactional
    public Transaction createTransaction(BigDecimal amount, Long sourceAccountId,
                                          Long destinationAccountId, TransactionType transactionType,
                                          String description, User user, Long tellerId) {
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setTransactionType(transactionType);
        transaction.setDescription(description);
        transaction.setUser(user);

        if (tellerId != null) {
            Teller teller = tellerRepository.findById(tellerId)
                    .orElseThrow(() -> new IllegalArgumentException("Teller not found"));
            transaction.setTeller(teller);
        }

        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setSourceAccount(accountRepository.findById(sourceAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Source account not found")));
        
        if (destinationAccountId != null) {
            transaction.setDestinationAccount(accountRepository.findById(destinationAccountId)
                    .orElseThrow(() -> new IllegalArgumentException("Destination account not found")));
        }

        return transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactionsByAccount(Long accountId) {
        return transactionRepository.findBySourceAccount_Id(accountId);
    }

    public List<Transaction> getTransactionsByUser(Long userId) {
        return transactionRepository.findByUserId(userId);
    }

    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }
}
