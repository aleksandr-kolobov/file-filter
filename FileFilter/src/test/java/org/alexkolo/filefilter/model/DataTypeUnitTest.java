package org.alexkolo.filefilter.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

class DataTypeUnitTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "0", "1", "-1", "+1",
            "1234567890", "-1234567890",
            "92233720368547758073456456345", "-9223372036854775808345634563456345",
            "0000", "-0000", "+0000",
            "00001", "-00001", "+00001"
    })
    void testIntegerValues(String input) {
        assertEquals(DataType.INTEGER, DataType.checkDataType(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "0.0", "1.0", "-1.0", "+1.0",
            "123.456", "-789.012", "+345.678",
            ".5", "0.5", "-.5", "+.5",
            "5.", "5.0", "-5.", "+5.",
            "1e10", "1E10", "-1e10", "+1e10",
            "1.23e-10", "-4.56E+20", "7.89e-05",
            "123.456e789", "0.0000001", "1000000.0"
    })
    void testFloatValues(String input) {
        assertEquals(DataType.FLOAT, DataType.checkDataType(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "abc", "ABC", "abc123", "123abc",
            "12.34.56", "--123", "++456",
            "12e", "e12", "12.34e", "e",
            " ", "\t", "\n", "  ", "   ",
            "null", "undefined", "NaN", "Infinity",
            "-", "+", ".", "-.", "+.",
            "0xFF", "0xABC", "0b101", "0x123",
            "1,000", "1 000", "1_000",
            "①", "②", "③", "一二三"
    })
    void testStringValues(String input) {
        assertEquals(DataType.STRING, DataType.checkDataType(input));
    }

    @Test
    void testVeryLongNumbers() {
        // Создаем число длиннее MAX_NUMBER_LENGTH (263)
        String longInteger = "1" + "0".repeat(300);
        assertEquals(DataType.STRING, DataType.checkDataType(longInteger));

        String longFloat = "1" + "0".repeat(300) + "." + "0".repeat(300);
        assertEquals(DataType.STRING, DataType.checkDataType(longFloat));

        String longExponential = "1" + "0".repeat(300) + "e10";
        assertEquals(DataType.STRING, DataType.checkDataType(longExponential));
    }

    @Test
    void testWhitespaceHandling() {
        assertEquals(DataType.INTEGER, DataType.checkDataType(" 123"));
        assertEquals(DataType.INTEGER, DataType.checkDataType("123 "));
        assertEquals(DataType.INTEGER, DataType.checkDataType(" 123 "));
        assertEquals(DataType.INTEGER, DataType.checkDataType("\t123"));
        assertEquals(DataType.INTEGER, DataType.checkDataType("123\t"));
    }

    @Test
    @Timeout(value = 100, unit = TimeUnit.MILLISECONDS)
    void testBulkProcessing() {
        String[] testValues = {
                "123", "-456", "+789", "0",
                "123.456", "-789.012", ".5", "5.",
                "1e10", "1.23e-10", "abc", "123abc"
        };
        assertTimeoutPreemptively(Duration.ofMillis(50), () -> {
            for (int i = 0; i < 1000; i++) {
                for (String value : testValues) {
                    DataType result = DataType.checkDataType(value);
                    assertNotNull(result);
                }
            }
        });
    }
}