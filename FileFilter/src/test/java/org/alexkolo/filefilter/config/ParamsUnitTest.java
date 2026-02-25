package org.alexkolo.filefilter.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParamsUnitTest {

    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private ByteArrayOutputStream outContent;
    private ByteArrayOutputStream errContent;

    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        errContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Nested
    class OutputPathTests {

        @Test
        void testOutputPathWithValidPath() {
            String[] args = {"-o", "temp-dir", "input.txt"};
            Params params = new Params(args);

            assertTrue(params.getOutputPath().contains("temp-dir"));
            assertTrue(params.getOutputPath().endsWith(File.separator));
        }

        @Test
        void testOutputPathWithRelativePath() {
            String[] args = {"-o", "test/output", "input.txt"};
            Params params = new Params(args);

            String outputPath = params.getOutputPath();
            assertTrue(outputPath.contains("test" + File.separator + "output"));
            assertTrue(outputPath.endsWith(File.separator));
        }

        @Test
        void testOutputPathWithoutValue() {
            String[] args = {"-o", "-s", "input.txt"};
            Params params = new Params(args);

            assertEquals("", params.getOutputPath());
            String errorOutput = errContent.toString();
            assertTrue(errorOutput.contains("Option -o without value"));
        }

        @Test
        void testOutputPathWithNextOption() {
            String[] args = {"-o", "-p", "test", "input.txt"};
            Params params = new Params(args);

            assertEquals("", params.getOutputPath());
            String errorOutput = errContent.toString();
            assertTrue(errorOutput.contains("Option -o without value"));
        }

        @Test
        void testOutputPathWithInvalidCharacters() {
            String[] args = {"-o", "invalid:*?\"<>|path", "input.txt"};
            Params params = new Params(args);

            assertNotNull(params.getOutputPath());
        }

        @Test
        void testOutputPathWithVeryLongPath() {
            String longPath = "a".repeat(500);
            String[] args = {"-o", longPath, "input.txt"};
            Params params = new Params(args);

            assertNotNull(params.getOutputPath());
        }
    }

    @Nested
    class FilePrefixTests {

        @Test
        void testFilePrefixWithValidPrefix() {
            String[] args = {"-p", "result_", "input.txt"};
            Params params = new Params(args);

            assertEquals("result_", params.getFilePrefix());
        }

        @ParameterizedTest
        @ValueSource(strings = {"test_", "result-", "data123", "_prefix", "a", "A", "1"})
        void testFilePrefixWithVariousValidPrefixes(String prefix) {
            String[] args = {"-p", prefix, "input.txt"};
            Params params = new Params(args);

            assertEquals(prefix, params.getFilePrefix());
        }

        @ParameterizedTest
        @ValueSource(strings = {"test:*?\"<>|file", "file\\name", "file/name"})
        void testFilePrefixWithInvalidPrefixes(String invalidPrefix) {
            String[] args = {"-p", invalidPrefix, "input.txt"};
            Params params = new Params(args);

            assertEquals("", params.getFilePrefix());
            if (!invalidPrefix.isEmpty()) {
                String errorOutput = errContent.toString();
                assertTrue(errorOutput.contains("Option -p has incorrect value"));
            }
        }

        @Test
        void testFilePrefixWithoutValue() {
            String[] args = {"-p", "-s", "input.txt"};
            Params params = new Params(args);

            assertEquals("", params.getFilePrefix());
            String errorOutput = errContent.toString();
            assertTrue(errorOutput.contains("Option -p without value"));
        }

        @Test
        void testFilePrefixWithNextOption() {
            String[] args = {"-p", "-o", "output", "input.txt"};
            Params params = new Params(args);

            assertEquals("", params.getFilePrefix());
            String errorOutput = errContent.toString();
            assertTrue(errorOutput.contains("Option -p without value"));
        }
    }

    @Nested
    class ModeOptionsTests {

        @Test
        void testAppendMode() {
            String[] args = {"-a", "input.txt"};
            Params params = new Params(args);

            assertTrue(params.isAppendMode());
        }

        @Test
        void testShortStatistics() {
            String[] args = {"-s", "input.txt"};
            Params params = new Params(args);

            assertTrue(params.isShortStatistics());
            assertFalse(params.isFullStatistics());
        }

        @Test
        void testFullStatistics() {
            String[] args = {"-f", "input.txt"};
            Params params = new Params(args);

            assertTrue(params.isFullStatistics());
            assertFalse(params.isShortStatistics());
        }

        @Test
        void testBothStatisticsOptions() {
            String[] args = {"-s", "-f", "input.txt"};
            Params params = new Params(args);

            assertTrue(params.isShortStatistics());
            assertTrue(params.isFullStatistics());
            String errorOutput = errContent.toString();
            assertTrue(errorOutput.contains("Cannot use both -s and -f options"));
        }

        @Test
        void testAllOptionsTogether() {
            String[] args = {"-o", "output", "-p", "test_", "-a", "-f", "input1.txt", "input2.txt"};
            Params params = new Params(args);

            assertTrue(params.getOutputPath().contains("output"));
            assertEquals("test_", params.getFilePrefix());
            assertTrue(params.isAppendMode());
            assertTrue(params.isFullStatistics());
            assertFalse(params.isShortStatistics());
        }
    }

    @Nested
    class InputFilesTests {

        @Test
        void testSingleInputFile() {
            String[] args = {"input.txt"};
            Params params = new Params(args);

            assertEquals(1, params.getInputFiles().size());
            assertEquals("input.txt", params.getInputFiles().get(0));
        }

        @Test
        void testMultipleInputFiles() {
            String[] args = {"file1.txt", "file2.txt", "file3.txt"};
            Params params = new Params(args);

            assertEquals(3, params.getInputFiles().size());
            assertTrue(params.getInputFiles().containsAll(java.util.Arrays.asList("file1.txt", "file2.txt", "file3.txt")));
        }

        @Test
        void testInputFilesWithOptions() {
            String[] args = {"-a", "file1.txt", "-p", "test_", "file2.txt", "-o", "output", "file3.txt"};
            Params params = new Params(args);

            assertEquals(3, params.getInputFiles().size());
            assertEquals("file1.txt", params.getInputFiles().get(0));
            assertEquals("file2.txt", params.getInputFiles().get(1));
            assertEquals("file3.txt", params.getInputFiles().get(2));
        }
    }

    @Nested
    class UnknownOptionsTests {

        @Test
        void testUnknownOption() {
            String[] args = {"-x", "input.txt"};
            Params params = new Params(args);

            assertEquals(1, params.getInputFiles().size());
            String errorOutput = errContent.toString();
            assertTrue(errorOutput.contains("Unknown option: -x"));
        }

        @Test
        void testMultipleUnknownOptions() {
            String[] args = {"-x", "-y", "-z", "input.txt"};
            Params params = new Params(args);

            assertEquals(1, params.getInputFiles().size());
            String errorOutput = errContent.toString();
            assertTrue(errorOutput.contains("Unknown option: -x"));
            assertTrue(errorOutput.contains("Unknown option: -y"));
            assertTrue(errorOutput.contains("Unknown option: -z"));
        }
    }

    @Nested
    class EdgeCasesTests {

        @Test
        void testManyArguments() {
            String[] args = new String[1000];
            for (int i = 0; i < 500; i++) {
                args[i * 2] = "file" + i + ".txt";
                args[i * 2 + 1] = "-a";
            }
            Params params = new Params(args);

            assertTrue(params.getInputFiles().size() > 0);
        }

        @Test
        void testRepeatedOptions() {
            String[] args = {"-o", "old/data", "-o", "other/path", "input.txt"};
            Params params = new Params(args);

            assertTrue(params.getOutputPath().contains("other" + File.separator + "path"));
        }

        @Test
        void testCaseSensitiveOptions() {
            String[] args = {"-A", "-S", "-F", "input.txt"};
            Params params = new Params(args);

            assertFalse(params.isAppendMode());
            assertFalse(params.isShortStatistics());
            assertFalse(params.isFullStatistics());

            String errorOutput = errContent.toString();
            assertTrue(errorOutput.contains("Unknown option: -A"));
            assertTrue(errorOutput.contains("Unknown option: -S"));
            assertTrue(errorOutput.contains("Unknown option: -F"));
        }
    }
}