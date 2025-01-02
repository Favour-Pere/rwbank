package com.robert.rwbank.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditDebitRequest {
    private String accountNumber;
    private BigDecimal amount;
    private String description;
}
