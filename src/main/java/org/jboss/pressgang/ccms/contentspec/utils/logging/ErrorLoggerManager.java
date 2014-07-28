/*
  Copyright 2011-2014 Red Hat

  This file is part of PresGang CCMS.

  PresGang CCMS is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  PresGang CCMS is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with PresGang CCMS.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.jboss.pressgang.ccms.contentspec.utils.logging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.pressgang.ccms.contentspec.sort.LogMessageComparator;

/**
 * An Error logger manager that manages error logs.
 */
public class ErrorLoggerManager {

    private final Map<String, ErrorLogger> logs = Collections.synchronizedMap(new HashMap<String, ErrorLogger>());
    private int debugLevel = 0;

    /**
     * Gets a logger for a specified name. If the logger doesn't exist then it creates one.
     *
     * @return An error logger object for the specified name
     */
    public ErrorLogger getLogger(final String name) {
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
    public <T> ErrorLogger getLogger(final Class<T> clazz) {
        return getLogger(clazz.getCanonicalName());
    }

    /**
     * Prints all the logger messages to the server console.
     */
    public void printAll() {
        System.out.print(generateLogs());
    }

    /**
     * ErrorLoggerManager constructor
     */
    public ErrorLoggerManager() {
    }

    /**
     * Checks if the logs contain a specific log by name
     *
     * @param name The logger name
     * @return boolean True if the error log is found
     */
    public boolean hasLog(final String name) {
        return logs.containsKey(name);
    }

    /**
     * Gets the error log
     *
     * @param name The logger name
     * @return ErrorLogger
     */
    public ErrorLogger getLog(final String name) {
        return hasLog(name) ? logs.get(name) : null;
    }

    /**
     * Adds the error log
     *
     * @param name The logger name
     */
    public void addLog(final String name) {
        ErrorLogger log = new ErrorLogger(name);
        logs.put(name, log);
        log.setVerboseDebug(debugLevel);
    }

    /**
     * Sets verbose debug level (0, 1 or 2)
     *
     * @param level verbose debug level
     */
    public void setVerboseDebug(final int level) {
        debugLevel = level;
        for (final String logName : logs.keySet()) {
            logs.get(logName).setVerboseDebug(level);
        }
    }

    /**
     * Generates a custom log for all of the error/info/warn/debug messages sent.
     *
     * @return A string containing the contents of the logs
     */
    public String generateLogs(boolean includeLevel) {
        final StringBuilder output = new StringBuilder();
        final ArrayList<LogMessage> messages = new ArrayList<LogMessage>();
        for (final String logName : logs.keySet()) {
            messages.addAll(logs.get(logName).getLogMessages());
        }
        Collections.sort(messages, new LogMessageComparator());
        for (final LogMessage msg : messages) {
            if (includeLevel) {
                output.append(msg.getMessage());
            } else {
                output.append(msg.getOriginalMessage());
            }
            output.append("\n");
        }
        return output.toString();
    }

    /**
     * Generates a custom log for all of the error/info/warn/debug messages sent.
     *
     * @return A string containing the contents of the logs
     */
    public String generateLogs() {
        return generateLogs(true);
    }

    /**
     * Gets a list of all of the logs that are managed.
     *
     * @return A List of all the log messages ordered by their timestamp.
     */
    public List<LogMessage> getLogs() {
        final ArrayList<LogMessage> messages = new ArrayList<LogMessage>();
        for (final String logName : logs.keySet()) {
            messages.addAll(logs.get(logName).getLogMessages());
        }
        Collections.sort(messages, new LogMessageComparator());
        return messages;
    }

    /**
     * Clears the error logs
     */
    public void clearLogs() {
        for (final String logName : logs.keySet()) {
            logs.get(logName).clearLogs();
        }
    }
}
