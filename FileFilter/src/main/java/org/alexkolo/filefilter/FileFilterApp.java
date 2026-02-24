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
        System.out.println("File Filter utility starts with params:");
        System.out.println("Output path: " + params.getOutputPath());
        System.out.println("Output file prefix: " + params.getFilePrefix());
        System.out.println("File write mode: " + (params.isAppendMode() ? "Append" : "Rewrite"));
        System.out.println("Statistics mode: " + (params.isFullStatistics() ? "Full" : "Short"));
        System.out.println("Input files: " + params.getInputFiles());

        FilterService service = new FilterService();
        Statistics statistics = new Statistics(params.isFullStatistics());
        try (FilterWriter writer = new FilterWriter(params.getOutputPath(), params.getFilePrefix(), params.isAppendMode())) {
            for (String inputFile : params.getInputFiles()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
                    service.processFile(reader, writer, statistics);
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
        System.out.println("Utility usage: java -jar file-filter.jar [options] <input files>");
        System.out.println("Options:");
        System.out.println("  -o <path>    Output directory path (default: current directory)");
        System.out.println("  -p <prefix>  Prefix for output files (default: empty)");
        System.out.println("  -a           Append write mode (default: overwrite)");
        System.out.println("  -s           Short statistics (default)");
        System.out.println("  -f           Full statistics");
    }
}