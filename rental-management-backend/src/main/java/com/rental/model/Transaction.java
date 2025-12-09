package com.rental.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Date;

@Document(collection = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    private String id;
    
    @Indexed
    private String propertyId;
    
    @Indexed
    private String tenantId; // User ID
    
    @Indexed
    private String ownerId; // User ID
    
    private BigDecimal amount;
    private Date startDate;
    private Date endDate;
    private TransactionStatus status = TransactionStatus.PENDING;
    private Date createdAt;
    private Date updatedAt;
    
    public enum TransactionStatus {
        PENDING, APPROVED, REJECTED, COMPLETED, CANCELLED
    }
}

