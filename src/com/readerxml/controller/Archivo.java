package com.readerxml.controller;

import com.readerxml.util.Log;
import javax.mail.MessagingException;
import javax.mail.Part;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;

public class Archivo {

    private final static Logger LOGGER = Logger.getLogger(Archivo.class.getName());

    public static void guardarXML(File savefile, Part part) {
        try {
            InputStream is;
            try (BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(savefile))) {
                byte[] buff = new byte[2048];
                is = part.getInputStream();
                int ret;
                while ((ret = is.read(buff)) > 0) {
                    bos.write(buff, 0, ret);
                }
            }
            is.close();
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "PROBLEMAS: {0}", Log.getStackTrace(e));
        } catch (IOException | MessagingException e) {
            LOGGER.log(Level.SEVERE, "PROBLEMAS: {0}", Log.getStackTrace(e));
        }
    }

    public static void guardarPDF(Part parte, String path) {
        try (PDDocument doc = PDDocument.load(parte.getInputStream())) {
            doc.setAllSecurityToBeRemoved(true);
            doc.save(path);
            LOGGER.log(Level.INFO, "RUTA GUARDADA PDF: {0}", path);
            
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "PROBLEMAS: {0}", Log.getStackTrace(ex));
        } catch (MessagingException ex) {
            LOGGER.log(Level.SEVERE, "PROBLEMA PARA EL GUARDADO DEL PDF");
        } catch (NullPointerException nulex){
            LOGGER.log(Level.SEVERE, "INTENTO DE LEER UN ARCHIVO PDF VACIO (NULO)");
        }
    }
}
