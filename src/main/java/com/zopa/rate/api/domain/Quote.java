package com.zopa.rate.api.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class Quote {
    private final BigDecimal requestedAmount;
    private final BigDecimal annualInterestRate;
    private final BigDecimal monthlyRepayment;
    private final BigDecimal repayment;
}
