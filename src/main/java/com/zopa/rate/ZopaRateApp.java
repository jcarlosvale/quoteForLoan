package com.zopa.rate;

import com.zopa.rate.api.domain.Quote;
import com.zopa.rate.api.exceptions.ParserException;
import com.zopa.rate.api.service.ZopaRateService;

public class ZopaRateApp
{
    public static void main( String[] args ) throws ParserException {
        if (args.length != 2) {
            System.out.println("Missing parameters. Example: java -jar ZopaRateApp.jar <market_file_path> <loan_amount>\n");
            return;
        }

        String filePath = args[0];
        String loanAmountStr = args[1];
        ZopaRateService zopaRateService = new ZopaRateService();
        Quote quote = zopaRateService.findQuote(filePath, loanAmountStr);
        
        printInformation(quote);

    }

    private static void printInformation(Quote quote) {
        if (null != quote) {
            System.out.printf(
                    "Requested amount: £%f\n" +
                    "Annual Interest Rate: %f\n" +
                    "Monthly repayment: £%f\n" +
                    "Total repayment: £%f",
                    quote.getRequestedAmount().doubleValue(),
                    quote.getAnnualInterestRate().doubleValue(),
                    quote.getMonthlyRepayment().doubleValue(),
                    quote.getRepayment().doubleValue());
        } else {
            System.out.println("It is not possible to provide a quote.");
        }
    }
}
