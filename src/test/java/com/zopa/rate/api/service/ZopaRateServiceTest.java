package com.zopa.rate.api.service;

import com.zopa.rate.api.domain.Quote;
import com.zopa.rate.api.exceptions.LoanAmountException;
import com.zopa.rate.api.exceptions.ParserException;
import com.zopa.rate.api.exceptions.PositiveValueException;
import org.junit.Test;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.Assert.*;

public class ZopaRateServiceTest {

    private final ZopaRateService service;

    public ZopaRateServiceTest() throws PositiveValueException {
        service = new ZopaRateService(1000, 15000, 100);
    }

    @Test
    public void testCalculateQuote() throws ParserException, PositiveValueException, LoanAmountException {
        String loanAmount = "1000";
        String fileName = "market.csv";
        String filePath = getPathFromFile(fileName);
        Quote expectedQuote = new Quote(
                BigDecimal.valueOf(1000).setScale(2, RoundingMode.HALF_EVEN),
                BigDecimal.valueOf(0.07).setScale(2, RoundingMode.HALF_EVEN),
                BigDecimal.valueOf(30.78).setScale(2, RoundingMode.HALF_EVEN),
                BigDecimal.valueOf(1108.10).setScale(2, RoundingMode.HALF_EVEN));

        Quote actualQuote = service.findQuote(filePath, loanAmount);
        assertEquals(expectedQuote, actualQuote);
        System.out.println(actualQuote);
    }

    @Test(expected = LoanAmountException.class)
    public void testLoanAmountLowerBound() throws ParserException, PositiveValueException, LoanAmountException {
        String loanAmount = "999";
        String fileName = "market.csv";
        String filePath = getPathFromFile(fileName);
        service.findQuote(filePath, loanAmount);
    }

    @Test(expected = LoanAmountException.class)
    public void testLoanAmountHigherBound() throws ParserException, PositiveValueException, LoanAmountException {
        String loanAmount = "15000.01";
        String fileName = "market.csv";
        String filePath = getPathFromFile(fileName);
        service.findQuote(filePath, loanAmount);
    }

    @Test(expected = LoanAmountException.class)
    public void testLoanAmountIncrement() throws ParserException, PositiveValueException, LoanAmountException {
        String loanAmount = "14501.00";
        String fileName = "market.csv";
        String filePath = getPathFromFile(fileName);
        service.findQuote(filePath, loanAmount);
    }

    @Test(expected = ParserException.class)
    public void testParserError() throws ParserException, PositiveValueException, LoanAmountException {
        String loanAmount = "1000";
        String fileName = "market-error.csv";
        String filePath = getPathFromFile(fileName);
        service.findQuote(filePath, loanAmount);
    }

    @Test
    public void testNoQuoteForLoan() throws ParserException, PositiveValueException, LoanAmountException {
        String loanAmount = "1000";
        String fileName = "market-not-enough.csv";
        String filePath = getPathFromFile(fileName);
        Quote quote = service.findQuote(filePath, loanAmount);
        assertNull(quote);
    }

    private String getPathFromFile(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(fileName).getFile());
        return file.getPath();
    }
}