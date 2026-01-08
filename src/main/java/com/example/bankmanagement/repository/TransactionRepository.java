package com.example.bankmanagement.repository;

import com.example.bankmanagement.model.Teller;
import com.example.bankmanagement.model.Transaction;
import com.example.bankmanagement.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // This method will allow you to find transactions by source account ID
    List<Transaction> findBySourceAccount_Id(Long sourceAccountId);

    // If you also need to find transactions by destination account ID, you can add:
    List<Transaction> findByDestinationAccount_Id(Long destinationAccountId);

    // If you need to find transactions for a specific user
    List<Transaction> findByUser_Id(Long userId);

    // You can also define methods for filtering transactions by status or type, if needed
    List<Transaction> findByStatus(Transaction.TransactionStatus status);
    
    List<Transaction> findByTransactionType(Transaction.TransactionType transactionType);

	List<Transaction> findByTeller(Teller teller);

	List<Transaction> findByUserId(Long userId);

	List<Transaction> findBySourceAccountIdOrDestinationAccountId(Long accountId, Long accountId2);

	List<Transaction> findTop10ByOrderByTransactionDateDesc();



    @Query("SELECT t FROM Transaction t WHERE t.teller.id = :tellerId")
    List<Transaction> findByTellerId(@Param("tellerId") Long tellerId);

	   long count();
	
}
