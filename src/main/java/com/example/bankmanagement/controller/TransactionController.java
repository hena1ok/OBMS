package com.example.bankmanagement.controller;

import com.example.bankmanagement.model.Account;
import com.example.bankmanagement.model.Transaction;
import com.example.bankmanagement.model.User;
import com.example.bankmanagement.security.CustomUserDetails;
import com.example.bankmanagement.service.AccountService;
import com.example.bankmanagement.service.TransactionService;
import com.example.bankmanagement.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/transactions")
public class TransactionController {
    private final AccountService accountService;
    private final UserService userService;
    private final TransactionService transactionService;

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    public TransactionController(AccountService accountService, UserService userService, TransactionService transactionService) {
        this.accountService = accountService;
        this.userService = userService;
        this.transactionService = transactionService;
    }

    private Long getLoggedInUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails loggedInUser = (CustomUserDetails) authentication.getPrincipal();
        return loggedInUser.getId();
    }

    @GetMapping("/create")
    public String showCreateTransactionForm(@RequestParam(required = false) String searchTerm, Model model) {
        populateTransactionFormModel(model, searchTerm);
        return "transaction/create";
    }

    @PostMapping("/create")
    public String createTransaction(@RequestParam BigDecimal amount,
                                    @RequestParam Long accountId,
                                    @RequestParam(required = false) Long destinationAccountId,
                                    @RequestParam String transactionType,
                                    @RequestParam(required = false) String description,
                                    @RequestParam(required = false) Long tellerId,
                                    RedirectAttributes redirectAttributes) {
        logger.info("Creating transaction with parameters: amount={}, accountId={}, destinationAccountId={}, transactionType={}, description={}, tellerId={}",
                amount, accountId, destinationAccountId, transactionType, description, tellerId);

        String validationError = validateTransactionInput(amount, accountId, destinationAccountId, transactionType, description);
        if (validationError != null) {
            redirectAttributes.addFlashAttribute("error", validationError);
            return "redirect:/transactions/create";
        }

        try {
            Long userId = getLoggedInUserId();
            User loggedInUser = userService.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            Account sourceAccount = accountService.findById(accountId)
                    .orElseThrow(() -> new IllegalArgumentException("Source account not found"));

            Account destAccount = null;
            if ("TRANSFER".equalsIgnoreCase(transactionType)) {
                destAccount = accountService.findById(destinationAccountId)
                        .orElseThrow(() -> new IllegalArgumentException("Destination account not found"));
            }

            updateAccountBalances(transactionType, amount, sourceAccount, destAccount);
            accountService.save(sourceAccount);
            if (destAccount != null) {
                accountService.save(destAccount);
            }

            Transaction transaction = transactionService.createTransaction(amount, sourceAccount.getId(),
                    (destAccount != null ? destAccount.getId() : sourceAccount.getId()),
                    Transaction.TransactionType.valueOf(transactionType.toUpperCase()), description, loggedInUser, tellerId);

            redirectAttributes.addFlashAttribute("message", "Transaction created successfully!");
            return "redirect:/transactions/create";
        } catch (IllegalArgumentException e) {
            logger.error("Transaction creation failed: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Invalid transaction data: " + e.getMessage());
            return "redirect:/transactions/create";
        } catch (Exception e) {
            logger.error("An unexpected error occurred while creating transaction: ", e);
            redirectAttributes.addFlashAttribute("error", "An unexpected error occurred: " + e.getMessage());
            return "redirect:/transactions/create";
        }
    }

    private String validateTransactionInput(BigDecimal amount, Long accountId, Long destinationAccountId,
                                            String transactionType, String description) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return "Amount must be greater than zero.";
        }

        if (transactionType == null || description == null || description.trim().isEmpty()) {
            return "Transaction type and description are required.";
        }

        switch (transactionType.toUpperCase()) {
            case "DEPOSIT":
            case "WITHDRAWAL":
                if (accountId == null) {
                    return "Account ID is required for deposits and withdrawals.";
                }
                break;
            case "TRANSFER":
                if (accountId == null || destinationAccountId == null) {
                    return "Both source and destination account IDs are required for transfers.";
                }
                if (accountId.equals(destinationAccountId)) {
                    return "Source and destination accounts cannot be the same for transfers.";
                }
                break;
            default:
                return "Invalid transaction type.";
        }

        return null; // No validation errors
    }

    private void updateAccountBalances(String transactionType, BigDecimal amount, Account sourceAccount, Account destAccount) {
        switch (transactionType.toUpperCase()) {
            case "DEPOSIT":
                sourceAccount.setBalance(sourceAccount.getBalance().add(amount));
                break;
            case "WITHDRAWAL":
                if (sourceAccount.getBalance().compareTo(amount) < 0) {
                    throw new IllegalArgumentException("Insufficient balance for withdrawal");
                }
                sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
                break;
            case "TRANSFER":
                if (sourceAccount.getBalance().compareTo(amount) < 0) {
                    throw new IllegalArgumentException("Insufficient balance for transfer");
                }
                sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
                destAccount.setBalance(destAccount.getBalance().add(amount));
                break;
            default:
                throw new IllegalArgumentException("Invalid transaction type");
        }
    }

    private void populateTransactionFormModel(Model model, String searchTerm) {
        Long userId = getLoggedInUserId();
        User loggedInUser = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Account> userAccounts;
        
        // Check user roles and retrieve accounts accordingly
        if (loggedInUser.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN") || role.getName().equals("ROLE_TELLER"))) {
            // If the user is Admin or Teller, fetch all accounts
            userAccounts = (searchTerm != null && !searchTerm.isEmpty())
                    ? accountService.searchAccounts(searchTerm) // Use search functionality
                    : accountService.getAllAccounts();
        } else {
            // For other users, fetch only their accounts
            userAccounts = accountService.findAccountsByUserId(userId);
        }
        
        model.addAttribute("userAccounts", userAccounts);
        model.addAttribute("transaction", new Transaction()); // Empty transaction object for form binding
        model.addAttribute("searchTerm", searchTerm); // Include search term in model
    }

    @GetMapping("/account/{accountId}")
    public String getTransactionsByAccount(@PathVariable Long accountId, Model model) {
        List<Transaction> transactions = transactionService.getTransactionsByAccount(accountId);
        model.addAttribute("transactions", transactions);
        return "transaction/accountTransactions"; // Return the account transactions template
    }

    @GetMapping("/user")
    public String getTransactionsByUser(Model model) {
        Long userId = getLoggedInUserId();
        List<Transaction> transactions = transactionService.getTransactionsByUser(userId);
        model.addAttribute("transactions", transactions);
        return "transaction/userTransactions"; // Return the user transactions template
    }

    @GetMapping("/teller")
    public String getTransactionsByTeller(Model model) {
        Long userId = getLoggedInUserId();
        List<Transaction> transactions = transactionService.getTransactionsByUser(userId);
        model.addAttribute("transactions", transactions);
        return "transaction/userTransactions"; // Return the user transactions template
    }

    @GetMapping("/{id}")
    public String getTransactionById(@PathVariable Long id, Model model) {
        Optional<Transaction> transaction = transactionService.getTransactionById(id);
        if (transaction.isPresent()) {
            model.addAttribute("transaction", transaction.get());
            return "transaction/viewTransaction"; // Return the view transaction template
        } else {
            model.addAttribute("error", "Transaction not found");
            return "transaction/error"; // Redirect to the error page if not found
        }
    }
}
