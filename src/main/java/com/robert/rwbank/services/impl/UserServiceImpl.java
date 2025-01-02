package com.robert.rwbank.services.impl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.robert.rwbank.dto.AccountInfo;
import com.robert.rwbank.dto.BankResponse;
import com.robert.rwbank.dto.CreditDebitRequest;
import com.robert.rwbank.dto.EmailDetails;
import com.robert.rwbank.dto.EnquiryRequest;
import com.robert.rwbank.dto.TransactionDto;
import com.robert.rwbank.dto.TransferRequest;
import com.robert.rwbank.dto.UserRequest;
import com.robert.rwbank.entity.User;
import com.robert.rwbank.repository.UserRepository;
import com.robert.rwbank.services.EmailService;
import com.robert.rwbank.services.TransactionService;
import com.robert.rwbank.services.UserService;
import com.robert.rwbank.utils.AccountUtils;

@Service
public class UserServiceImpl implements UserService {

        @Autowired
        UserRepository userRepository;

        @Autowired
        EmailService emailService;

        @Autowired
        TransactionService transactionService;

        @Override
        public BankResponse createAccount(UserRequest userRequest) {
                /*
                 * Creating an account - Saving a new user into the db
                 * Using the builder pattern
                 * 
                 * Check if user already has an account
                 */
                if (userRepository.existsByEmail(userRequest.getEmail())) {
                        return BankResponse.builder()
                                        .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                                        .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                                        .accountInfo(null)
                                        .build();
                }

                User newUser = User.builder()
                                .firstName(userRequest.getFirstName())
                                .lastName(userRequest.getLastName())
                                .otherName(userRequest.getOtherName())
                                .gender(userRequest.getGender())
                                .address(userRequest.getAddress())
                                .stateOfOrigin(userRequest.getStateOfOrigin())
                                .accountNumber(AccountUtils.generateAccountNumber())
                                .accountBalance(BigDecimal.ZERO)
                                .email(userRequest.getEmail())
                                .phoneNumber(userRequest.getPhoneNumber())
                                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                                .status("ACTIVE")
                                .build();

                User savedUser = userRepository.save(newUser);
                // Send email Alert

                EmailDetails emailDetails = EmailDetails.builder()
                                .recipient(savedUser.getEmail())
                                .subject("ACCOUNT CREATION")
                                .messageBody("Congratulations your account has been successfully Created. \n Your Account Details: \n"
                                                +
                                                "Account Name: " + savedUser.getFirstName() + " "
                                                + savedUser.getLastName() + " "
                                                + savedUser.getOtherName() + "\n Account Number: "
                                                + savedUser.getAccountNumber())
                                .build();
                emailService.sendEmailAlert(emailDetails);
                return BankResponse.builder()
                                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                                .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                                .accountInfo(AccountInfo.builder()
                                                .accountBalance(savedUser.getAccountBalance())
                                                .accountNumber(savedUser.getAccountNumber())
                                                .accountName(savedUser.getFirstName() + " " + savedUser.getLastName()
                                                                + " "
                                                                + savedUser.getOtherName())
                                                .build())
                                .build();

        }

        @Override
        public BankResponse balanceEnquiry(EnquiryRequest request) {
                boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
                if (!isAccountExist) {
                        return BankResponse.builder()
                                        .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                                        .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                                        .accountInfo(null)
                                        .build();
                }
                User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());

