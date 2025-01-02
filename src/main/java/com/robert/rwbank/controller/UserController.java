package com.robert.rwbank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.robert.rwbank.dto.BankResponse;
import com.robert.rwbank.dto.CreditDebitRequest;
import com.robert.rwbank.dto.UserRequest;
import com.robert.rwbank.services.UserService;
import com.robert.rwbank.dto.EnquiryRequest;
import com.robert.rwbank.dto.TransferRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User account Management APIs")
public class UserController {

    @Autowired
    UserService userService;

    @Operation(summary = "Create New User Account", description = "Given details of the user such as first name, last name, other name, gender, address, state of origin, email and phone number a user is created ")
    @ApiResponse(responseCode = "201", description = "HTTP status 201 created")
    @PostMapping
    public BankResponse createAccount(@RequestBody UserRequest userRequest) {
        return userService.createAccount(userRequest);
    }

    @Operation(summary = "Balance Enquiry", description = "Given an account number, Checks user balance")
    @ApiResponse(responseCode = "200", description = "HTTP status 200 SUCCESS")
    @GetMapping("/balanceEnquiry")
    public BankResponse banlanceEnquiry(@RequestBody EnquiryRequest enquiryRequest) {
        return userService.balanceEnquiry(enquiryRequest);
    }

    @Operation(summary = "Name Enquiry", description = "Given an account number, returns users name")
    @ApiResponse(responseCode = "200", description = "HTTP status 200 SUCCESS")
    @GetMapping("nameEnquiry")
    public String nameEnquiry(@RequestBody EnquiryRequest enquiryRequest) {
        return userService.nameEnquiry(enquiryRequest);
    }

    @Operation(summary = "Credit Operation", description = "Given an account number, amount and description perform a credit operation")
    @ApiResponse(responseCode = "201", description = "HTTP status 201 Created")
    @PostMapping("credit")
    public BankResponse creditAccount(@RequestBody CreditDebitRequest request) {
        return userService.creditAccount(request);
    }

    @Operation(summary = "Debit Operation", description = "Given an account number, amount and description performs a debit operation")
    @ApiResponse(responseCode = "201", description = "HTTP status 201 Created")
    @PostMapping("debit")
    public BankResponse debitAccount(@RequestBody CreditDebitRequest request) {
        return userService.debitAccount(request);
    }

    @Operation(summary = "Transfer Operation", description = "Given a senders account number, a receiver account number, amount and description")
    @ApiResponse(responseCode = "201", description = "HTTP status 201 Created")
    @PostMapping("transfer")
    public BankResponse transfer(@RequestBody TransferRequest request) {
        return userService.transferFunds(request);
    }

}
