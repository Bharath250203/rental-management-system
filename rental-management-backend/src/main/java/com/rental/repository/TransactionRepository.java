package com.rental.repository;

import com.rental.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {
    Page<Transaction> findByTenantId(String tenantId, Pageable pageable);
    Page<Transaction> findByOwnerId(String ownerId, Pageable pageable);
    Page<Transaction> findByPropertyId(String propertyId, Pageable pageable);
}

