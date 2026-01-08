package com.example.bankmanagement.service;

import com.example.bankmanagement.model.ATM;
import com.example.bankmanagement.repository.ATMRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ATMService {

	 @Autowired
	    private ATMRepository atmRepository;


    // Create or update ATM
    @Transactional
    public ATM save(ATM atm) {
        return atmRepository.save(atm);
    }

    // Get all ATMs
    public List<ATM> findAll() {
        return atmRepository.findAll();
    }

    // Get ATM by ID
    public ATM findById(Long id) {
        return atmRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ATM not found with id: " + id));
    }
    public long getTotalATMs() {
        List<ATM> atms = atmRepository.findAll();
        return atms.size();
    }

    // Delete ATM by ID
    @Transactional
    public void deleteById(Long id) {
        atmRepository.deleteById(id);
    }
}
