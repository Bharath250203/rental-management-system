package com.rental.service;

import com.rental.model.Property;
import com.rental.model.Transaction;
import com.rental.model.User;
import com.rental.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private PropertyService propertyService;
    
    @Autowired
    private UserService userService;
    
    public Transaction createTransaction(String propertyId, Date startDate, Date endDate) {
        Property property = propertyService.getPropertyById(propertyId);
        User currentUser = userService.getCurrentUser();
        
        if (property.getStatus() != Property.PropertyStatus.AVAILABLE) {
            throw new RuntimeException("Property is not available");
        }
        
        Transaction transaction = new Transaction();
        transaction.setPropertyId(propertyId);
        transaction.setTenantId(currentUser.getId());
        transaction.setOwnerId(property.getOwnerId());
        transaction.setAmount(property.getPrice());
        transaction.setStartDate(startDate);
        transaction.setEndDate(endDate);
        transaction.setStatus(Transaction.TransactionStatus.PENDING);
        transaction.setCreatedAt(new Date());
        transaction.setUpdatedAt(new Date());
        
        return transactionRepository.save(transaction);
    }
    
    public Transaction approveTransaction(String id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        User currentUser = userService.getCurrentUser();
        if (!transaction.getOwnerId().equals(currentUser.getId()) && !currentUser.getRole().equals("ADMIN")) {
            throw new RuntimeException("Unauthorized to approve this transaction");
        }
        
        transaction.setStatus(Transaction.TransactionStatus.APPROVED);
        transaction.setUpdatedAt(new Date());
        
        // Update property status
        propertyService.updatePropertyStatus(transaction.getPropertyId(), Property.PropertyStatus.RENTED);
        
        return transactionRepository.save(transaction);
    }
    
    public Page<Transaction> getUserTransactions(Pageable pageable) {
        User currentUser = userService.getCurrentUser();
        return transactionRepository.findByTenantId(currentUser.getId(), pageable);
    }
    
    public Page<Transaction> getOwnerTransactions(Pageable pageable) {
        User currentUser = userService.getCurrentUser();
        return transactionRepository.findByOwnerId(currentUser.getId(), pageable);
    }
}

