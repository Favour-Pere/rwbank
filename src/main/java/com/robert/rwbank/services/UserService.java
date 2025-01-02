package com.robert.rwbank.services;

import com.robert.rwbank.dto.BankResponse;
import com.robert.rwbank.dto.CreditDebitRequest;
import com.robert.rwbank.dto.EnquiryRequest;
import com.robert.rwbank.dto.TransferRequest;
import com.robert.rwbank.dto.UserRequest;

public interface UserService {
   BankResponse createAccount(UserRequest userRequest);

   BankResponse balanceEnquiry(EnquiryRequest request);

   String nameEnquiry(EnquiryRequest request);

   BankResponse creditAccount(CreditDebitRequest request);

   BankResponse debitAccount(CreditDebitRequest request);
   
   BankResponse transferFunds(TransferRequest request);
}
