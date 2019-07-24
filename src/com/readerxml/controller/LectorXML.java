package com.readerxml.controller;

import com.readerxml.dao.SendEmailSqliteDAO;
import com.readerxml.dao.DocumentoElectronicoDAO;
import com.readerxml.bean.Documento;
import com.readerxml.bean.Detalle;
import com.readerxml.bean.Cabecera;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.mail.MessagingException;
import javax.mail.Part;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.readerxml.LectorEmail;
import com.readerxml.bean.EmailSend;
import com.readerxml.bean.Total;


public class LectorXML extends Xml {
    private final static Logger LOGGER = Logger.getLogger("com.readerxml.controller.LectorXML");
    private static LectorXML lectorXML;
    public Documento documento;
    Cabecera cabecera;
    Detalle detalle;
    ArrayList<Detalle> listaDetalle;
    Total total;
    public static boolean existe = true;

    private LectorXML() {
        documento = new Documento();
        cabecera = new Cabecera();
        detalle = new Detalle();
        total = new Total();
    }

    public static LectorXML newInstance() {
        if (lectorXML == null) {
            lectorXML = new LectorXML();
        }
        return lectorXML;
    }

    public void iniciarLectura(Part archivo) throws MessagingException {
        System.out.println("=============LECTURA DEL XML===========");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document xml = dBuilder.parse(archivo.getInputStream());
            Xml.estado = true;
            super.iniciar(xml);
            cabecera();
            emisor();
            receptor();
            detalle();
            total();
            guardarDocumentoElectronico(archivo);
        } catch (ParserConfigurationException e){
            e.printStackTrace();
        } catch (SAXException e) {
            LOGGER.log(Level.SEVERE, "LECTURA NO PERMITIDA: {0}", LectorEmail.getStackTrace(e));
        } catch (IOException errorFile) {
            LOGGER.log(Level.SEVERE, "ERROR CON EL ARCHIVO");
        } catch (NumberFormatException errorFormat) {
            LOGGER.log(Level.SEVERE, "NO SE ENCONTRO LA ETIQUETA: Invoice, CreditNote o DebitNote, QUE IDENTIFICA UN DOCUMENTO VALIDO.({0})", ETIQUETA_GLOBAL);
        }
    }

    public void guardarDocumentoElectronico(Part archivo) {
        DocumentoElectronicoDAO documentoElectronicoDAO = new DocumentoElectronicoDAO();       
        if (!existeDocumento(documentoElectronicoDAO)) { 
            existe = false;
            generarPath(archivo);
            documento.setCabecera(cabecera);
            documento.setTotal(total);
            documento.setDetallesDocumento(listaDetalle);
            if (!Xml.estado) {
                registrarError(Xml.ERROR_ETIQUETA);
                registrarErrorLog();
            } else {
                documentoElectronicoDAO.insertDocumentoElectronico(documento);
            }
        }else{
            existe = true;
            LOGGER.log(Level.INFO, "DOCUMENTO EXISTENTE EN LA BD");
        }
    }
    
    public boolean existeDocumento(DocumentoElectronicoDAO documentoElectronicoDAO){        
        return documentoElectronicoDAO.verificarDocumentoExistente(cabecera.getSerieDocumento(), cabecera.getCorrelativoDocumento(), cabecera.getNroDocumentoEmis(), cabecera.getTipoDocumento());
    }

    public void registrarError(int tipoError) {
        EmailSend emailSend = new EmailSend();
        emailSend.setAsunto("REGISTRO DE ERRORES");
        emailSend.setNombreArchivo(LectorEmail.flag + ".xml");
        emailSend.setRutaArchivo(documento.getPathXML());
        emailSend.setDestino("jhonrmc15@gmail.com");
        emailSend.setTipoMensaje(tipoError);
        SendEmailSqliteDAO dao = new SendEmailSqliteDAO();
        if (dao.registrarSuccessEnvioCorreo(emailSend, errorEtiquetas)) {
            LOGGER.log(Level.WARNING, "REGISTRO EXITOSO, PARA EL ENVIO DE CORREO");
        } else {
            LOGGER.log(Level.WARNING, "ERROR EN EL REGISTRO, PARA EL ENVIO DE CORREO");
        }
    }

    public void registrarAviso(int tipoError) {
        EmailSend emailSend = new EmailSend();
        emailSend.setAsunto("AVISO DE RECHAZO DE CORREO");
        emailSend.setDestino("jhonrmc15@gmail.com");
        emailSend.setNombreArchivo("");
        emailSend.setRutaArchivo("");
        emailSend.setTipoMensaje(tipoError);
        SendEmailSqliteDAO dao = new SendEmailSqliteDAO();
        if (dao.registrarSuccessEnvioCorreo(emailSend, errorEtiquetas)) {
            LOGGER.log(Level.WARNING, "REGISTRO EXITOSO, PARA EL ENVIO DE CORREO");
        } else {
            LOGGER.log(Level.WARNING, "ERROR EN EL REGISTRO, PARA EL ENVIO DE CORREO");
        }
    }

    public void generarPath(Part archivo) {
        String ruta;
        String nombreArchivo = null;
        if (!Xml.estado) {
            ruta = "c:/home/error";
            nombreArchivo = LectorEmail.flag;
        } else {
            nombreArchivo = cabecera.getNroDocumentoEmis() + "-" + cabecera.getCorrelativoDocumento();
            ruta = "c:/home/proveedores/TD_" + tipoDocumento + "/" + cabecera.getNroDocumentoEmis() + "/" + cabecera.getSerieDocumento() + "-" + cabecera.getCorrelativoDocumento();
        }
        File savedir = new File(ruta);
        if (!savedir.exists()) {
            savedir.mkdirs();
        }

        documento.setPathXML(ruta + "/" + nombreArchivo + ".xml");
        documento.setPathPDF(ruta + "/" + nombreArchivo + ".pdf");
        File savefile = new File(savedir, nombreArchivo + ".xml");
        Archivo.guardarXML(savefile, archivo);
        LOGGER.log(Level.INFO, "RUTA GUARDADA XML: {0}", documento.getPathXML());
    }

    @Override
    public void cabecera() {
        System.out.println("=============INICIO CABECERA===========");
        String[] serieYCorrelativo = getNumeroDocumento();
        cabecera.setTipoDocumento(Integer.parseInt(getTipoDocumento()));
        cabecera.setSerieDocumento(serieYCorrelativo[0]);
        cabecera.setCorrelativoDocumento(serieYCorrelativo[1]);
        cabecera.setCodTipoMoneda(getTipoMoneda());
        cabecera.setFechEmision(getFechaEmision());
        System.out.println("=============FIN CABECERA===========");
    }

    @Override
    public void emisor() {
        cabecera.setNroDocumentoEmis(getNumeroDocumentoEmisor());
        cabecera.setCodTipoDocEmis(Integer.parseInt(getTipoDocumentoEmisor()));
        cabecera.setNombreEmis(getNombreEmisor());

    }

    @Override
    public void receptor() {
        cabecera.setNroDocumentoRecep(getNumeroDocumentoReceptor());
        cabecera.setCodTipoDocRecep(Integer.parseInt(getTipoDocumentoReceptor()));
        cabecera.setNombreRecep(getNombreReceptor());
    }

    @Override
    public void detalle() {
        listaDetalle = new ArrayList<>();
        int lista = obtenerCantidadProductos();
        for (int i = 0; i < lista; i++) {
            generarDetalle(i);
            detalle.setItemProducto(Integer.parseInt(getNumeroItem()));
            detalle.setCodProducto(getcodigoProducto());
            detalle.setDescProducto(getDescripcionProducto());
            detalle.setCantProducto(Double.parseDouble(getCantidadProducto()));
            detalle.setPrecioUnitarioProducto(Double.parseDouble(getPrecioProducto()));
            detalle.setValorVentaProducto(Double.parseDouble(getValorVenta()));
            listaDetalle.add(detalle);
        }
    }

    @Override
    public void total() {
        obtenerTipoOperacion(getTipoTotal(), getValorSubTotal());
        total.setTotalIGV(Double.parseDouble(getTotalIGV()));
        total.setTotalVenta(Double.parseDouble(getTotalVenta()));
    }

    public void obtenerTipoOperacion(String tipo, String valorSubtotal) {
        switch (tipo) {
            case "1001":
                total.setTotalOG(Double.parseDouble(valorSubtotal));
                break;
            case "1002":
                total.setTotalOI(Double.parseDouble(valorSubtotal));
                break;
            case "1003":
                total.setTotalOE(Double.parseDouble(valorSubtotal));
                break;
            case "1004":
                total.setTotalOGR(Double.parseDouble(valorSubtotal));
                break;
            case "1005":
                total.setTotalSV(Double.parseDouble(valorSubtotal));
                break;
            default:  
        }
    }

    public void registrarErrorLog() {
        LOGGER.log(Level.WARNING, LectorEmail.email);
        LOGGER.log(Level.WARNING, LectorEmail.asunto);
        LOGGER.log(Level.WARNING, LectorEmail.fecha);
        LOGGER.log(Level.WARNING, "*********************");
    }
}
