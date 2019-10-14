package com.readerxml;

import com.readerxml.bean.ErrorEtiquetas;
import com.readerxml.controller.LectorXML;
import com.readerxml.controller.Archivo;
import com.readerxml.controller.Xml;
import com.Log;
import com.readerxml.util.Propiedades;
import javax.mail.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class LectorEmail {

    private LectorXML lectorXML;
    public static int cantidadCorreosLeidos;

    public static String email;
    public static String flag;
    public static String asunto;
    public static String fecha;
    private boolean correosPorLeer;
    private int cantidadCorreosActual;
    private int cantidadCorreoSinLeer;
    private final static Logger LOGGER = Logger.getLogger(LectorEmail.class.getName());
    private Message correo;
    private int cont = 0;
    private Part[] archivos;
    private boolean correoValido;
    private List<String> emailsNoDeseados;

    public LectorEmail() {
        Propiedades.cargarPropiedades();
    }

    public void configuracionEmail() {

        try {
            lectorXML = new LectorXML();
            Session session = Session.getInstance(Propiedades.propiedades);
            session.setDebug(true);
            Store store = session.getStore(Propiedades.propiedades.getProperty("protocolo.correo"));
            store.connect(Propiedades.propiedades.getProperty("host.name"),
                    Propiedades.propiedades.getProperty("usuario.correo"),
                    Propiedades.propiedades.getProperty("password.correo"));
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            emailsNoDeseados = Arrays.asList(Propiedades.propiedades.getProperty("mail.no.deseados").split(","));
            lecturaBuzon(inbox);
                inbox.close(false);
                store.close();
        } catch (MessagingException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void lecturaBuzon(Folder buzon) {

        cantidadCorreosLeidos = Integer.parseInt(Propiedades.propiedades.getProperty("cantidad.msg.buzon.leidos"));

        try {
            cantidadCorreosActual = buzon.getMessageCount();
            Message[] correos = buzon.getMessages(cantidadCorreosLeidos, cantidadCorreosActual);
            cantidadCorreoSinLeer = correos.length;
            for (int i = 0; i <= cantidadCorreosActual; i++) {
                archivos = new Part[2];
                correo = correos[i];
                LectorEmail.email = obtenerCorrero(correo.getFrom()[0].toString());
                correosPorLeer = true;
                validarCorreros();
                if (!correoValido) {
                    cargarDatosDelRemitente();
                    mostrarInfoDeEnvio();
                    if (esCorrectoElFormatoAdjunto()) {
                        Multipart archivosAdjuntos = (Multipart) correo.getContent();
                        leerInfoAdjunto(archivosAdjuntos);
                        if (esValidoLaCantidadArchivos()) {
                            leerArchivo(archivos);
                        }
                    }
                }
                cantidadCorreosLeidos++;
                Propiedades.escribirPropiedad("cantidad.msg.buzon.leidos", String.valueOf(cantidadCorreosLeidos));
            }
        } catch (ClassCastException castEx) {
            try {
                cantidadCorreosLeidos++;
                Propiedades.escribirPropiedad("cantidad.msg.buzon.leidos", String.valueOf(cantidadCorreosLeidos));
                LOGGER.log(Level.WARNING, "CORREO SIN DOCUMENTO ADJUNTO");
            } catch (IOException ex) {
                Logger.getLogger(LectorEmail.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException error_content) {
            error_content.printStackTrace();
            LOGGER.log(Level.SEVERE, "ERROR EN EL CONTENIDO");
        } catch (MessagingException ex) {
            ex.printStackTrace();
            LOGGER.log(Level.SEVERE, "ERROR EN LECTURA DEL ARCHIVO ADJUNTO DEL CORREO{0}", Log.getStackTrace(ex));
        } catch (ArrayIndexOutOfBoundsException indexEx) {
            if (correosPorLeer) {
                indexEx.printStackTrace();
                LOGGER.log(Level.INFO, "A LA ESPERA DE NUEVOS CORREOS...");
                correosPorLeer = false;
            } else {
                System.out.println("Fecha: " + new Date());
            }
        } catch (IllegalStateException illegalEx) {
            illegalEx.printStackTrace();
            LOGGER.log(Level.SEVERE, "PROBLEMAS EN LA CONFIGURACION DE APERTURA DEL BUZON, SE REALIZA RECONFIGURACION.");
            configuracionEmail();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void validarCorreros() {

        emailsNoDeseados.forEach(emailNoDeseado -> {
            if (LectorEmail.email.contains(emailNoDeseado)) {
                correoValido = true;
            }
        });
    }

    public boolean esValidoLaCantidadArchivos() {
        boolean validacion = cont == 2;
        if (!validacion) {
            LOGGER.log(Level.WARNING, "EL CORREO NO CONTIENE LA CANTIDAD DE  DOCUMENTOS PARA SU VALIDACION, UN PDF Y XML");
            registrarAvisoDeRechazoCorreo();
        }
        return validacion;
    }

    public String obtenerCorrero(String correo) {
        return correo.contains("<") ? (correo.substring((correo.indexOf("<") + 1), correo.indexOf(">"))) : correo;
    }

    private void registrarAvisoDeRechazoCorreo() {
        LOGGER.log(Level.WARNING, "***********");
        LOGGER.log(Level.WARNING, "EMAIL REMITENTE: {0}", LectorEmail.email);
        LOGGER.log(Level.WARNING, "ASUNTO DEL CORREO: {0}", LectorEmail.asunto);
        LOGGER.log(Level.WARNING, "FECHA REMITIDA: {0}", LectorEmail.fecha);
        LOGGER.log(Level.WARNING, "***********");
        lectorXML.registrarAviso(Xml.ERROR_AVISO);
    }

    private void leerArchivo(Part[] archivosAdjuntos) {
        try{
        lectorXML.iniciarLectura(archivosAdjuntos[0]);
        leerPDF(archivosAdjuntos[1]);}
        catch (NullPointerException ex){
            System.out.println("NUMERO DE ARCHIVOS ADJUNTOS SUPERA LOS 2 SOLICITUDADOS");
        }
    }

    private void leerPDF(Part archivosAdjuntos) {
        if (!lectorXML.isExiste()) {
            Archivo.guardarPDF(archivosAdjuntos, lectorXML.documento.getPathPDF());
        }
    }

    private void cargarDatosDelRemitente() throws MessagingException {
        Date fechaEmision = correo.getSentDate();
        LectorEmail.flag = fechaEmision.getYear() + "-" + fechaEmision.getMonth() + "-" + fechaEmision.getMinutes() + "-" + fechaEmision.getTime();
        LectorEmail.asunto = correo.getSubject();
        LectorEmail.fecha = fechaEmision.toLocaleString();
    }

    private void mostrarInfoDeEnvio() {
        System.out.println("********************** INICIO-EMAIL ***********************");
        System.out.println("Email: " + cantidadCorreosLeidos + " de: " + cantidadCorreosActual);
        System.out.println("Fecha: " + LectorEmail.fecha);
        System.out.println("Asunto: " + LectorEmail.asunto);
        System.out.println("De: " + LectorEmail.email);
    }

    private boolean esCorrectoElFormatoAdjunto() {
        boolean tipoMime = false;
        try {
            tipoMime = !correo.isMimeType("text/*");
        } catch (MessagingException ex) {
            ex.printStackTrace();
        }
        return tipoMime;
    }

    private void leerInfoAdjunto(Multipart archivosAdjuntos) {
        try {
            Part primeraParte = null;
            cont = 0;
            int cantidadArchivosAdjuntos = archivosAdjuntos.getCount();
            System.out.println("CANTIDAD DE ARCHIVOS ADJUNTOS(INICIO): " + cantidadArchivosAdjuntos);
            for (int i = 0; i < cantidadArchivosAdjuntos; i++) {
                try {

                    primeraParte = archivosAdjuntos.getBodyPart(i);
                    mostrarDetallesArchivoAdjunto(primeraParte);
                    if ( esCorrectoElFormatoAdjunto() && esCorrectoElDocumento(primeraParte)) {
                        agregarArchivoLista(primeraParte);
                    } else {
                        leerInfoAdjunto((Multipart) primeraParte.getContent());
                    }
                } catch (ClassCastException | MessagingException | IOException ex) {
                    /*ex.printStackTrace();
                    String result = new BufferedReader(new InputStreamReader(primeraParte.getInputStream())).lines()
                    .parallel().collect(Collectors.joining("\n"));
                    System.out.println("RESULTADO: " + result);*/
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (MessagingException ex) {
            ex.printStackTrace();
            Logger.getLogger(LectorEmail.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean esCorrectoElDocumento(Part primeraParte) throws MessagingException {
        if ((esCorretoContenido(primeraParte) && EsCorrectoFormatoMIME(primeraParte)) && esNuloDisposition(primeraParte)) {
            System.out.println("ES CORRECTO EL DOCUMENTO");
            return true;
        } else if (esCorretoContenido(primeraParte) && primeraParte.getDisposition().contains(Part.ATTACHMENT)) {
            System.out.println("ES CORRECTO EL DOCUMENTO");
            return true;
        } else if (esCorrectoElNombre(primeraParte) && primeraParte.getDisposition().contains(Part.ATTACHMENT)) {
            System.out.println("ES CORRECTO EL DOCUMENTO");
            return true;
        }
        System.out.println("ES INCORRECTO EL DOCUMENTO");
        return false;
    }

    public boolean esCorretoContenido(Part primeraParte) throws MessagingException {
        try {
            return primeraParte.getContentType().toLowerCase().trim().contains(".xml") || primeraParte.getContentType().toLowerCase().contains(".pdf");
        } catch (NullPointerException ex) {
            return false;
        }
    }

    public boolean esCorrectoElNombre(Part primeraParte) throws MessagingException {
        try {
            return primeraParte.getFileName().toLowerCase().trim().contains(".xml") || primeraParte.getFileName().toLowerCase().contains(".pdf");
        } catch (NullPointerException ex) {
            return false;
        }
    }

    public boolean EsCorrectoFormatoMIME(Part primeraParte) throws MessagingException {
        return primeraParte.getContentType().toLowerCase().trim().contains("application/octet-stream");
    }

    public boolean esNuloDisposition(Part primeraParte) throws MessagingException {
        return primeraParte.getDisposition() == null;
    }

    public void agregarArchivoLista(Part archivoAdjunto) throws MessagingException {
        String nombreArchivo;
        nombreArchivo = archivoAdjunto.getFileName().toLowerCase();
        if (nombreArchivo.endsWith(".xml")) {
            System.out.println("ARCHIVO NUMERO: " + (++cont));
            System.out.println("NOMBRE ARCHIVO: " + nombreArchivo);
            archivos[0] = archivoAdjunto;
        } else if(nombreArchivo.endsWith(".pdf")) {
            System.out.println("ARCHIVO NUMERO: " + (++cont));
            System.out.println("NOMBRE ARCHIVO: " + nombreArchivo);
            archivos[1] = archivoAdjunto;
        }

    }

    public void mostrarDetallesArchivoAdjunto(Part primeraParte) {
        try {
            System.out.println(primeraParte.getContentType());
            System.out.println(primeraParte.getDescription());
            System.out.println(primeraParte.getDisposition());
            System.out.println(primeraParte.getFileName());
            System.out.println(primeraParte.getLineCount());
            System.out.println(primeraParte.getSize());
        } catch (MessagingException ex) {
            ex.printStackTrace();
        }
    }
}
