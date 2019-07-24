package com.readerxml;

import com.readerxml.bean.ErrorEtiquetas;
import com.readerxml.controller.LectorXML;
import com.readerxml.controller.Archivo;
import com.readerxml.controller.Xml;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.mail.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LectorEmail extends Thread {

    private final static Logger LOGGER_DTO = Logger.getLogger("com.readerxml.dto");
    private final static Logger LOG_DAO = Logger.getLogger("com.readerxml.dao");
    private final static Logger LOG_PRINCIPAL = Logger.getLogger("com");
    private final static Logger LOGGER = Logger.getLogger("com.readerxml.LectorEmail");
    private final static Logger LOGGER_ARCHIVO = Logger.getLogger("com.readerxml.controller");
    private LectorXML lectorXML;
    private Properties propiedades;
    private int cantidadMsgBuzonAnterior;
    private final String PROPERTIES = "config_service_email.properties";
    int cantidadArchivos;
    public static String email;
    public static String flag;
    public static String asunto;
    public static String fecha;
    private static LectorEmail lectorEmail;
    private boolean verificacionEnvio = true;

    public static void main(String[] args) {
        lectorEmail = new LectorEmail();
        lectorEmail.registrarLog();
        lectorEmail.start();
    }

    @Override
    public void run() {
        while (true) {
            configuracionEmail();
        }
    }

    private void registrarLog() {
        try {
            Handler consoleHandler = new ConsoleHandler();

            Handler fileHandler = new FileHandler("./bitacora.log", false);

            SimpleFormatter simpleFormatter = new SimpleFormatter();

            fileHandler.setFormatter(simpleFormatter);

            LOG_PRINCIPAL.addHandler(consoleHandler);
            LOG_PRINCIPAL.addHandler(fileHandler);
            consoleHandler.setLevel(Level.ALL);
            fileHandler.setLevel(Level.SEVERE);
            fileHandler.setLevel(Level.WARNING);

            LOGGER.log(Level.INFO, "Log inicializada");

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

    public void configuracionEmail() {
        try {
            propiedades = cargarPropiedades(PROPERTIES);
            cantidadMsgBuzonAnterior = Integer.parseInt(propiedades.getProperty("cantidad.msg.buzon.leidos"));
            try {
                Session session = Session.getInstance(propiedades);
                Store store = session.getStore(propiedades.getProperty("protocolo.correo"));
                store.connect(propiedades.getProperty("host.name"),
                        propiedades.getProperty("usuario.correo"),
                        propiedades.getProperty("password.correo"));

                Folder inbox = store.getFolder("INBOX");
                inbox.open(Folder.READ_ONLY);
                lecturaBuzon(inbox);

                if (inbox.isOpen()) {
                    inbox.close(false);
                    store.close();
                }
            } catch (MessagingException e) {
                LOGGER.log(Level.SEVERE, "PROBLEMA EN LA CONEXION DE RED, VERIFIQUE SU CONEXIÓN A INTERNET");
            } catch (IllegalStateException IlegalEx) {
                LOGGER.log(Level.SEVERE, "PROBLEMA DE SEGURIDAD CON LA LECTURA DEL DOCUMENTO PDF");
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "PROBLEMA EN LA CONEXIÓN CON EL BUZON DE CORREO");
        }
    }

    public void lecturaBuzon(Folder inbox) {
        lectorXML = LectorXML.newInstance();
        try {
            Message[] messages = inbox.getMessages();
            int cantidadMsgBuzonActual = messages.length;
            for (int i = cantidadMsgBuzonAnterior; i <= cantidadMsgBuzonActual; i++) {
                LectorEmail.email = obtenerCorrero(messages[i].getFrom()[0].toString());
                verificacionEnvio = true;
                if (!LectorEmail.email.equalsIgnoreCase(propiedades.getProperty("usuario.correo"))) {
                    Date sentDate = messages[i].getSentDate();
                    LectorEmail.flag = sentDate.getYear() + "-" + sentDate.getMonth() + "-" + sentDate.getMinutes() + "-" + sentDate.getTime();
                    LectorEmail.asunto = messages[i].getSubject();
                    LectorEmail.fecha = sentDate.toLocaleString();
                    Xml.errorEtiquetas = new ErrorEtiquetas();
                    Xml.errorEtiquetas.setFecha(LectorEmail.fecha);
                    Xml.errorEtiquetas.setAsunto(LectorEmail.asunto);
                    Xml.errorEtiquetas.setEmail(LectorEmail.email);
                    try {
                        if (!messages[i].isMimeType("text/*")) {
                            System.out.println("********************** INICIO-EMAIL ***********************");
                            System.out.println("Email: " + messages[i].getMessageNumber() + " de: " + cantidadMsgBuzonActual);
                            System.out.println("Fecha: " + LectorEmail.fecha);
                            System.out.println("Asunto: " + LectorEmail.asunto);
                            System.out.println("De: " + LectorEmail.email);
                            Multipart archivosAdjuntos = (Multipart) messages[i].getContent();
                            cantidadArchivos = archivosAdjuntos.getCount() - 1;
                            if (cantidadArchivos == 2) {
                                for (int j = 1; j <= cantidadArchivos; j++) {
                                    Part archivoAdjuntoXML = archivosAdjuntos.getBodyPart(j);
                                    String nombreArchivoXML = archivoAdjuntoXML.getFileName().toLowerCase();
                                    System.out.println("Contenido: [" + j + "]" + nombreArchivoXML);

                                    if (nombreArchivoXML.endsWith(".xml")) {
                                        lectorXML.iniciarLectura(archivoAdjuntoXML);
                                    } else if (nombreArchivoXML.endsWith(".pdf") && !LectorXML.existe) {
                                        Archivo.guardarPDF(archivoAdjuntoXML, lectorXML.documento.getPathPDF());
                                    }
                                }
                            } else {
                                avisoRegistro();
                                LOGGER.log(Level.WARNING, "SOLO SE ACEPTAN EMAIL CON DOS ARCHIVOS ADJUNTOS, XML Y PDF");
                            }
                        }
                    } catch (NullPointerException FILE_NOFOUND) {
                        FILE_NOFOUND.printStackTrace();
                        avisoRegistro();
                        LOGGER.log(Level.SEVERE, "NO SE PUDO RECUPERAR EL XML ADJUNTO EN EL BUZON: ( REALIZAR NUEVO ADJUNTO DE ARCHIVOS )");
                    }
                }
                cantidadMsgBuzonAnterior++;
                escribirPropiedad("cantidad.msg.buzon.leidos", String.valueOf(cantidadMsgBuzonAnterior));
            }
        } catch (IOException error_content) {
            LOGGER.log(Level.SEVERE, "ERROR EN EL CONTENIDO");
        } catch (MessagingException ex) {

        } catch (ArrayIndexOutOfBoundsException indexEx) {
            if (verificacionEnvio) {
                LOGGER.log(Level.INFO, "A LA ESPERA DE NUEVOS CORREOS...");
                verificacionEnvio = false;
            }
        }
    }

    public String obtenerCorrero(String correo) {
        return correo.contains("<") ? (correo.substring((correo.indexOf("<") + 1), correo.indexOf(">"))) : correo;
    }

    public Properties cargarPropiedades(String nombreArchivo) throws IOException {
        Properties config = new Properties();
        InputStream configInputStream = new FileInputStream(nombreArchivo);
        config.load(configInputStream);
        return config;
    }

    public void escribirPropiedad(String property, String value) throws IOException {
        propiedades.setProperty(property, value);
        propiedades.store(new FileOutputStream(PROPERTIES), "cantidad de correos anteriores leidos - default 21212 - primer documento registrado fecha 25/02/19");
    }

    private void avisoRegistro() {
        LOGGER.log(Level.CONFIG, "***********");
        LOGGER.log(Level.CONFIG, LectorEmail.email);
        LOGGER.log(Level.CONFIG, LectorEmail.asunto);
        LOGGER.log(Level.CONFIG, LectorEmail.fecha);
        Xml.estado = false;
        lectorXML.registrarAviso(Xml.ERROR_AVISO);
        LOGGER.log(Level.CONFIG, "***********");
    }
}
