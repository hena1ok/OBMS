package com.example.bankmanagement.service;

import org.springframework.stereotype.Service;

import com.example.bankmanagement.model.LoanType;

import java.util.Arrays;
import java.util.List;

@Service
public class LoanTypeService {

    public List<LoanType> getAllLoanTypes() {
        return Arrays.asList(LoanType.values());
    }
}
