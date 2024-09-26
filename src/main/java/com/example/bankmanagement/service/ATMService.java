package com.example.bankmanagement.service;

import com.example.bankmanagement.model.ATM;
import com.example.bankmanagement.repository.ATMRepository;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ATMService {

    private final ATMRepository atmRepository;

    @Autowired
    public ATMService(ATMRepository atmRepository) {
        this.atmRepository = atmRepository;
    }

    public List<ATM> getAllATMs() {
        return atmRepository.findAll();
    }

    public ATM getATMById(Long id) {
        return atmRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("ATM not found with id: " + id));
    }

    public void createATM(ATM atm) {
        atmRepository.save(atm);
    }

    public void updateATM(Long id, ATM updatedATM) {
        if (!atmRepository.existsById(id)) {
            throw new IllegalArgumentException("ATM not found with id: " + id);
        }
        updatedATM.setId(id);
        atmRepository.save(updatedATM);
    }

    public void deleteATM(Long id) {
        if (!atmRepository.existsById(id)) {
            throw new IllegalArgumentException("ATM not found with id: " + id);
        }
        atmRepository.deleteById(id);
    }

    public void createOrUpdateATM(@Valid ATM atm) {
        if (atm.getId() == null) {
            // Creating a new ATM
            atmRepository.save(atm);
            System.out.println("New ATM created at location: " + atm.getLocation());
        } else {
            // Updating an existing ATM
            Optional<ATM> existingATM = atmRepository.findById(atm.getId());
            if (existingATM.isPresent()) {
                ATM updatedATM = existingATM.get();
                updatedATM.setLocation(atm.getLocation());
                updatedATM.setCashAvailable(atm.getCashAvailable());
                atmRepository.save(updatedATM);
                System.out.println("ATM updated at location: " + atm.getLocation());
            } else {
                throw new IllegalArgumentException("ATM with ID " + atm.getId() + " not found.");
            }
        }
    }

}
