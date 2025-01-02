package com.robert.rwbank.controller;

import java.io.FileNotFoundException;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.text.DocumentException;
import com.robert.rwbank.entity.Transaction;
import com.robert.rwbank.services.impl.BankStatement;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/bankstatement")
@AllArgsConstructor
public class TransactionController {

    private BankStatement bankStatement;

    @GetMapping
    public List<Transaction> genarateBankStatement(@RequestParam String accountNumber, @RequestParam String startDate,
            @RequestParam String endDate) throws FileNotFoundException, DocumentException {
        return bankStatement.generateStatement(accountNumber, startDate, endDate);
    }
    
}
