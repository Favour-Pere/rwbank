package com.robert.rwbank.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.robert.rwbank.dto.TransactionDto;
import com.robert.rwbank.entity.Transaction;
import com.robert.rwbank.repository.TransactionRepository;
import com.robert.rwbank.services.TransactionService;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    @Override
    public void SaveTransaction(TransactionDto transactionDto) {
        Transaction transaction = Transaction.builder()
        .transactionType(transactionDto.getTransactionType())
        .accountNumber(transactionDto.getAccountNumber())
        .amount(transactionDto.getAmount())
        .description(transactionDto.getDescription())
        .status("SUCCESS")
        .build();

    transactionRepository.save(transaction);
    System.out.println("Transaction Successfull");
    }

}
