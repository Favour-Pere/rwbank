package com.robert.rwbank.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.robert.rwbank.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    
}
