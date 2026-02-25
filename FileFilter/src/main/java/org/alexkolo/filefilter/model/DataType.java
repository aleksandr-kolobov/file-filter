package org.alexkolo.filefilter.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public enum DataType {
    INTEGER("integers.txt"),
    FLOAT("floats.txt"),
    STRING("strings.txt");

    @Getter
    private final String fileName;

    private static final int MAX_NUMBER_LENGTH = 263;
    private static final Pattern PATTERN_INTEGER = Pattern.compile("[-+]?\\d{1,256}");
    private static final Pattern PATTERN_FLOAT_1
            = Pattern.compile("[-+]?\\d{1,128}\\.?\\d{0,128}([eE][-+]?\\d{1,3})?");
    private static final Pattern PATTERN_FLOAT_2
            = Pattern.compile("[-+]?\\d{0,128}\\.?\\d{1,128}([eE][-+]?\\d{1,3})?");

    public static DataType checkDataType(String str) {
        str = str.trim();
        if (str.length() > MAX_NUMBER_LENGTH) {
            return STRING;
        }
        if (PATTERN_INTEGER.matcher(str).matches()) {
            return INTEGER;
        }
        if (PATTERN_FLOAT_1.matcher(str).matches() || PATTERN_FLOAT_2.matcher(str).matches()) {
            return FLOAT;
        }
        return STRING;
    }
}
