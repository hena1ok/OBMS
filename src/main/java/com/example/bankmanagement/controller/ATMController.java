package com.example.bankmanagement.controller;

import com.example.bankmanagement.service.ATMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/atm")
public class ATMController {

    @Autowired
    private ATMService atmService;

    @GetMapping("/balance/{accountNumber}")
    public double getBalance(@PathVariable String accountNumber) {
        // Call ATM service to get account balance
        return atmService.getBalance(accountNumber);
    }

    @PostMapping("/withdraw")
    public String withdraw(@RequestParam String accountNumber, @RequestParam double amount) {
        // Call ATM service to withdraw
        if (atmService.withdraw(accountNumber, amount)) {
            return "Withdrawal successful.";
        } else {
            return "Withdrawal failed. Insufficient funds.";
        }
    }

    @PostMapping("/deposit")
    public String deposit(@RequestParam String accountNumber, @RequestParam double amount) {
        // Call ATM service to deposit
        if (atmService.deposit(accountNumber, amount)) {
            return "Deposit successful.";
        } else {
            return "Deposit failed.";
        }
    }
    
}