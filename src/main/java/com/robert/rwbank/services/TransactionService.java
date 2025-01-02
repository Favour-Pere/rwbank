package com.robert.rwbank.services;

import com.robert.rwbank.dto.TransactionDto;

public interface TransactionService {
    void SaveTransaction(TransactionDto transactionDto);
}
