package org.server.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MyLogger {
    private final static Logger logger = LogManager.getLogger("GlobalLogger");

    public static void info(String msg) {
        logger.info(msg);
    }
}
