package com.rental.controller;

import com.rental.model.Transaction;
import com.rental.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "http://localhost:3000")
@PreAuthorize("hasRole('USER')")
public class TransactionController {
    
    @Autowired
    private TransactionService transactionService;
    
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(
            @RequestParam String propertyId,
            @RequestParam Long startDate,
            @RequestParam Long endDate) {
        Transaction transaction = transactionService.createTransaction(
                propertyId, 
                new Date(startDate), 
                new Date(endDate)
        );
        return ResponseEntity.ok(transaction);
    }
    
    @PutMapping("/{id}/approve")
    public ResponseEntity<Transaction> approveTransaction(@PathVariable String id) {
        Transaction transaction = transactionService.approveTransaction(id);
        return ResponseEntity.ok(transaction);
    }
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getUserTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactions = transactionService.getUserTransactions(pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("transactions", transactions.getContent());
        response.put("currentPage", transactions.getNumber());
        response.put("totalItems", transactions.getTotalElements());
        response.put("totalPages", transactions.getTotalPages());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/owner")
    public ResponseEntity<Map<String, Object>> getOwnerTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactions = transactionService.getOwnerTransactions(pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("transactions", transactions.getContent());
        response.put("currentPage", transactions.getNumber());
        response.put("totalItems", transactions.getTotalElements());
        response.put("totalPages", transactions.getTotalPages());
        
        return ResponseEntity.ok(response);
    }
}

