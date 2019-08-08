package com.zopa.rate.api.service;

import com.sun.istack.internal.NotNull;
import com.zopa.rate.api.domain.Lender;
import com.zopa.rate.api.domain.Quote;
import com.zopa.rate.api.exceptions.ParserException;
import com.zopa.rate.api.parser.Parser;
import com.zopa.rate.api.util.Calculator;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ZopaRateService {
    private final int MAX_PAYMENTS = 36;

    public Quote findQuote(@NotNull String filePath, @NonNull String loanAmountStr) throws NumberFormatException, ParserException {
        Parser parser = new Parser();
        double loanAmount = Double.valueOf(loanAmountStr);
        List<Lender> listLenders = parser.parseFile(filePath);
        listLenders.sort(Comparator.comparing(Lender::getRate));

        //getting the available values sorted by rates of lender and storing in the selected lender
        List<Lender> selectedLender = new ArrayList<>();
        double sumOfAmount = 0.0;
        for(Lender lender:listLenders) {
            double diffAmount = loanAmount - sumOfAmount;
            if (lender.getAvailableAmount() <= diffAmount) {
                selectedLender.add(lender);
                sumOfAmount += lender.getAvailableAmount();
            } else {
                selectedLender.add(new Lender(lender.getName(), lender.getRate(), diffAmount));
                sumOfAmount += diffAmount;
            }
            if (sumOfAmount == loanAmount) break;
        }

        if (sumOfAmount != loanAmount) return null;  //it´s not possible lend the money

        return calculateQuote(selectedLender, loanAmount);
    }

    private Quote calculateQuote(List<Lender> listLenders, Double loanAmount) {
        Calculator calculator = new Calculator();
        double monthlyRepayment = 0;

        for(Lender lender : listLenders) {
            monthlyRepayment += calculator
                    .calculatePaymentAmount(lender.getAvailableAmount(), lender.getRate(), MAX_PAYMENTS);
        }

        double rate = calculator.calculateIRR(MAX_PAYMENTS, monthlyRepayment, loanAmount, true);

        double repayment = MAX_PAYMENTS * monthlyRepayment;

        return new Quote(BigDecimal.valueOf(loanAmount), BigDecimal.valueOf(rate),
                BigDecimal.valueOf(monthlyRepayment),BigDecimal.valueOf(repayment));

    }
}