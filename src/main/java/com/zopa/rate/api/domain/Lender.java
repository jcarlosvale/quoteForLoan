package com.zopa.rate.api.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class Lender {
    private final String name;
    private final Double rate;
    private final Double availableAmount;
}
