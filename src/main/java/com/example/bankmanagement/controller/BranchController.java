package com.example.bankmanagement.controller;

import com.example.bankmanagement.model.Branch;
import com.example.bankmanagement.service.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/branch")
public class BranchController {

    @Autowired
    private BranchService branchService;

    @GetMapping("/list")
    public String listBranches(Model model) {
        model.addAttribute("branches", branchService.findAll());
        return "branch/branch_list";
    }

    @GetMapping("/{id}")
    public String getBranch(@PathVariable("id") Long id, Model model) {
        Optional<Branch> branch = branchService.findById(id);
        if (branch.isPresent()) {
            model.addAttribute("branch", branch.get());
            return "branch/view";
        } else {
            return "error/404"; // Handle not found error
        }
    }

    @GetMapping("/branch_form")
    public String newBranchForm(Model model) {
        model.addAttribute("branch", new Branch());
        return "branch/branch_form";
    }

    @PostMapping
    public String saveBranch(@ModelAttribute @Valid Branch branch, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "branch/branch_form";
        }
        branchService.save(branch);
        return "redirect:/branch/list";
    }

    @GetMapping("/edit/{id}")
    public String editBranch(@PathVariable("id") Long id, Model model) {
        Optional<Branch> branch = branchService.findById(id);
        if (branch.isPresent()) {
            model.addAttribute("branch", branch.get());
            return "branch/branch_form";
        } else {
            return "error/404"; // Handle not found error
        }
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteBranch(@PathVariable("id") Long id) {
        branchService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
