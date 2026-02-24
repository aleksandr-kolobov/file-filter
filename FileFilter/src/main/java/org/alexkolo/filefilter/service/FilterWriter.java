package org.alexkolo.filefilter.service;

import org.alexkolo.filefilter.model.DataType;
import lombok.Getter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

public class FilterWriter implements AutoCloseable {
    private static final String INTEGERS = "integers.txt";
    private static final String FLOATS = "floats.txt";
    private static final String STRINGS = "strings.txt";

    private final String path;
    private final String prefix;
    private final boolean appendMode;

    @Getter
    private final Map<DataType, BufferedWriter> writers = new EnumMap<>(DataType.class);

    public FilterWriter(String path, String prefix, boolean appendMode) {
        this.path = path;
        this.prefix = prefix;
        this.appendMode = appendMode;
    }

    public void initWriterByType(DataType dataType) throws IOException {
        if (writers.containsKey(dataType)) {
            throw new RuntimeException("Double init writer by type: " + dataType);
        }
        String fileName = path + prefix + switch (dataType) {
            case INTEGER -> INTEGERS;
            case FLOAT -> FLOATS;
            case STRING -> STRINGS;
        };
        writers.put(dataType, new BufferedWriter(new FileWriter(fileName, appendMode)));
    }

    @Override
    public void close() throws IOException {
        for (BufferedWriter writer : writers.values()) {
            writer.flush();
            writer.close();
        }
    }
}
