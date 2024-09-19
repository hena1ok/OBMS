package com.example.bankmanagement.service;

import com.example.bankmanagement.model.Branch;
import com.example.bankmanagement.repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BranchService {

    @Autowired
    private BranchRepository branchRepository;

    public List<Branch> findAll() {
        return branchRepository.findAll();
    }

    public Optional<Branch> findById(Long id) {
        return branchRepository.findById(id);
    }

    public Branch save(Branch branch) {
        return branchRepository.save(branch);
    }

    public void deleteById(Long id) {
        branchRepository.deleteById(id);
    }
}
