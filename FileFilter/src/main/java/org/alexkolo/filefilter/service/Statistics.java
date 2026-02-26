package org.alexkolo.filefilter.service;

import lombok.Getter;
import org.alexkolo.filefilter.model.DataType;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.EnumMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class Statistics {
    private final boolean isFull;
    private final Map<DataType, Long> counter = new EnumMap<>(DataType.class);

    private BigInteger integerMin, integerMax, integerSum;
    private BigDecimal floatMin, floatMax, floatSum;
    private int minLength, maxLength;

    public void addValue(DataType type, String value) {
        counter.merge(type, 1L, Long::sum);
        if (!isFull) {
            return;
        }
        switch (type) {
            case INTEGER -> {
                var tmp = new BigInteger(value);
                if (integerMin == null) {
                    integerMin = integerMax = integerSum = tmp;
                } else {
                    integerMin = tmp.min(integerMin);
                    integerMax = tmp.max(integerMax);
                    integerSum = integerSum.add(tmp);
                }
            }
            case FLOAT -> {
                var tmp = new BigDecimal(value);
                if (floatMin == null) {
                    floatMin = floatMax = floatSum = tmp;
                } else {
                    floatMin = tmp.min(floatMin);
                    floatMax = tmp.max(floatMax);
                    floatSum = floatSum.add(tmp);
                }
            }
            case STRING -> {
                int length = value.length();
                if (counter.get(type) == 1L) {
                    minLength = maxLength = length;
                } else {
                    minLength = Math.min(minLength, length);
                    maxLength = Math.max(maxLength, length);
                }
            }
        }
    }

    public void printStatistics() {
        System.out.println("********* Count Statistics *********");
        counter.forEach((type, count) -> System.out.println(type + ": " + count));
        if (!isFull) {
            return;
        }
        System.out.println("********* Full Statistics *********");
        if (integerMin != null) {
            var avg = integerSum.divide(BigInteger.valueOf(counter.get(DataType.INTEGER)));
            System.out.printf("%s:%n  Min: %s%n  Max: %s%n  Sum: %s%n  Avg: %s%n",
                    DataType.INTEGER, integerMin, integerMax, integerSum, avg);
        }
        if (floatMin != null) {
            var count = BigDecimal.valueOf(counter.get(DataType.FLOAT));
            var avg = floatSum.divide(count, 3, RoundingMode.HALF_UP);
            System.out.printf("%s:%n  Min: %.3f%n  Max: %.3f%n  Sum: %.3f%n  Avg: %.3f%n",
                    DataType.FLOAT, floatMin, floatMax, floatSum, avg);
        }
        if (counter.containsKey(DataType.STRING)) {
            System.out.printf("%s:%n  Min len: %d%n  Max len: %d%n",
                    DataType.STRING, minLength, maxLength);
        }
    }
}