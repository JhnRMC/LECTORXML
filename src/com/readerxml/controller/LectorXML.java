package com.readerxml.controller;

import com.readerxml.dao.SendEmailSqliteDAO;
import com.readerxml.dao.DocumentoElectronicoDAO;
import com.readerxml.bean.Documento;
import com.readerxml.bean.Detalle;
import com.readerxml.bean.Cabecera;
import javax.mail.Part;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.readerxml.LectorEmail;
import com.readerxml.bean.EmailSend;
import com.readerxml.bean.Total;
import com.readerxml.bean.ErrorEtiquetas;
import com.readerxml.util.Etiqueta;
import java.util.HashMap;

public class LectorXML extends Xml implements Xml.Callback {

    private final static Logger LOGGER = Logger.getLogger(LectorXML.class.getName());
    public Documento documento;
    Cabecera cabecera;
    Detalle detalle;
    ArrayList<Detalle> listaDetalles;
    Total total;
    ErrorEtiquetas etiquetaErradas;
    HashMap<Integer, String> etiquetasErradas = new HashMap();
    private boolean existe = true;

    public LectorXML() {
        etiquetaErradas = new ErrorEtiquetas();
    }

    public void iniciarLectura(Part archivo) {
        System.out.println("=============LECTURA DEL XML===========");
        try {           
            etiquetaValida = true;
            super.iniciar(archivo, this);
            cabecera();
            emisor();
            receptor();
            detalle();
            total();
        }catch (NumberFormatException errorFormat) {
            LOGGER.log(Level.SEVERE, "NO SE ENCONTRO LA ETIQUETA: Invoice, CreditNote o DebitNote, QUE IDENTIFICA UN DOCUMENTO VALIDO.({0})", etiquetaGlobal);
        } finally {
            guardarDocumentoElectronico(archivo);
        }

    }

    public void guardarDocumentoElectronico(Part archivo) {
        generarPath(archivo);
        DocumentoElectronicoDAO documentoElectronicoDAO = new DocumentoElectronicoDAO();
        if (!isEtiquetaValida()) {
            registrarError(Xml.ERROR_ETIQUETA);
            registrarErrorLog();
        } else {
            if (!existeDocumento(documentoElectronicoDAO)) {
                System.out.println("REGISTRANDO...");
                existe = false;
                documento.setCabecera(cabecera);
                documento.setTotal(total);
                documento.setDetallesDocumento(listaDetalles);
                documentoElectronicoDAO.insertDocumentoElectronico(documento);
            } else {
                existe = true;
                LOGGER.log(Level.INFO, "DOCUMENTO EXISTENTE EN LA BD");
            }
        }
    }

    public boolean existeDocumento(DocumentoElectronicoDAO documentoElectronicoDAO) {
        return documentoElectronicoDAO.verificarDocumentoExistente(
                cabecera.getSerieDocumento(), 
                cabecera.getCorrelativoDocumento(), 
                cabecera.getNroDocumentoEmis(), 
                cabecera.getTipoDocumento()
        );
    }

    public void registrarError(int tipoError) {
        EmailSend emailSend = new EmailSend();
        emailSend.setAsunto("REGISTRO DE ERRORES");
        emailSend.setNombreArchivo(LectorEmail.flag + ".xml");
        emailSend.setRutaArchivo(documento.getPathXML());
        emailSend.setDestino(LectorEmail.email);
        emailSend.setTipoMensaje(tipoError);
        SendEmailSqliteDAO dao = new SendEmailSqliteDAO();
        LOGGER.log(Level.WARNING, "REGISTRO ERROR: {0}", emailSend.getAsunto());
        /*if (dao.registrarSuccessEnvioCorreo(emailSend, errorEtiquetas)) {
            LOGGER.log(Level.WARNING, "REGISTRO EXITOSO, PARA EL ENVIO DE CORREO");
        } else {
            LOGGER.log(Level.WARNING, "ERROR EN EL REGISTRO, PARA EL ENVIO DE CORREO");
        }*/
    }

    public void registrarAviso(int tipoError) {
        EmailSend emailSend = new EmailSend();
        emailSend.setAsunto("AVISO DE RECHAZO DE CORREO");
        emailSend.setDestino(LectorEmail.email);
        emailSend.setNombreArchivo("");
        emailSend.setRutaArchivo("");
        emailSend.setTipoMensaje(tipoError);
        SendEmailSqliteDAO dao = new SendEmailSqliteDAO();
        LOGGER.log(Level.WARNING, "REGISTRO ERROR: {0}", emailSend.getAsunto());
        /*if (dao.registrarSuccessEnvioCorreo(emailSend, errorEtiquetas)) {
            LOGGER.log(Level.WARNING, "REGISTRO EXITOSO, PARA EL ENVIO DE CORREO");
        } else {
            LOGGER.log(Level.WARNING, "ERROR EN EL REGISTRO, PARA EL ENVIO DE CORREO");
        }*/
    }

    public void generarPath(Part archivo) {
        String ruta;
        String ruta_SO;
        String nombreArchivo = null;
        if (isSOWindow()) {
            ruta_SO = "c:/";
        } else {
            ruta_SO = "/";
        }

        if (!isEtiquetaValida()) {
            ruta = ruta_SO + "home/error";
            nombreArchivo = LectorEmail.flag;
        } else {
            nombreArchivo = cabecera.getNroDocumentoEmis() + "-" + cabecera.getCorrelativoDocumento();
            ruta = ruta_SO + "home/proveedores/TD_" + tipoDocumento + "/" + cabecera.getNroDocumentoEmis() + "/" + cabecera.getSerieDocumento() + "-" + cabecera.getCorrelativoDocumento();
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

    public boolean isSOWindow() {
        String so = System.getProperty("os.name");
        return so.contains("Windows");
    }

    @Override
    public void cabecera() {
        documento = new Documento();
        cabecera = new Cabecera();
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
        listaDetalles = new ArrayList<>();
        int lista = obtenerCantidadProductos();
        for (int i = 0; i < lista; i++) {
            detalle = new Detalle();
            generarDetalle(i);
            detalle.setItemProducto(Integer.parseInt(getNumeroItem()));
            detalle.setCodProducto(getCodigoProducto());
            detalle.setDescProducto(getDescripcionProducto());
            detalle.setCantProducto(Double.parseDouble(getCantidadProducto()));
            detalle.setPrecioUnitarioProducto(Double.parseDouble(getPrecioProducto()));
            detalle.setValorVentaProducto(Double.parseDouble(getValorVenta()));
            listaDetalles.add(detalle);
        }
    }

    @Override
    public void total() {
        total = new Total();
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

    public boolean isExiste() {
        return existe;
    }

    @Override
    public void onFail(Etiqueta etiqueta) {
        etiquetasErradas.put(etiqueta.ordinal(), etiqueta.obtenerEtiqueta());
    }
        
}
