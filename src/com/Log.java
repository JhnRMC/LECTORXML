package com;

import java.util.logging.Logger;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

public class Log {

    private final static Logger LOGGER_DTO = java.util.logging.Logger.getLogger("com.readerxml.conexion");
    private final static Logger LOGGER_LECTOR = java.util.logging.Logger.getLogger("com.readerxml");
    private final static Logger LOGGER_DAO = java.util.logging.Logger.getLogger("com.readerxml.dao");
    private final static Logger LOGGER_RAIZ = java.util.logging.Logger.getLogger("com");
    private final static Logger LOGGER = java.util.logging.Logger.getLogger(Log.class.getName());
    private final static Logger LOGGER_CONTROLLER = java.util.logging.Logger.getLogger("com.readerxml.controller");
    
    public static void registrar() {

        try {
            Handler consoleHandler = new ConsoleHandler();

            Handler fileHandler = new FileHandler("./bitacora.log", false);

            SimpleFormatter simpleFormatter = new SimpleFormatter();

            fileHandler.setFormatter(simpleFormatter);
            LOGGER_RAIZ.addHandler(consoleHandler);
            LOGGER_RAIZ.addHandler(fileHandler);
            consoleHandler.setLevel(Level.ALL);
            fileHandler.setLevel(Level.SEVERE);
            fileHandler.setLevel(Level.WARNING);

            LOGGER.log(Level.INFO, "Log inicializada - LECTOR");

        } catch (IOException | SecurityException ex) {
            LOGGER.log(Level.SEVERE, getStackTrace(ex));
        }
    }
    
    public static String getStackTrace(Exception e) {
        StringWriter sWriter = new StringWriter();
        PrintWriter pWriter = new PrintWriter(sWriter);
        e.printStackTrace(pWriter);
        return sWriter.toString();

    }
}
