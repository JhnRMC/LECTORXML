package com.readerxml;

import com.readerxml.bean.ErrorEtiquetas;
import com.readerxml.controller.LectorXML;
import com.readerxml.controller.Archivo;
import com.readerxml.controller.Xml;
import com.readerxml.util.Log;
import com.readerxml.util.Propiedades;
import javax.mail.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LectorEmail {

    private LectorXML lectorXML;
    public static int cantidadCorreosLeidos;

    public static String email;
    public static String flag;
    public static String asunto;
    public static String fecha;
    private static LectorEmail lectorEmail;
    private boolean isValidoElCorreo;
    private int cantidadCorreosActual;
    private final static Logger LOGGER = Logger.getLogger(LectorEmail.class.getName());
    private Message correo;
    private int cont = 0;
    private List<Part> archivos;

    public LectorEmail() {
        Propiedades.cargarPropiedades();
    }

    public void configuracionEmail() {

        try {
            lectorXML = new LectorXML();
            Session session = Session.getInstance(Propiedades.propiedades);
            Store store = session.getStore(Propiedades.propiedades.getProperty("protocolo.correo"));
            store.connect(Propiedades.propiedades.getProperty("host.name"),
                    Propiedades.propiedades.getProperty("usuario.correo"),
                    Propiedades.propiedades.getProperty("password.correo"));
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            lecturaBuzon(inbox.getMessages());
            if (inbox.isOpen()) {
                inbox.close(false);
                store.close();
            }
        } catch (MessagingException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void lecturaBuzon(Message[] correos) {

        cantidadCorreosLeidos = Integer.parseInt(Propiedades.propiedades.getProperty("cantidad.msg.buzon.leidos"));
        try {

            cantidadCorreosActual = correos.length;
            for (int i = cantidadCorreosLeidos; i <= cantidadCorreosActual; i++) {
                archivos = new ArrayList();
                correo = correos[i];
                LectorEmail.email = obtenerCorrero(correo.getFrom()[0].toString());
                isValidoElCorreo = true;
                if (!LectorEmail.email.equalsIgnoreCase(Propiedades.propiedades.getProperty("usuario.correo"))) {
                    cargarDatosDelRemitente();
                    mostrarInfoDeEnvio();
                    if (esCorrectoElFormatoAdjunto()) {
                        Multipart archivosAdjuntos = (Multipart) correo.getContent();
                        leerInfoAdjunto(archivosAdjuntos);
                        if (esValidoLaCantidadArchivos()) {
                            archivos.forEach(archivo -> leerXML(archivo));
                            archivos.forEach(archivo -> leerPDF(archivo));
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
            LOGGER.log(Level.SEVERE, "ERROR EN EL CONTENIDO");
        } catch (MessagingException ex) {
            LOGGER.log(Level.SEVERE, "ERROR EN LECTURA DEL ARCHIVO ADJUNTO DEL CORREO{0}", Log.getStackTrace(ex));
        } catch (ArrayIndexOutOfBoundsException indexEx) {
            if (isValidoElCorreo) {
                LOGGER.log(Level.INFO, "A LA ESPERA DE NUEVOS CORREOS...");
                isValidoElCorreo = false;
            } else {
                System.out.println("Fecha: " + new Date());
            }
        } catch (IllegalStateException illegalEx) {
            LOGGER.log(Level.SEVERE, "PROBLEMAS EN LA CONFIGURACION DE APERTURA DEL BUZON, SE REALIZA RECONFIGURACION.");
            configuracionEmail();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            Runtime garbage = Runtime.getRuntime();
            garbage.gc();
        }
    }

    public boolean esValidoLaCantidadArchivos() {
        boolean validacion = archivos.size() == 2;
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
        Xml.estado = false;
        lectorXML.registrarAviso(Xml.ERROR_AVISO);
    }

    private void leerXML(Part archivosAdjuntos) {
        String nombreArchivo;
        try {
            nombreArchivo = archivosAdjuntos.getFileName().toLowerCase();
            if (nombreArchivo.endsWith(".xml")) {
                lectorXML.iniciarLectura(archivosAdjuntos);
            }
        } catch (MessagingException ex) {
            ex.printStackTrace();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    private void leerPDF(Part archivosAdjuntos) {
        String nombreArchivo;
        try {
            nombreArchivo = archivosAdjuntos.getFileName().toLowerCase();
            if (!LectorXML.existe) {
                nombreArchivo = archivosAdjuntos.getFileName().toLowerCase();
                if (nombreArchivo.endsWith(".pdf") && !LectorXML.existe) {
                    Archivo.guardarPDF(archivosAdjuntos, lectorXML.documento.getPathPDF());
                }
            }
        } catch (MessagingException ex) {
            ex.printStackTrace();
        } catch (NullPointerException ex) {

        }
    }

    private void cargarDatosDelRemitente() throws MessagingException {
        Date fechaEmision = correo.getSentDate();
        LectorEmail.flag = fechaEmision.getYear() + "-" + fechaEmision.getMonth() + "-" + fechaEmision.getMinutes() + "-" + fechaEmision.getTime();
        LectorEmail.asunto = correo.getSubject();
        LectorEmail.fecha = fechaEmision.toLocaleString();
        Xml.errorEtiquetas = new ErrorEtiquetas();
        Xml.errorEtiquetas.setFecha(LectorEmail.fecha);
        Xml.errorEtiquetas.setAsunto(LectorEmail.asunto);
        Xml.errorEtiquetas.setEmail(LectorEmail.email);
    }

    private void mostrarInfoDeEnvio() {
        System.out.println("********************** INICIO-EMAIL ***********************");
        System.out.println("Email: " + correo.getMessageNumber() + " de: " + cantidadCorreosActual);
        System.out.println("Fecha: " + LectorEmail.fecha);
        System.out.println("Asunto: " + LectorEmail.asunto);
        System.out.println("De: " + LectorEmail.email);
    }

    private boolean esCorrectoElFormatoAdjunto() {
        boolean tipoMime = false;
        try {
            tipoMime = !correo.isMimeType("text/*");
        } catch (MessagingException ex) {
            Logger.getLogger(LectorEmail.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tipoMime;
    }

    private void leerInfoAdjunto(Multipart archivosAdjuntos) throws MessagingException, IOException {
        Part primeraParte = null;
        cont = 0;
        int cantidadArchivosAdjuntos = archivosAdjuntos.getCount();
        for (int i = 0; i < cantidadArchivosAdjuntos; i++) {
            try {
                primeraParte = archivosAdjuntos.getBodyPart(i);
                if (esCorrectoElDocumento(primeraParte)) {
                    agregarArchivoLista(primeraParte);
                } else {
                    leerInfoAdjunto((Multipart) primeraParte.getContent());
                }
            } catch (ClassCastException | MessagingException ex) {
                /*String result = new BufferedReader(new InputStreamReader(primeraParte.getInputStream())).lines()
                        .parallel().collect(Collectors.joining("\n"));
                System.out.println("RESULTADO: " + result);*/
            } catch (IOException ex) {
                Logger.getLogger(LectorEmail.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public boolean esCorrectoElDocumento(Part primeraParte) throws MessagingException {
        if ((esCorretoContenido(primeraParte) && EsCorrectoFormatoMIME(primeraParte)) && esNuloDisposition(primeraParte)) {
            return true;
        } else if (esCorretoContenido(primeraParte) && primeraParte.getDisposition().contains(Part.ATTACHMENT)) {
            return true;
        } else if (esCorrectoElNombre(primeraParte) && primeraParte.getDisposition().contains(Part.ATTACHMENT)) {
            return true;
        }
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
        System.out.println("ARCHIVO NUMERO: " + (++cont));
        nombreArchivo = archivoAdjunto.getFileName().toLowerCase();
        System.out.println("NOMBRE ARCHIVO: " + nombreArchivo);
        archivos.add(archivoAdjunto);
    }
}
