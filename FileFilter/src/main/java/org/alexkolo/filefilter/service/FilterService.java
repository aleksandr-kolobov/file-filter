package org.alexkolo.filefilter.service;

import lombok.RequiredArgsConstructor;
import org.alexkolo.filefilter.model.DataType;
import java.io.BufferedReader;
import java.io.IOException;

@RequiredArgsConstructor
public class FilterService {

    private final Statistics statistics;

    public void processFile(BufferedReader reader, FilterWriter writer) throws IOException {
        String line;
        while ((line = processLine(reader)) != null) {
            if (line.isBlank()) {
                continue;
            }
            DataType dataType = DataType.checkDataType(line);
            if (!writer.getWriters().containsKey(dataType)) {
                writer.initWriterByType(dataType);
            }
            writer.getWriters().get(dataType).write(line);
            writer.getWriters().get(dataType).newLine();

            statistics.addValue(dataType, line);
        }
    }

    private String processLine(BufferedReader reader) {
        String line;
        try {
            line = reader.readLine();
        } catch (IOException e) {
            System.err.println("Error process line. " + e.getMessage());
            return "";
        }
        return line;
    }
}
