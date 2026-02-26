package org.alexkolo.filefilter.service;

import org.alexkolo.filefilter.model.DataType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Map;

@RequiredArgsConstructor
public class FilterWriter implements AutoCloseable {
    private final String path;
    private final String prefix;
    private final boolean appendMode;

    @Getter
    private final Map<DataType, BufferedWriter> writers = new EnumMap<>(DataType.class);

    public void initWriterByType(DataType dataType) throws IOException {
        if (writers.containsKey(dataType)) {
            System.err.println("Double init writer by type: " + dataType);
            return;
        }
        String fileName = path + prefix + dataType.getFileName();
        writers.put(dataType,new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fileName, appendMode), StandardCharsets.UTF_8)));
    }

    @Override
    public void close() throws IOException {
        for (BufferedWriter writer : writers.values()) {
            writer.close();
        }
    }
}
