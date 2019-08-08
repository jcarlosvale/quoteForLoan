package com.zopa.rate.api.service;

import com.zopa.rate.api.domain.Lender;
import com.zopa.rate.api.domain.Quote;
import com.zopa.rate.api.exceptions.LoanAmountException;
import com.zopa.rate.api.exceptions.ParserException;
import com.zopa.rate.api.exceptions.PositiveValueException;
import com.zopa.rate.api.parser.Parser;
import com.zopa.rate.api.util.Calculator;
import lombok.NonNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.zopa.rate.api.validator.Validator.validatePositiveNumber;

public class ZopaRateService {

    private final double minValue;
    private final double maxValue;
    private final double increment;

    /**
     * The allowed limits to lend
     * @param minValue
     * @param maxValue
     * @param increment
     * @throws PositiveValueException
     */
    public ZopaRateService(double minValue, double maxValue, double increment) throws PositiveValueException {
        this.minValue = validatePositiveNumber(minValue);
        this.maxValue = validatePositiveNumber(maxValue);
        this.increment = validatePositiveNumber(increment);
    }

    /**
     * Main method to find a Quote:
     * 1 - read the CSV and load in a List of Lender
     * 2 - sort the List of Lender by rates ASC
     * 3 - select the lenders and insert their information in another List and values of each one
     * 4 - calculate the Quote
     *
     * If the Quote is not possible to calculate or to do a loan, return null
     * @param filePath
     * @param loanAmountStr
     * @return
     * @throws NumberFormatException
     * @throws ParserException
     * @throws PositiveValueException
     * @throws LoanAmountException
     */
    public Quote findQuote(@NonNull String filePath, @NonNull String loanAmountStr)
            throws NumberFormatException, ParserException, PositiveValueException, LoanAmountException {
        Parser parser = new Parser();
        double loanAmount = validatePositiveNumber(Double.parseDouble(loanAmountStr));

        verifyLoanAmount(loanAmount);

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

    /**
     * Verify the rule: A quote may be requested in any £X.XX increment between £MIN and £MAX inclusive.
     * @param loanAmount
     * @throws LoanAmountException
     */
    private void verifyLoanAmount(double loanAmount) throws LoanAmountException {
        if (loanAmount > maxValue || loanAmount < minValue) {
            throw new LoanAmountException(String.format("Loan value %f out of range [%f - %f]", loanAmount, minValue, maxValue));
        }

        double reminder = loanAmount % minValue;
        if (reminder > 0) {
            if (reminder % increment != 0) {
                throw new LoanAmountException(String.format("Loan value %f is not a valid value" +
                        " [max = %f, min = %f, increment = %f]", loanAmount, minValue, maxValue, increment));
            }
        }
    }

    /**
     * Calculate the Quote of a group of lenders
     * @param listLenders
     * @param loanAmount
     * @return
     */
    private Quote calculateQuote(List<Lender> listLenders, double loanAmount) {
        Calculator calculator = new Calculator();
        double monthlyRepayment = 0;

        int MAX_PAYMENTS = 36;
        for(Lender lender : listLenders) {
            double equivalentRate = calculator.calculateEquivalentRate(lender.getRate(), 1.0/12);
            monthlyRepayment += calculator
                    .calculatePaymentAmount(lender.getAvailableAmount(), equivalentRate, MAX_PAYMENTS);
        }

        double rate = calculator.calculateIRR(MAX_PAYMENTS, monthlyRepayment, -loanAmount, true);

        double repayment = MAX_PAYMENTS * monthlyRepayment;

        return new Quote(
                BigDecimal.valueOf(loanAmount).setScale(2, RoundingMode.HALF_EVEN),
                BigDecimal.valueOf(rate).setScale(2, RoundingMode.HALF_EVEN),
                BigDecimal.valueOf(monthlyRepayment).setScale(2, RoundingMode.HALF_EVEN),
                BigDecimal.valueOf(repayment).setScale(2, RoundingMode.HALF_EVEN));
    }
}