package com.zopa.rate.api.util;

import com.zopa.rate.api.exceptions.RateValueException;

public class Calculator {

    public double calculateEquivalentRate(double rate, double n) {
        return Math.pow(1+rate,n) - 1;
    }
    /**
     * Calculate the installment value
     * @param totalAmount
     * @param rate
     * @param nPeriods
     * @return
     */
    public double calculatePaymentAmount(double totalAmount, double rate, int nPeriods) {
        if (totalAmount <=0 || rate <= 0 || nPeriods <= 0) {
            throw new RateValueException(
                    String.format("The parameter value must be positive. " +
                            "[totalAmount= %f, rate= %f, nPeriods= %d]",totalAmount, rate, nPeriods));
        }
        double multiplicand = (Math.pow(1 + rate, nPeriods) * rate) / (Math.pow(1 + rate, nPeriods) - 1);
        return totalAmount * multiplicand;
    }

    /**
     * Internal Rate of Return calculation
     * source: https://www.calkoo.com/js/loanCalc.js
     * @param nPeriods
     * @param paymentValue
     * @param presentValue
     * @param isAnnualRate
     * @return Internal Rate of Return calculation
     */
    public double calculateIRR(int nPeriods, double paymentValue, double presentValue, boolean isAnnualRate) {
        double FINANCIAL_ACCURACY = 1.0e-9;
        int FINANCIAL_MAX_ITERATIONS = 100;
        double rate = 0.01d; //first approximation
        int i  = 0;
        double x0 = 0;
        double x1 = rate;
        double y, y0, y1, f;

        f = Math.exp(nPeriods * Math.log(1 + rate));

        y0 = presentValue + paymentValue * nPeriods;
        y1 = presentValue * f + paymentValue * (1 / rate) * (f - 1);

        // find root by secant method
        while ((Math.abs(y0 - y1) > FINANCIAL_ACCURACY) && (i < FINANCIAL_MAX_ITERATIONS)) {
            rate = (y1 * x0 - y0 * x1) / (y1 - y0);
            x0 = x1;
            x1 = rate;

            if (Math.abs(rate) < FINANCIAL_ACCURACY) {
                y = presentValue * (1 + nPeriods * rate) + paymentValue * (1) * nPeriods;
            } else {
                f = Math.exp(nPeriods * Math.log(1 + rate));
                y = presentValue * f + paymentValue * (1 / rate) * (f - 1);
            }

            y0 = y1;
            y1 = y;
            i++;
        }

        rate = isAnnualRate? rate*12 : rate;

        //if (rate < 0 || rate > 250) throw new RateValueException("IRR value unexpected: " + rate);

        return rate;
    }
}
