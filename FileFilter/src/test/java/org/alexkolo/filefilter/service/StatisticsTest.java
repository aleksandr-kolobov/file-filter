package org.alexkolo.filefilter.service;

import org.alexkolo.filefilter.model.DataType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.EnumMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class StatisticsTest {

    private Statistics statistics;

    @Test
    void testAddValueWithFullStatsFalse() {
        statistics = new Statistics(false);

        statistics.addValue(DataType.INTEGER, "123");
        statistics.addValue(DataType.FLOAT, "45.67");
        statistics.addValue(DataType.STRING, "test");

        Map<DataType, Long> expectedCounters = new EnumMap<>(DataType.class);
        expectedCounters.put(DataType.INTEGER, 1L);
        expectedCounters.put(DataType.FLOAT, 1L);
        expectedCounters.put(DataType.STRING, 1L);
    }

    @Nested
    class WithFullStatsTrue {

        @BeforeEach
        void setUp() {
            statistics = new Statistics(true);
        }

        @Test
        void testAddIntegerValue_FirstValue() {
            statistics.addValue(DataType.INTEGER, "123");

            assertEquals(1L, statistics.getCounter().get(DataType.INTEGER));
            assertEquals(new BigInteger("123"), statistics.getIntegerMin());
            assertEquals(new BigInteger("123"), statistics.getIntegerMax());
            assertEquals(new BigInteger("123"), statistics.getIntegerSum());
            assertNull(statistics.getFloatMin());
            assertEquals(0L, statistics.getMinLength());
        }

        @Test
        void testAddIntegerValue_SubsequentValues() {
            statistics.addValue(DataType.INTEGER, "123");
            statistics.addValue(DataType.INTEGER, "45");
            statistics.addValue(DataType.INTEGER, "789");

            assertEquals(3L, statistics.getCounter().get(DataType.INTEGER));
            assertEquals(new BigInteger("45"), statistics.getIntegerMin());
            assertEquals(new BigInteger("789"), statistics.getIntegerMax());
            assertEquals(new BigInteger("957"), statistics.getIntegerSum()); // 123 + 45 + 789 = 957
        }

        @Test
        void testAddIntegerValue_WithNegativeNumbers() {
            statistics.addValue(DataType.INTEGER, "123");
            statistics.addValue(DataType.INTEGER, "-50");
            statistics.addValue(DataType.INTEGER, "75");

            assertEquals(new BigInteger("-50"), statistics.getIntegerMin());
            assertEquals(new BigInteger("123"), statistics.getIntegerMax());
            assertEquals(new BigInteger("148"), statistics.getIntegerSum()); // 123 - 50 + 75 = 148
        }

        @Test
        void testAddIntegerValue_WithLargeNumbers() {
            statistics.addValue(DataType.INTEGER, "999999999999999999999999999999");
            statistics.addValue(DataType.INTEGER, "1");

            assertEquals(new BigInteger("1"), statistics.getIntegerMin());
            assertEquals(new BigInteger("999999999999999999999999999999"), statistics.getIntegerMax());
            assertEquals(new BigInteger("1000000000000000000000000000000"), statistics.getIntegerSum());
        }

        @Test
        void testAddFloatValue_FirstValue() {
            statistics.addValue(DataType.FLOAT, "123.45");

            assertEquals(1L, statistics.getCounter().get(DataType.FLOAT));
            assertEquals(new BigDecimal("123.45"), statistics.getFloatMin());
            assertEquals(new BigDecimal("123.45"), statistics.getFloatMax());
            assertEquals(new BigDecimal("123.45"), statistics.getFloatSum());
        }

        @Test
        void testAddFloatValue_SubsequentValues() {
            statistics.addValue(DataType.FLOAT, "10.5");
            statistics.addValue(DataType.FLOAT, "20.3");
            statistics.addValue(DataType.FLOAT, "5.2");

            assertEquals(3L, statistics.getCounter().get(DataType.FLOAT));
            assertEquals(new BigDecimal("5.2"), statistics.getFloatMin());
            assertEquals(new BigDecimal("20.3"), statistics.getFloatMax());
            assertEquals(new BigDecimal("36.0"), statistics.getFloatSum()); // 10.5 + 20.3 + 5.2 = 36.0
        }

        @Test
        void testAddFloatValue_WithNegativeNumbers() {
            statistics.addValue(DataType.FLOAT, "10.5");
            statistics.addValue(DataType.FLOAT, "-5.3");
            statistics.addValue(DataType.FLOAT, "7.8");

            assertEquals(new BigDecimal("-5.3"), statistics.getFloatMin());
            assertEquals(new BigDecimal("10.5"), statistics.getFloatMax());
            assertEquals(new BigDecimal("13.0"), statistics.getFloatSum()); // 10.5 - 5.3 + 7.8 = 13.0
        }

        @Test
        void testAddFloatValue_WithHighPrecision() {
            statistics.addValue(DataType.FLOAT, "123.456789");
            statistics.addValue(DataType.FLOAT, "987.654321");

            assertEquals(new BigDecimal("123.456789"), statistics.getFloatMin());
            assertEquals(new BigDecimal("987.654321"), statistics.getFloatMax());
            assertEquals(new BigDecimal("1111.11111"), statistics.getFloatSum().setScale(5));
        }

        @Test
        void testAddStringValue_FirstValue() {
            statistics.addValue(DataType.STRING, "hello");

            assertEquals(1L, statistics.getCounter().get(DataType.STRING));
            assertEquals(5, statistics.getMinLength());
            assertEquals(5, statistics.getMaxLength());
        }

        @Test
        void testAddStringValue_SubsequentValues() {
            statistics.addValue(DataType.STRING, "hello");
            statistics.addValue(DataType.STRING, "hi");
            statistics.addValue(DataType.STRING, "greetings");

            assertEquals(3L, statistics.getCounter().get(DataType.STRING));
            assertEquals(2, statistics.getMinLength());
            assertEquals(9, statistics.getMaxLength());
        }

        @Test
        void testAddStringValue_WithEmptyString() {
            statistics.addValue(DataType.STRING, "");
            statistics.addValue(DataType.STRING, "hello");

            assertEquals(0, statistics.getMinLength());
            assertEquals(5, statistics.getMaxLength());
        }

        @Test
        void testAddStringValue_WithVeryLongString() {
            String longString = "a".repeat(1000);
            statistics.addValue(DataType.STRING, "short");
            statistics.addValue(DataType.STRING, longString);

            assertEquals(5, statistics.getMinLength());
            assertEquals(1000, statistics.getMaxLength());
        }

        @Test
        void testAddMixedTypes() {
            statistics.addValue(DataType.INTEGER, "100");
            statistics.addValue(DataType.FLOAT, "50.5");
            statistics.addValue(DataType.STRING, "test");
            statistics.addValue(DataType.INTEGER, "200");
            statistics.addValue(DataType.FLOAT, "25.3");

            assertEquals(2L, statistics.getCounter().get(DataType.INTEGER));
            assertEquals(2L, statistics.getCounter().get(DataType.FLOAT));
            assertEquals(1L, statistics.getCounter().get(DataType.STRING));

            assertEquals(new BigInteger("100"), statistics.getIntegerMin());
            assertEquals(new BigInteger("200"), statistics.getIntegerMax());
            assertEquals(new BigInteger("300"), statistics.getIntegerSum());

            assertEquals(new BigDecimal("25.3"), statistics.getFloatMin());
            assertEquals(new BigDecimal("50.5"), statistics.getFloatMax());
            assertEquals(new BigDecimal("75.8"), statistics.getFloatSum());

            assertEquals(4, statistics.getMinLength());
            assertEquals(4, statistics.getMaxLength());
        }

        @Test
        void testAddValue_WithNullValue() {
            assertThrows(NullPointerException.class, () -> {
                statistics.addValue(DataType.STRING, null);
            });
        }

        @Test
        void testAddValue_WithInvalidInteger() {
            assertThrows(NumberFormatException.class, () -> {
                statistics.addValue(DataType.INTEGER, "not a number");
            });
        }

        @Test
        void testAddValue_WithInvalidFloat() {
            assertThrows(NumberFormatException.class, () -> {
                statistics.addValue(DataType.FLOAT, "not a float");
            });
        }
    }
}