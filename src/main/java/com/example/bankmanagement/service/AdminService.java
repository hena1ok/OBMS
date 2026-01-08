//package com.example.bankmanagement.service;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.example.bankmanagement.model.Transaction;
//import com.example.bankmanagement.model.User;
//import com.example.bankmanagement.repository.TransactionRepository;
//import com.example.bankmanagement.repository.UserRepository;
//
//@Service
//public class AdminService {
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private TransactionRepository transactionRepository;
//
//    public List<User> getRecentUserRegistrations() {
//        return userRepository.findTop5ByOrderByRegistrationDateDesc();
//    }
//
//    public List<Transaction> getRecentTransactions() {
//        return transactionRepository.findTop5ByOrderByTransactionDateDesc();
//    }
//}
//
