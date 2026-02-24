package org.alexkolo.filefilter.model;

import java.util.regex.Pattern;

public enum DataType {
    INTEGER,
    FLOAT,
    STRING;

    private static final Pattern PATTERN_INTEGER = Pattern.compile("[-+]?\\d+");
    private static final Pattern PATTERN_FLOAT_1
            = Pattern.compile("[-+]?\\d*\\.?\\d+([eE][-+]?\\d+)?");
    private static final Pattern PATTERN_FLOAT_2
            = Pattern.compile("[-+]?\\d+\\.?\\d*([eE][-+]?\\d+)?");

    public static DataType checkDataType(String str) {
        if (PATTERN_INTEGER.matcher(str).matches()) {
            return DataType.INTEGER;
        }
        if (PATTERN_FLOAT_1.matcher(str).matches() || PATTERN_FLOAT_2.matcher(str).matches()) {
            return DataType.FLOAT;
        }
        return DataType.STRING;
    }
}
