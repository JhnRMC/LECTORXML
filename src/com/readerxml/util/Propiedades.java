/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.readerxml.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sammy Guergachi <sguergachi at gmail.com>
 */
public class Propiedades {

    private static final String PROPERTIES = "config_service_email.properties";
    public static Properties propiedades;
    private final static Logger LOGGER = Logger.getLogger(Propiedades.class.getName());  

    public static void escribirPropiedad(String property, String value) throws IOException {
        Propiedades.propiedades.setProperty(property, value);
        Propiedades.propiedades.store(new FileOutputStream(PROPERTIES), "cantidad de correos anteriores leidos - default 21212 - primer documento registrado fecha 25/02/19");
    }

    public static void cargarPropiedades() {
        propiedades = new Properties();
        try {
            InputStream configInputStream = new FileInputStream(PROPERTIES);
            propiedades.load(configInputStream);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "LECTURA DEL ARCHIVO 'Properties'");
        }        
    }
}
