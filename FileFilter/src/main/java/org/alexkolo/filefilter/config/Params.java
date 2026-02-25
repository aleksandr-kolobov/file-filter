package org.alexkolo.filefilter.config;

import lombok.Getter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Params {
    private static final String REGEX_PREFIX = "^[^\\\\/:*?\"<>|]+$";

    private String outputPath = "";
    private String filePrefix = "";
    private boolean appendMode = false;
    private boolean fullStatistics = false;
    private boolean shortStatistics = false;
    private final List<String> inputFiles = new ArrayList<>();

    public Params(String[] args) {
        initParams(args);
        validateParams();
        printParams();
    }

    private void initParams(String[] args) {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-o" -> {
                    if ((i + 1 < args.length) && (args[i + 1].charAt(0) != '-')) {
                        outputPath = args[++i];
                    } else {
                        System.err.println("Option -o without value!");
                    }
                }
                case "-p" -> {
                    if ((i + 1 < args.length) && (args[i + 1].charAt(0) != '-')) {
                        filePrefix = args[++i];
                    } else {
                        System.err.println("Option -p without value!");
                    }
                }
                case "-a" -> appendMode = true;
                case "-f" -> fullStatistics = true;
                case "-s" -> shortStatistics = true;
                default -> {
                    if (args[i].charAt(0) != '-') {
                        inputFiles.add(args[i]);
                    } else {
                        System.err.println("Unknown option: " + args[i]);
                    }
                }
            }
        }
    }

    private void validateParams() {
        try {
            Path dir = Paths.get(outputPath);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
            if (!outputPath.isEmpty()) {
                outputPath = dir + File.separator;
            }
        } catch (Exception e) {
            System.err.println("Option -o has incorrect value!");
            outputPath = "";
        }

        if (!filePrefix.isEmpty() && !filePrefix.matches(REGEX_PREFIX)) {
            System.err.println("Option -p has incorrect value!");
            filePrefix = "";
        }

        if (shortStatistics && fullStatistics) {
            System.err.println("Cannot use both -s and -f options! Will be used full mode!");
        }

        if (inputFiles.isEmpty()) {
            System.err.println("No input files specified!");
        }
    }

    private void printParams() {
        System.out.println("File Filter utility starts with params:");
        System.out.println("Output path: " + outputPath);
        System.out.println("Output file prefix: " + filePrefix);
        System.out.println("File write mode: " + (appendMode ? "Append" : "Rewrite"));
        System.out.println("Statistics mode: " + (fullStatistics ? "Full" : "Short"));
        System.out.println("Input files: " + inputFiles);
    }
}
