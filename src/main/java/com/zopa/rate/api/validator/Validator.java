package com.zopa.rate.api.validator;

import com.zopa.rate.api.exceptions.PositiveValueException;

public class Validator {

    /**
     * Validator method used by the APP
     * @param value to validate
     * @return the parameter value
     * @throws PositiveValueException in error case
     */
    public static double validatePositiveNumber(double value) throws PositiveValueException {
        if (value <0) {
            throw new PositiveValueException(String.format("The value must be positive [value= %f].",value));
        }
        return value;
    }
}
