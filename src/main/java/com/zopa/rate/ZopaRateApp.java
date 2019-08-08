package com.zopa.rate;

import com.zopa.rate.api.domain.Quote;
import com.zopa.rate.api.exceptions.LoanAmountException;
import com.zopa.rate.api.exceptions.ParserException;
import com.zopa.rate.api.exceptions.PositiveValueException;
import com.zopa.rate.api.service.ZopaRateService;

import java.math.RoundingMode;

public class ZopaRateApp
{
    public static void main( String[] args ) throws ParserException, PositiveValueException, LoanAmountException {
        if (args.length != 2) {
            System.out.println("Missing parameters. Example: java -jar ZopaRateApp.jar <market_file_path> <loan_amount>\n");
            return;
        }

        String filePath = args[0];
        String loanAmountStr = args[1];
        ZopaRateService zopaRateService = new ZopaRateService(1000, 15000, 100);
        Quote quote = zopaRateService.findQuote(filePath, loanAmountStr);
        
        printInformation(quote);

    }

    private static void printInformation(Quote quote) {
        if (null != quote) {
            System.out.printf(
                    "Requested amount: £%.2f\n" +
                    "Annual Interest Rate: %.1f %%\n" +
                    "Monthly repayment: £%.2f\n" +
                    "Total repayment: £%.2f \n",
                    quote.getRequestedAmount().setScale(2, RoundingMode.HALF_EVEN).doubleValue(),
                    quote.getAnnualInterestRate().setScale(2, RoundingMode.HALF_EVEN).doubleValue()*100,
                    quote.getMonthlyRepayment().setScale(2, RoundingMode.HALF_EVEN).doubleValue(),
                    quote.getRepayment().setScale(2, RoundingMode.HALF_EVEN).doubleValue());
        } else {
            System.out.println("It is not possible to provide a quote.");
        }
    }
}
