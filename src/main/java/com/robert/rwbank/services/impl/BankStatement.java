package com.robert.rwbank.services.impl;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Component;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.robert.rwbank.entity.Transaction;
import com.robert.rwbank.entity.User;
import com.robert.rwbank.repository.TransactionRepository;
import com.robert.rwbank.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@AllArgsConstructor
@Slf4j
public class BankStatement {

   private TransactionRepository transactionRepository;

   private UserRepository userRepository;

   public static final String FILE = "/Users/robertperemobowei/Documents/Statements/MyStatements.pdf";

   /**
    * retrive list of transactions within a date range given an account number
    * genarate a pdf file of transactions
    * send the file via emaol
    * 
    * @throws DocumentException
    * @throws FileNotFoundException
    */

   public List<Transaction> generateStatement(String accountNumber, String startDate, String endDate)
         throws DocumentException, FileNotFoundException {
      LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
      LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
      User user = userRepository.findByAccountNumber(accountNumber);

      String customerName = user.getFirstName() + " " + user.getLastName() + " " + user.getOtherName();
      List<Transaction> transactionList = transactionRepository.findAll().stream()
            .filter(transaction -> transaction.getAccountNumber().equals(accountNumber))
            .filter(transaction -> transaction.getCreatedAt().isEqual(start))
            .filter(transaction -> transaction.getCreatedAt().isEqual(end)).toList();

      Document document = new Document(PageSize.A4, 20, 20, 30, 30);
      log.info("setting size of document");
      OutputStream outputStream = new FileOutputStream(FILE);
      PdfWriter.getInstance(document, outputStream);
      document.open();

      PdfPTable bankInfoTable = new PdfPTable(1);
      PdfPCell bankName = new PdfPCell(new Phrase("R-W Bank"));
      bankName.setBorder(0);
      bankName.setBackgroundColor(BaseColor.BLUE);
      bankName.setPadding(20f);

      PdfPCell bankAddress = new PdfPCell(new Phrase("72, Some Address, Bayelsa Nigeria"));
      bankAddress.setBorder(0);
      bankInfoTable.addCell(bankName);
      bankInfoTable.addCell(bankAddress);

      PdfPTable statementInfo = new PdfPTable(2);
      PdfPCell customerInfo = new PdfPCell(new Phrase("Start Date: " + start));
      customerInfo.setBorder(0);
      PdfPCell statement = new PdfPCell(new Phrase("STATEMENT OF ACCOUNT"));
      statement.setBorder(0);
      PdfPCell stopDate = new PdfPCell(new Phrase("End Date: " + end));
      stopDate.setBorder(0);
      PdfPCell name = new PdfPCell(new Phrase("Customer Name: " + customerName));
      name.setBorder(0);

      PdfPCell space = new PdfPCell();
      space.setBorder(0);
      PdfPCell address = new PdfPCell(new Phrase("Customer Address " + user.getAddress()));
      address.setBorder(0);

      PdfPTable transactionTable = new PdfPTable(5);
      PdfPCell date = new PdfPCell(new Phrase("DATE"));
      date.setBackgroundColor(BaseColor.BLUE);
      date.setBorder(0);
      PdfPCell transactionType = new PdfPCell(new Phrase("TRANSACTION TYPE"));
      transactionType.setBackgroundColor(BaseColor.BLUE);
      transactionType.setBorder(0);
      PdfPCell amount = new PdfPCell(new Phrase("AMOUNT"));
      amount.setBackgroundColor(BaseColor.BLUE);
      amount.setBorder(0);
      PdfPCell description = new PdfPCell(new Phrase("DESCRIPTION"));
      description.setBackgroundColor(BaseColor.BLUE);
      description.setBorder(0);
      PdfPCell status = new PdfPCell(new Phrase("STATUS"));
      status.setBackgroundColor(BaseColor.BLUE);
      status.setBorder(0);

      transactionTable.addCell(date);
      transactionTable.addCell(transactionType);
      transactionTable.addCell(amount);
      transactionTable.addCell(description);
      transactionTable.addCell(status);

      transactionList.forEach(transaction -> {
         transactionTable.addCell(new Phrase(transaction.getCreatedAt().toString()));
         transactionTable.addCell(new Phrase(transaction.getTransactionType().toString()));
         transactionTable.addCell(new Phrase(transaction.getAmount().toString()));
         transactionTable.addCell(new Phrase(transaction.getDescription().toString()));
         transactionTable.addCell(new Phrase(transaction.getStatus().toString()));
      });
      
      statementInfo.addCell(customerInfo);
      statementInfo.addCell(statement);
      statementInfo.addCell(stopDate);
      statementInfo.addCell(name);
      statementInfo.addCell(space);
      statementInfo.addCell(address);

      document.add(bankInfoTable);
      document.add(statementInfo);
      document.add(transactionTable);

      document.close();

      return transactionList;
   }

}

