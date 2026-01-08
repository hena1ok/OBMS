package com.example.bankmanagement.service;

import com.example.bankmanagement.model.Teller;
import com.example.bankmanagement.model.Transaction;
import com.example.bankmanagement.repository.TellerRepository;
import com.example.bankmanagement.repository.TransactionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TellerService {

    @Autowired
    private TellerRepository tellerRepository;
    @Autowired
    private TransactionService transactionService;
    // Retrieve all tellers
    public List<Teller> getAllTellers() {
        return tellerRepository.findAll();
    }

    // Retrieve a teller by ID
    public Teller getTellerById(Long id) {
        return tellerRepository.findById(id).orElse(null);
    }

    // Save or update a teller
    public void saveTeller(Teller teller) {
        tellerRepository.save(teller);
    }
    @Autowired
    private TransactionRepository transactionRepository;

    // Fetch recent teller-related transactions (you can implement a more complex query if needed)
    public List<Transaction> getRecentTellerTransactions() {
        // This is a simplified query - modify as needed
        return transactionRepository.findTop10ByOrderByTransactionDateDesc();  // Example query
    }
  
   
    // New method to count the number of tellers
    public long getTellerCount() {
        return tellerRepository.count();
    }
    // Delete a teller by ID
    public void deleteTeller(Long id) {
        tellerRepository.deleteById(id);
    }
}
