package com.nokia.oss.commons.tools.outage;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by harchen on 8/27/2015.
 */
public class NSSCapacityLogger {
    private static final Logger LOGGER = Logger.getLogger(NSSCapacityLogger.class.toString());
    private String capacityLogFileName = "nssCapacity.log";
    private StringBuilder summary = new StringBuilder();

    /**
     * Writes the summary string to file.
     */
    final public synchronized void writetoFile() {
        try {
            OutputStream writer = new FileOutputStream(capacityLogFileName, true);
            writer.write(summary.toString().getBytes());
            writer.flush();
            writer.close();
            summary = null;
            summary = new StringBuilder();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }
}
