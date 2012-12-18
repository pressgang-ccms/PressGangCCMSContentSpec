package org.jboss.pressgang.ccms.contentspec.utils.logging;

import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.jboss.pressgang.ccms.utils.structures.Pair;


/**
 * An Error logger manager that manages error logs.
 */
public class LoggerManager {

    private static final Map<String, Pair<Logger, StringWriter>> logs = Collections.synchronizedMap(
            new HashMap<String, Pair<Logger, StringWriter>>());
    private static Level level = Level.INFO;

    // Global Log writer
    private static StringWriter globalLogWriter = new StringWriter();
    private static final WriterAppender globalAppender = new WriterAppender(new PatternLayout("%5p - %m%n"), globalLogWriter);

    static {
        globalAppender.setThreshold(Level.ALL);
        globalAppender.setName("globalAppender");
    }

    /**
     * Gets a logger for a specified name. If the logger doesn't exist then it creates one.
     *
     * @return An error logger object for the specified name
     */
    public static Logger getLogger(final String name) {
        if (name == null || name.equals("")) return null;
        if (!hasLog(name)) {
            addLog(name);
        }
        return getLog(name);
    }

    /**
     * Gets a logger for a specified class. If the logger doesn't exist then it creates one.
     *
     * @return An error logger object for the specified class
     */
    public static <T> Logger getLogger(final Class<T> clazz) {
        return getLogger(clazz.getCanonicalName());
    }

    /**
     * Prints all the logger messages to the server console.
     */
    public static void printAll() {
        System.out.print(generateLogs());
    }

    /**
     * Checks if the logs contain a specific log by name
     *
     * @param name The logger name
     * @return boolean True if the error log is found
     */
    protected static boolean hasLog(final String name) {
        return logs.containsKey(name);
    }

    /**
     * Gets the error log
     *
     * @param name The logger name
     * @return ErrorLogger
     */
    protected static Logger getLog(final String name) {
        return hasLog(name) ? logs.get(name).getFirst() : null;
    }

    /**
     * Adds the error log
     *
     * @param name The logger name
     */
    protected static void addLog(final String name) {
        final Logger log = Logger.getLogger(name);

        // Add the global appender
        log.addAppender(globalAppender);

        // Base Writer
        final StringWriter writer = new StringWriter();
        final WriterAppender appender = new WriterAppender(new PatternLayout("%d{ABSOLUTE} %5p %c{1} - %m%n"), writer);
        appender.setThreshold(Level.ALL);
        appender.setName("name");

        log.addAppender(appender);
        log.setLevel(level);

        logs.put(name, new Pair<Logger, StringWriter>(log, writer));
    }

    /**
     * Sets logging level
     *
     * @param level verbose debug level
     */
    public static void setVerboseDebug(final Level level) {
        if (level == LoggerManager.level) return;

        LoggerManager.level = level;
        for (final Map.Entry<String, Pair<Logger, StringWriter>> entry : logs.entrySet()) {
            final Logger log = entry.getValue().getFirst();
            log.setLevel(level);
        }
    }

    /**
     * Generates a custom log for all of the error/info/warn/debug messages sent.
     *
     * @return A string containing the contents of the logs
     */
    public static String generateLogs() {
        return globalLogWriter.toString();
    }

    public static String generateLog(Class<?> clazz) {
        return generateLog(clazz.getCanonicalName());
    }

    public static String generateLog(final String name) {
        if (hasLog(name)) {
            return logs.get(name).getSecond().toString();
        } else {
            return null;
        }
    }

    /**
     * Clears the error logs
     */
    public static void clearLogs() {
        globalLogWriter.getBuffer().setLength(0);
        globalAppender.setWriter(globalLogWriter);

        for (final Entry<String, Pair<Logger, StringWriter>> entry : logs.entrySet()) {
            entry.getValue().getSecond().getBuffer().setLength(0);
        }
    }

    protected LoggerManager() {
    }
}
