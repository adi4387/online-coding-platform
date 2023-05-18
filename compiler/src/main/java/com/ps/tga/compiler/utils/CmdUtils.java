package com.ps.tga.compiler.utils;

import com.ps.tga.compiler.exceptions.ProcessExecutionException;
import com.ps.tga.compiler.exceptions.ProcessExecutionTimeoutException;
import com.ps.tga.compiler.models.ProcessOutput;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import static com.ps.tga.logger.LogEvent.eventType;
import static com.ps.tga.logger.LogEvent.unexpectedError;
import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Slf4j
public abstract class CmdUtils {

    private static final int MAX_ERROR_LENGTH = 1000; // number of chars
    public static final String LONG_MESSAGE_TRAIL = "...";

    private CmdUtils() {}

    public static String readOutput(BufferedReader reader) throws IOException {
        String line;
        StringBuilder builder = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            builder.append(line);
            builder.append(System.getProperty("line.separator"));
        }

        return builder.toString();
    }

    public static boolean compareOutput(String output, String expectedOutput) {
        return trimText(output).equals(trimText(expectedOutput));
    }

    private static String trimText(String text) {
        return text
                .trim()
                .replaceAll("\\s+", " ")
                .replace("/n","");
    }

    public static String buildErrorOutput(String readOutput) {
        if (readOutput.length() > MAX_ERROR_LENGTH) {
            return readOutput.substring(0, MAX_ERROR_LENGTH - LONG_MESSAGE_TRAIL.length()) + LONG_MESSAGE_TRAIL;
        }
        return readOutput;
    }

    public static ProcessOutput executeProcess(String[] commands, long timeout)
            throws ProcessExecutionException, ProcessExecutionTimeoutException {

        if (timeout <= 0) {
            throw new IllegalArgumentException("timeout should be a positive value");
        }

        if (commands == null || commands.length < 1) {
            throw new IllegalArgumentException("commands should have at least one element");
        }

        try {
            log.info(eventType("runContainer").with("command", asList(commands)).asJSON());
            ProcessBuilder processbuilder = new ProcessBuilder(commands);
            Process process = processbuilder.start();
            long executionStartTime = System.currentTimeMillis();

            // Do not let the process exceed the timeout
            process.waitFor(timeout, MILLISECONDS);

            long executionEndTime = System.currentTimeMillis();

            int status;
            String stdOut;
            String stdErr;

            if (process.isAlive()) {
                log.info(eventType("processAlive").with("The process exceeded the {} Millis allowed for its execution", timeout).asJSON());
                process.destroy();
                throw new ProcessExecutionTimeoutException(timeout);
            } else {
                status = process.exitValue();

                BufferedReader containerOutputReader =
                        new BufferedReader(new InputStreamReader(process.getInputStream()));
                stdOut = CmdUtils.readOutput(containerOutputReader);

                BufferedReader containerErrorReader =
                        new BufferedReader(new InputStreamReader(process.getErrorStream()));
                stdErr = CmdUtils.buildErrorOutput(CmdUtils.readOutput(containerErrorReader));
            }

            return ProcessOutput
                    .builder()
                    .stdOut(stdOut)
                    .stdErr(stdErr)
                    .status(status)
                    .executionDuration(executionEndTime - executionStartTime)
                    .build();

        } catch(RuntimeException | InterruptedException | IOException exception) {
            if (exception instanceof ProcessExecutionTimeoutException) {
                throw new ProcessExecutionTimeoutException(exception.getMessage());
            }
            Thread.currentThread().interrupt();
            log.error(unexpectedError(exception).asJSON());
            throw new ProcessExecutionException("Fatal error for command " + Arrays.toString(commands) + " : " + exception.getMessage());
        }
    }
}

