package com.readerxml.controller;

import com.readerxml.bean.*;
import com.readerxml.dao.SendEmailSqliteDAO;
import com.readerxml.dao.DocumentoElectronicoDAO;

import javax.mail.Part;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.readerxml.LectorEmail;

public class LectorXML extends Xml {
    private String pathCanonico;
    private final static Logger LOGGER = Logger.getLogger(LectorXML.class.getName());
    public Documento documento;
    Cabecera cabecera;
    Detalle detalle;
    ArrayList<Detalle> listaDetalles;
    Total total;
    //ErrorEtiquetas etiquetaErradas;
    String ruta;
    String nombreArchivo;
    private boolean existe = true;

    public LectorXML() {
    }

    public void iniciarLectura(Part archivo) throws NullPointerException {
        System.out.println("=============LECTURA DEL XML===========");
        try {           
            etiquetaValida = true;
            super.iniciar(archivo);
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

    public void guardarDocumentoElectronico(Part archivo) throws NullPointerException {
        generarPath();
        DocumentoElectronicoDAO documentoElectronicoDAO = new DocumentoElectronicoDAO();
        if (!isEtiquetaValida()) {
            registrarAlertaCorreo(Email.ETIQUETA_ERRADA);
            registrarErrorLog();
        } else {
            if (!existeDocumento(documentoElectronicoDAO)) {
                System.out.println("REGISTRANDO...");
                guardarArchivos(archivo);
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
    public void registrarAlertaCorreo(String tipoAlerta){
        SendEmailSqliteDAO dao = new SendEmailSqliteDAO();
        Email email = new Email();
        Adjunto adjunto = new Adjunto();
        adjunto.setNombre(LectorEmail.flag);
        adjunto.setPath(pathCanonico);
        email.setCorreo(LectorEmail.email);
        email.setAsunto(LectorEmail.asunto);
        email.setFecha(LectorEmail.fecha);
        email.setTipo(tipoAlerta);
        email.setEtiquetaError(getEtiquetaError());
        email.setAdjunto(adjunto);
         if (dao.registrarSuccessEnvioCorreo(email)) {
            LOGGER.log(Level.WARNING, "REGISTRO EXITOSO, PARA EL ENVIO DE CORREO");
        } else {
            LOGGER.log(Level.WARNING, "ERROR EN EL REGISTRO, PARA EL ENVIO DE CORREO");
        }
    }

    public void generarPath() throws NullPointerException{

        String ruta_SO;
        if (isSOWindow()) {
            ruta_SO = "c:/";
        } else {
            ruta_SO = "/";
        }

        if (!isEtiquetaValida()) {
            ruta = ruta_SO + "home/error/";
            nombreArchivo = LectorEmail.flag;
        } else {
            nombreArchivo = cabecera.getNroDocumentoEmis() + "-" + cabecera.getCorrelativoDocumento();
            ruta = ruta_SO + "home/proveedores/TD_" + tipoDocumento + "/" + cabecera.getNroDocumentoEmis() + "/" + cabecera.getSerieDocumento() + "-" + cabecera.getCorrelativoDocumento() + "/";
        }
        pathCanonico = ruta + nombreArchivo;
        documento.setPathXML(pathCanonico + ".xml");
        documento.setPathPDF(pathCanonico + ".pdf");
        LOGGER.log(Level.INFO, "RUTA GUARDADA XML: {0}", documento.getPathXML());
        LOGGER.log(Level.INFO, "RUTA GUARDADA PDF: {0}", documento.getPathPDF());
    }

    public void guardarArchivos(Part archivo){
        File savedir = new File(ruta);
        if (!savedir.exists()) {
            savedir.mkdirs();
        }
        File savefile = new File(savedir, nombreArchivo + ".xml");
        Archivo.guardarXML(savefile, archivo);
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

    public String getPathCanonico() {
        return pathCanonico;
    }
}
