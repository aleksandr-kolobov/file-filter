package org.alexkolo.filefilter.service;

import org.alexkolo.filefilter.model.DataType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class Statistics {
    private final boolean isFull;

    private long integerCount = 0;
    private BigInteger integerMin;
    private BigInteger integerMax;
    private BigInteger integerSum;

    private long floatCount = 0;
    private BigDecimal floatMin;
    private BigDecimal floatMax;
    private BigDecimal floatSum;

    private long stringCount = 0;
    private int minLength;
    private int maxLength;

    public Statistics(boolean isFull) {
        this.isFull = isFull;
    }

    public void addValue(DataType type, String value) {
        switch (type) {
            case INTEGER -> integerCount++;
            case FLOAT -> floatCount++;
            case STRING -> stringCount++;
        }

        if (!isFull) {
            return;
        }

        switch (type) {
            case INTEGER -> {
                BigInteger tmp = new BigInteger(value);
                if (integerCount == 1L) {
                    integerMin = tmp;
                    integerMax = tmp;
                    integerSum = tmp;
                } else {
                    if (tmp.compareTo(integerMin) < 0) {
                        integerMin = tmp;
                    }
                    if (tmp.compareTo(integerMax) > 0) {
                        integerMax = tmp;
                    }
                    integerSum = integerSum.add(tmp);
                }
            }
            case FLOAT -> {
                BigDecimal tmp = new BigDecimal(value);
                if (floatCount == 1L) {
                    floatMin = tmp;
                    floatMax = tmp;
                    floatSum = tmp;
                } else {
                    if (tmp.compareTo(floatMin) < 0) {
                        floatMin = tmp;
                    }
                    if (tmp.compareTo(floatMax) > 0) {
                        floatMax = tmp;
                    }
                    floatSum = floatSum.add(tmp);
                }
            }
            case STRING -> {
                int length = value.length();
                if (stringCount == 1L) {
                    minLength = length;
                    maxLength = length;
                } else {
                    if (length < minLength) {
                        minLength = length;
                    }
                    if (length > maxLength) {
                        maxLength = length;
                    }
                }
            }
        }
    }

    public void printStatistics() {
        System.out.println("********* Count Statistics *********");
        System.out.println("Integers: " + integerCount);
        System.out.println("Floats: " + floatCount);
        System.out.println("Strings: " + stringCount);
        if (!isFull) {
            return;
        }
        System.out.println("********* Full Statistics *********");
        if (integerCount > 0) {
            System.out.println("Integers: ");
            System.out.println("  Min: " + integerMin);
            System.out.println("  Max: " + integerMax);
            System.out.println("  Sum: " + integerSum);
            System.out.println("  Average: " + integerSum.divide(BigInteger.valueOf(integerCount)));
        }
        if (floatCount > 0) {
            System.out.println("Floats: ");
            System.out.println("  Min: " + floatMin);
            System.out.println("  Max: " + floatMax);
            System.out.println("  Sum: " + floatSum.setScale(3, RoundingMode.HALF_UP));
            System.out.println("  Average: " + floatSum.divide(BigDecimal.valueOf(floatCount),
                    RoundingMode.HALF_UP).setScale(3, RoundingMode.HALF_UP));
        }
        if (stringCount > 0) {
            System.out.println("Strings: ");
            System.out.println("  Min length: " + minLength);
            System.out.println("  Max length: " + maxLength);
        }
    }
}