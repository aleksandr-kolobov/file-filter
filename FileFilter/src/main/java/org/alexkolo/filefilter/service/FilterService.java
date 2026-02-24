package org.alexkolo.filefilter.service;

import org.alexkolo.filefilter.model.DataType;
import java.io.BufferedReader;
import java.io.IOException;

public class FilterService {
    public void processFile(BufferedReader reader, FilterWriter writer, Statistics statistics) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) {
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
}
