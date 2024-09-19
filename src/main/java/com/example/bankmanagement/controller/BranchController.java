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
@RequestMapping("/branches")
public class BranchController {

    @Autowired
    private BranchService branchService;

    @GetMapping
    public String listBranches(Model model) {
        model.addAttribute("branches", branchService.findAll());
        return "branch/list";
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

    @GetMapping("/new")
    public String newBranchForm(Model model) {
        model.addAttribute("branch", new Branch());
        return "branch/form";
    }

    @PostMapping
    public String saveBranch(@ModelAttribute @Valid Branch branch, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "branch/form";
        }
        branchService.save(branch);
        return "redirect:/branches";
    }

    @GetMapping("/edit/{id}")
    public String editBranch(@PathVariable("id") Long id, Model model) {
        Optional<Branch> branch = branchService.findById(id);
        if (branch.isPresent()) {
            model.addAttribute("branch", branch.get());
            return "branch/form";
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