                return BankResponse.builder()
                                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                                .responseMessage(AccountUtils.ACCOUNT_FOUND_MESSAGE)
                                .accountInfo(AccountInfo.builder()
                                                .accountName(
                                                                foundUser.getFirstName() + " "
                                                                                + foundUser.getOtherName() + " "
                                                                                + foundUser.getLastName())
                                                .accountBalance(foundUser.getAccountBalance())
                                                .accountNumber(foundUser.getAccountNumber())
                                                .build())
                                .build();
        }

        @Override
        public String nameEnquiry(EnquiryRequest request) {
                boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
                if (!isAccountExist) {
                        return AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE;
                }
                User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
                return foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName();
        }

        @Override
        public BankResponse creditAccount(CreditDebitRequest request) {
                boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
                if (!isAccountExist) {
                        return BankResponse.builder()
                                        .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                                        .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                                        .accountInfo(null)
                                        .build();
                }
                User userToCredit = userRepository.findByAccountNumber(request.getAccountNumber());
                userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));

                userRepository.save(userToCredit);

                TransactionDto transactionDto = TransactionDto.builder()
                                .accountNumber(userToCredit.getAccountNumber())
                                .transactionType("CREDIT")
                                .amount(request.getAmount())
                                .description(request.getDescription())
                                .build();

                transactionService.SaveTransaction(transactionDto);
                return BankResponse.builder()
                                .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS)
                                .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                                .accountInfo(AccountInfo.builder()
                                                .accountName(userToCredit.getFirstName() + " "
                                                                + userToCredit.getLastName() + " "
                                                                + userToCredit.getOtherName())
                                                .accountBalance(userToCredit.getAccountBalance())
                                                .accountNumber(userToCredit.getAccountNumber())
                                                .build())
                                .build();
        }

        @Override
        public BankResponse debitAccount(CreditDebitRequest request) {
                boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
                if (!isAccountExist) {
                        return BankResponse.builder()
                                        .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                                        .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                                        .accountInfo(null)
                                        .build();
                }
                User userToDebit = userRepository.findByAccountNumber(request.getAccountNumber());

                double availableBalance = Double.parseDouble(userToDebit.getAccountBalance().toString());
                double debitAmount = Double.parseDouble(request.getAmount().toString());

                if (availableBalance < debitAmount || debitAmount < 0) {
                        return BankResponse.builder()
                                        .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                                        .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                                        .accountInfo(null)
                                        .build();
                } else {

                        userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));
                        userRepository.save(userToDebit);
                        TransactionDto transactionDto = TransactionDto.builder()
                                        .accountNumber(userToDebit.getAccountNumber())
                                        .transactionType("DEBIT")
                                        .amount(request.getAmount())
                                        .description(request.getDescription())
                                        .build();

                        transactionService.SaveTransaction(transactionDto);
                        return BankResponse.builder()
                                        .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS)
                                        .responseMessage(AccountUtils.ACCOUNT_DEBITED_SUCCESS_MESSAGE)
                                        .accountInfo(AccountInfo.builder()
                                                        .accountBalance(userToDebit.getAccountBalance())
                                                        .accountName(userToDebit.getFirstName() + " "
                                                                        + userToDebit.getLastName() + " "
                                                                        + userToDebit.getOtherName())
                                                        .accountNumber(userToDebit.getAccountNumber())
                                                        .build())
                                        .build();
                }

        }

        @Override
        public BankResponse transferFunds(TransferRequest request) {
                boolean isSenderAccountExist = userRepository.existsByAccountNumber(request.getSenderAccountNumber());
                if (!isSenderAccountExist) {
                        return BankResponse.builder()
                                        .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                                        .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)

                                        .accountInfo(null)
                                        .build();
                }

                boolean isReceiverAccountExist = userRepository
                                .existsByAccountNumber(request.getReceiverAccountNumber());
                if (!isReceiverAccountExist) {
                        return BankResponse.builder()
                                        .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                                        .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                                        .accountInfo(null)
                                        .build();
                }

                User userToDebit = userRepository.findByAccountNumber(request.getSenderAccountNumber());

                User userToCredit = userRepository.findByAccountNumber(request.getReceiverAccountNumber());

                double availableBalance = Double.parseDouble(userToDebit.getAccountBalance().toString());
                double debitAmount = Double.parseDouble(request.getAmount().toString());
                if (availableBalance < debitAmount || debitAmount < 0) {
                        return BankResponse.builder()   
                                        .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                                        .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                                        .accountInfo(null)
                                        .build();
                } else {
                        userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));
                        userRepository.save(userToDebit);
                        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));
                        userRepository.save(userToCredit);

                        EmailDetails debitAlert = EmailDetails.builder()
                                        .subject("Debit Alert")
                                        .recipient(userToDebit.getEmail())
                                        .messageBody("The sum of " + request.getAmount()
                                                        + " has been deducted from your account! Your current balance is "
                                                        + userToDebit.getAccountBalance())
                                        .build();

                        emailService.sendEmailAlert(debitAlert);

                        EmailDetails creditAlert = EmailDetails.builder()
                                        .subject("Credit Alert")
                                        .recipient(userToCredit.getEmail())
                                        .messageBody("The sum of " + request.getAmount()
                                                        + " has been added to your account! Your current balance is "
                                                        + userToCredit.getAccountBalance())
                                        .build();

                        emailService.sendEmailAlert(creditAlert);

                        TransactionDto transactionDtoDebit = TransactionDto.builder()
                                        .accountNumber(userToDebit.getAccountNumber())
                                        .transactionType("DEBIT")
                                        .amount(request.getAmount())
                                        .description(request.getDescription())
                                        .build();

                        TransactionDto transactionDto = TransactionDto.builder()
                                        .accountNumber(userToCredit.getAccountNumber())
                                        .transactionType("CREDIT")
                                        .amount(request.getAmount())
                                        .description(request.getDescription())
                                        .build();

                        transactionService.SaveTransaction(transactionDto);
                        transactionService.SaveTransaction(transactionDtoDebit);
                        return BankResponse.builder()
                                        .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS)
                                        .responseMessage(AccountUtils.ACCOUNT_DEBITED_SUCCESS_MESSAGE)
                                        .accountInfo(AccountInfo.builder()
                                                        .accountBalance(userToDebit.getAccountBalance())
                                                        .accountName(userToDebit.getFirstName() + " "
                                                                        + userToDebit.getLastName() + " "
                                                                        + userToDebit.getOtherName())
                                                        .accountNumber(userToDebit.getAccountNumber())
                                                        .build())
                                        .build();
                }

        }
}
