package com.example.bankmanagement.repository;

import com.example.bankmanagement.model.Teller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TellerRepository extends JpaRepository<Teller, Long> {
    // Add custom queries if needed
}
