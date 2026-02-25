package org.alexkolo.filefilter;

import org.alexkolo.filefilter.config.Params;
import org.alexkolo.filefilter.service.FilterService;
import org.alexkolo.filefilter.service.Statistics;
import org.alexkolo.filefilter.service.FilterWriter;
import java.io.BufferedReader;
import java.io.FileReader;

public class FileFilterApp {
    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            System.exit(0);
        }

        Params params = new Params(args);
        Statistics statistics = new Statistics(params.isFullStatistics());
        FilterService service = new FilterService(statistics);
        try (FilterWriter writer = new FilterWriter(params.getOutputPath(), params.getFilePrefix(), params.isAppendMode())) {
            for (String inputFile : params.getInputFiles()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
                    service.processFile(reader, writer);
                } catch (Exception e) {
                    System.err.println("Error process file: " + inputFile + " - " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error in process. " + e.getMessage());
        }
        statistics.printStatistics();
    }

    private static void printUsage() {
        String usage = """
        Utility usage: java -jar target/file-filter.jar [options] <input files>
        Options:
          -o <path>    Output directory path (default: current directory)
          -p <prefix>  Prefix for output files (default: empty)
          -a           Append write mode (default: overwrite)
          -s           Short statistics (default)
          -f           Full statistics
        Example: java -jar target/file-filter.jar -o data/result -p res_ data/in1.txt data/in2.txt -f
        """;
        System.out.println(usage);
    }
}