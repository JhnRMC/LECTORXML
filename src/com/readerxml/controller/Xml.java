package com.readerxml.controller;

import com.readerxml.bean.ErrorEtiquetas;
import com.readerxml.util.Etiqueta;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class Xml {

    private Element elemento;
    private String etiqueta;
    private Document xml;
    protected String ETIQUETA_GLOBAL = "";
    protected String version = "";
    protected String tipoDocumento = "";
    protected String numeroDocumento = Etiqueta.CBC_ID.obtenerEtiqueta();
    protected String tipoMoneda = Etiqueta.DOCUMENTCURRENCYCODE.obtenerEtiqueta();
    protected String fechaEmision = Etiqueta.CBC_ISSUEDATE.obtenerEtiqueta();
    protected String nodoPrincipalEmisor = Etiqueta.CAC_ACCOUNTINGSUPPLIERPARTY.obtenerEtiqueta();
    protected String nodoEmisor = "";
    protected String numeroDocumentoEmisor = Etiqueta.CBC_CUSTOMERASSIGNEDACCOUNTID.obtenerEtiqueta();
    protected String tipoDocumentoEmisor = "6";
    protected String nombreEmisor = Etiqueta.CBC_REGISTRATIONNAME.obtenerEtiqueta();
    protected String nodoPrincipalReceptor = Etiqueta.CAC_ACCOUNTINGCUSTOMERPARTY.obtenerEtiqueta();
    protected String nodoReceptor = "";
    protected String numeroDocumentoReceptor = Etiqueta.CBC_CUSTOMERASSIGNEDACCOUNTID.obtenerEtiqueta();
    protected String tipoDocumentoReceptor = "6";
    protected String nombreReceptor = Etiqueta.CBC_REGISTRATIONNAME.obtenerEtiqueta();
    protected String nodoDetalle = Etiqueta.CAC_INVOICELINE.obtenerEtiqueta();
    protected String codigoitem = Etiqueta.CBC_ID.obtenerEtiqueta();
    protected String nodoDescripcion = Etiqueta.CAC_ITEM.obtenerEtiqueta();
    protected String nodoCodigoProducto = Etiqueta.CAC_SELLERSITEMIDENTIFICATION.obtenerEtiqueta();
    protected String codigoProducto = Etiqueta.CBC_ID.obtenerEtiqueta();
    protected String descripcionProducto = Etiqueta.CBC_DESCRIPTION.obtenerEtiqueta();
    protected String cantidadProducto = Etiqueta.CBC_INVOICEDQUANTITY.obtenerEtiqueta();
    protected String nodoPrecioProducto = Etiqueta.CAC_PRICE.obtenerEtiqueta();
    protected String precioProducto = Etiqueta.CBC_PRICEAMOUNT.obtenerEtiqueta();
    protected String valorVenta = Etiqueta.CBC_LINEEXTENSIONAMOUNT.obtenerEtiqueta();
    protected String nodoPrincipalSubTotal = Etiqueta.EXT_UBLEXTENSIONS.obtenerEtiqueta();
    protected String nodoSubTotal = Etiqueta.SAC_ADDITIONALMONETARYTOTAL.obtenerEtiqueta();
    protected String tipoTotal = Etiqueta.CBC_ID.obtenerEtiqueta();
    protected String valorSubTotal = Etiqueta.CBC_PAYABLEAMOUNT.obtenerEtiqueta();
    protected String nodoTotalIGV = Etiqueta.CAC_TAXTOTAL.obtenerEtiqueta();
    protected String totalIGV = Etiqueta.CBC_TAXAMOUNT.obtenerEtiqueta();
    protected String nodoTotalVenta = Etiqueta.CAC_LEGALMONETARYTOTAL.obtenerEtiqueta();
    protected String totalVenta = Etiqueta.CBC_PAYABLEAMOUNT.obtenerEtiqueta();
    public static boolean estado = false;
    public static ErrorEtiquetas errorEtiquetas;
    public static int ERROR_AVISO = 0;
    public static int ERROR_ETIQUETA = 1;
    private final static Logger LOGGER = Logger.getLogger("com.readerxml.controller.Xml");

    protected void iniciar(Document xml) throws NullPointerException {
        this.xml = xml;
        this.xml.getDocumentElement().normalize();
        ETIQUETA_GLOBAL = xml.getDocumentElement().getTagName();
        obtenerVersion();
        generarEtiquetas();

    }

    protected void obtenerVersion() {
        try {
            version = etiqueta(Etiqueta.CBC_UBLVERSIONID.obtenerEtiqueta()).obtener();
        } catch (NullPointerException Null_Version) {
            Xml.estado = false;
            LOGGER.log(Level.WARNING, "EL XML NO CUENTA CON ETIQUETA DE VERSION( cbc:UBLVersionID )");
        }
    }

    private void generarEtiquetas() {

        switch (ETIQUETA_GLOBAL) {
            case "Invoice": {
                System.out.println("DOCUMENTO: " + ETIQUETA_GLOBAL + "(" + " FACTURA " + ")");
                tipoDocumento = "01";
                nodoDetalle = Etiqueta.CAC_INVOICELINE.obtenerEtiqueta();
                cantidadProducto = Etiqueta.CBC_INVOICEDQUANTITY.obtenerEtiqueta();
                nodoCodigoProducto = Etiqueta.CAC_SELLERSITEMIDENTIFICATION.obtenerEtiqueta();
                if (version.equals("2.1")) {
                    facturaV2();
                } else {
                    facturaV1();
                }
                break;
            }
            case "CreditNote": {
                System.out.println("DOCUMENTO: " + ETIQUETA_GLOBAL + "(" + " NOTA-CREDITO " + ")");
                tipoDocumento = "07";
                nodoDetalle = Etiqueta.CAC_CREDITNOTELINE.obtenerEtiqueta();
                numeroDocumentoReceptor = Etiqueta.CBC_ID.obtenerEtiqueta();
                cantidadProducto = Etiqueta.CAC_CREDITEDQUANTITY.obtenerEtiqueta();
                if (version.equals("2.1")) {
                    notaCreditoV2();
                } else {
                    notaCreditoV1();
                }
                break;
            }
            case "DebitNote": {
                System.out.println("DOCUMENTO: " + ETIQUETA_GLOBAL + "(" + " NOTA-DEBITO " + ")");
                nodoPrecioProducto = "";
                tipoDocumento = "08";
                nodoDetalle = Etiqueta.CAC_DEBITNOTELINE.obtenerEtiqueta();
                cantidadProducto = Etiqueta.CAC_DEBITEDQUANTITY.obtenerEtiqueta();
                nodoCodigoProducto = "";
                if (version.equals("2.1")) {
                    notaDebitoV2();
                } else {
                    notaDebitoV1();
                }
                break;
            }
            default:
                Xml.estado = false;
                LOGGER.log(Level.SEVERE, "ERROR DE ETIQUETA PRINCIPAL: {0}", ETIQUETA_GLOBAL);
                errorEtiquetas.setETIQUETA_GLOBAL(ETIQUETA_GLOBAL);
        }
    }

    private void facturaV1() {
        System.out.println("VERSION 2.0");
        nodoEmisor = "";
        numeroDocumentoEmisor = Etiqueta.CBC_CUSTOMERASSIGNEDACCOUNTID.obtenerEtiqueta();
        nodoReceptor = "";
        numeroDocumentoReceptor = Etiqueta.CBC_CUSTOMERASSIGNEDACCOUNTID.obtenerEtiqueta();
        nodoDescripcion = Etiqueta.CAC_ITEM.obtenerEtiqueta();
        nodoPrecioProducto = Etiqueta.CAC_PRICE.obtenerEtiqueta();
        precioProducto = Etiqueta.CBC_PRICEAMOUNT.obtenerEtiqueta();
        nodoSubTotal = Etiqueta.SAC_ADDITIONALMONETARYTOTAL.obtenerEtiqueta();
        tipoTotal = Etiqueta.CBC_ID.obtenerEtiqueta();
        valorSubTotal = Etiqueta.CBC_PAYABLEAMOUNT.obtenerEtiqueta();
        nodoTotalVenta = Etiqueta.CAC_LEGALMONETARYTOTAL.obtenerEtiqueta();
    }

    private void facturaV2() {
        System.out.println("VERSION 2.1");
        nodoEmisor = Etiqueta.CAC_PARTYIDENTIFICATION.obtenerEtiqueta();
        numeroDocumentoEmisor = Etiqueta.CBC_ID.obtenerEtiqueta();
        nodoReceptor = Etiqueta.CAC_PARTYIDENTIFICATION.obtenerEtiqueta();
        numeroDocumentoReceptor = Etiqueta.CBC_ID.obtenerEtiqueta();
    }

    private void notaCreditoV1() {
        System.out.println("VERSION 2.0");
        nodoEmisor = "";
        numeroDocumentoEmisor = Etiqueta.CBC_CUSTOMERASSIGNEDACCOUNTID.obtenerEtiqueta();
        nodoReceptor = Etiqueta.CBC_CUSTOMERASSIGNEDACCOUNTID.obtenerEtiqueta();
        nodoCodigoProducto = "";
        nodoPrecioProducto = "";
    }

    private void notaCreditoV2() {
        System.out.println("VERSION 2.1");
        nodoEmisor = Etiqueta.CAC_PARTYIDENTIFICATION.obtenerEtiqueta();
        numeroDocumentoEmisor = Etiqueta.CBC_ID.obtenerEtiqueta();
        nodoReceptor = Etiqueta.CAC_PARTYIDENTIFICATION.obtenerEtiqueta();
        nodoCodigoProducto = "";
    }

    private void notaDebitoV1() {
        System.out.println("VERSION 2.0");
        nodoEmisor = "";
        numeroDocumentoEmisor = Etiqueta.CBC_CUSTOMERASSIGNEDACCOUNTID.obtenerEtiqueta();
        numeroDocumentoReceptor = Etiqueta.CBC_CUSTOMERASSIGNEDACCOUNTID.obtenerEtiqueta();
        nodoReceptor = Etiqueta.CBC_CUSTOMERASSIGNEDACCOUNTID.obtenerEtiqueta();
        nodoSubTotal = "";
        nodoTotalVenta = "";
    }

    private void notaDebitoV2() {
        System.out.println("VERSION 2.1");
        nodoEmisor = Etiqueta.CAC_PARTYIDENTIFICATION.obtenerEtiqueta();
        numeroDocumentoEmisor = Etiqueta.CBC_ID.obtenerEtiqueta();
        numeroDocumentoReceptor = Etiqueta.CBC_ID.obtenerEtiqueta();
        nodoReceptor = Etiqueta.CAC_PARTYIDENTIFICATION.obtenerEtiqueta();
        nodoSubTotal = "";
        nodoTotalVenta = "";
        tipoTotal = Etiqueta.CBC_ID.obtenerEtiqueta();
        valorSubTotal = Etiqueta.CBC_PAYABLEAMOUNT.obtenerEtiqueta();
    }

    private Xml nodo() {
        Node nodeList = xml.getElementsByTagName(ETIQUETA_GLOBAL).item(0);
        elemento = (Element) nodeList;
        return this;
    }

    private Xml elemento(String etiqueta) {
        if (!etiqueta.equals("")) {
            Node nodeList = elemento.getElementsByTagName(etiqueta).item(0);
            elemento = (Element) nodeList;
        }
        return this;
    }

    private String etiquetaAnidada(String etiqueta) {
        return elemento.getElementsByTagName(etiqueta).item(0).getTextContent();
    }

    private Xml etiqueta(String etq) {
        etiqueta = xml.getElementsByTagName(etq).item(0).getTextContent();
        return this;
    }

    private String obtener() {
        return etiqueta;
    }

    protected String getTipoDocumento() {
        String value = tipoDocumento;
        System.out.println("TIPO DE DOCUMENTO: " + value);
        return value;
    }

    protected String[] getNumeroDocumento() {
        
        NodeList lista = xml.getElementsByTagName(numeroDocumento);
        for (int i = 0; i < lista.getLength(); i++) {
            String numDocumento = xml.getElementsByTagName(numeroDocumento).item(i).getTextContent();
            String[] validator = numDocumento.split("-");
            if (validator.length == 2) {
                System.out.println("SERIE Y CORRELATIVO: " + validator[0] + "-" + validator[1]);
                return validator;
            }
        }
        Xml.estado = false;
        LOGGER.log(Level.WARNING, "ERROR EN LA ETIQUETA DE NUMERO DE DOCUMENTO: {0}", numeroDocumento);
        errorEtiquetas.setNumeroDocumento(numeroDocumento);
        return new String[]{"NO_FOUND", "NO_FOUND"};
    }

    protected String getTipoMoneda() {
        try {
            return etiqueta(tipoMoneda).obtener();
        } catch (NullPointerException Null_Tipo_Moneda) {
            Xml.estado = false;
            errorEtiquetas.setTipoMoneda(tipoMoneda);
            LOGGER.log(Level.WARNING, "EL XML NO CUENTA CON LA ETIQUETA TIPO DE MONEDA ( cbc:DocumentCurrencyCode )");
        }
        return null;
    }

    protected String getFechaEmision() {
        String value;
        try {
            value = etiqueta(fechaEmision).obtener();
            System.out.println("FECHA DE EMISION: " + value);
            return value;
        } catch (NullPointerException Null_Tipo_Moneda) {
            Xml.estado = false;
            errorEtiquetas.setFechaEmision(fechaEmision);
            LOGGER.log(Level.WARNING, "EL XML NO CUENTA CON LA ETIQUETA FECHA DE EMISION ( cbc:IssueDate )");
        }
        return null;
    }

    protected String getNumeroDocumentoEmisor() {
        try {
            return nodo().elemento(nodoEmisor).etiquetaAnidada(numeroDocumentoEmisor);
        } catch (NullPointerException Null_Tipo_Moneda) {
            Xml.estado = false;
            errorEtiquetas.setNumeroDocumentoEmisor(numeroDocumentoEmisor);
            LOGGER.log(Level.WARNING, "EL XML NO CUENTA CON LA ETIQUETA '<NUMERO DE DOCUMENTO EMISOR>'.");
        }
        return null;
    }

    protected String getTipoDocumentoEmisor() {
        try {
            return "6";

        } catch (NullPointerException Null_Tipo_Moneda) {
            Xml.estado = false;
            LOGGER.log(Level.WARNING, "EL XML NO CUENTA CON LA ETIQUETA '<TIPO DE DOCUMENTO EMISOR>'");
        }
        return null;
    }

    protected String getNombreEmisor() {
        try {
            return nodo().elemento(nodoPrincipalEmisor).etiqueta(nombreEmisor).obtener();
        } catch (NullPointerException Null_Tipo_Moneda) {
            Xml.estado = false;
            errorEtiquetas.setNombreEmisor(nombreEmisor);
            LOGGER.log(Level.WARNING, "EL XML NO CUENTA CON LA ETIQUETA '<cbc:RegistrationName>'");
        }
        return null;
    }

    protected String getNumeroDocumentoReceptor() {
        try {
            return nodo().elemento(nodoPrincipalReceptor).elemento(nodoReceptor).etiquetaAnidada(numeroDocumentoReceptor);
        } catch (NullPointerException Null_Tipo_Moneda) {
            Xml.estado = false;
            errorEtiquetas.setNumeroDocumentoReceptor(numeroDocumentoReceptor);
            LOGGER.log(Level.WARNING, "EL XML NO CUENTA CON LA ETIQUETA '<NUMERO DE DOCUMENTO RECEPTOR>'");
        }
        return null;
    }

    protected String getTipoDocumentoReceptor() {
        try {
            return "6";
        } catch (NullPointerException Null_Tipo_Moneda) {
            Xml.estado = false;
            LOGGER.log(Level.WARNING, "EL XML NO CUENTA CON LA ETIQUETA '<TIPO DE DOCUMENTO RECEPTOR>'");
        }
        return null;
    }

    protected String getNombreReceptor() {
        try {
            return nodo().elemento(nodoPrincipalReceptor).etiquetaAnidada(nombreReceptor);
        } catch (NullPointerException Null_Tipo_Moneda) {
            Xml.estado = false;
            errorEtiquetas.setNombreReceptor(nombreReceptor);
            LOGGER.log(Level.WARNING, "EL XML NO CUENTA CON LA ETIQUETA '<NOMBRE DEL RECEPTOR>'");
        }
        return null;
    }

    protected int obtenerCantidadProductos() {
        NodeList detalle = xml.getElementsByTagName(nodoDetalle);
        return detalle.getLength();
    }

    protected void generarDetalle(int index) {
        NodeList detalle = xml.getElementsByTagName(nodoDetalle); // Detalles
        Node nodoDet = detalle.item(index);
        elemento = (Element) nodoDet;
    }

    protected String getNumeroItem() {
        try {
            return elemento.getElementsByTagName(codigoitem).item(0).getTextContent();
        } catch (NullPointerException Null_Tipo_Moneda) {
            errorEtiquetas.setCodigoitem(codigoitem);
            LOGGER.log(Level.WARNING, "EL XML NO CUENTA CON LA ETIQUETA '<NUMERO ITEM>'");
        }
        return null;
    }

    protected String getcodigoProducto() {
        try {
            return (tipoDocumento.equals("01") ? ((Element) elemento.getElementsByTagName(nodoCodigoProducto).item(0)) : elemento).getElementsByTagName(codigoProducto).item(0).getTextContent();
        } catch (NullPointerException Null_Tipo_Moneda) {
            //Xml.estado = false;
            errorEtiquetas.setCodigoProducto(codigoProducto);
            //LOGGER.log(Level.INFO, "EL XML NO CUENTA CON LA ETIQUETA '<CODIGO PRODUCTO>'");
        }
        return null;
    }

    protected String getDescripcionProducto() {
        try {
            return elemento.getElementsByTagName(descripcionProducto).item(0).getTextContent();
        } catch (NullPointerException Null_Tipo_Moneda) {
            //Xml.estado = false;
            errorEtiquetas.setDescripcionProducto(descripcionProducto);
            //LOGGER.log(Level.INFO, "EL XML NO CUENTA CON LA ETIQUETA '<DESCRIPCION>'");
        }
        return null;
    }

    protected String getCantidadProducto() {
        try {
            return elemento.getElementsByTagName(cantidadProducto).item(0).getTextContent();
        } catch (NullPointerException Null_Tipo_Moneda) {
            Xml.estado = false;
            errorEtiquetas.setCantidadProducto(cantidadProducto);
            LOGGER.log(Level.WARNING, "EL XML NO CUENTA CON LA ETIQUETA '<CANTIDAD>'");
        }
        return null;
    }

    protected String getPrecioProducto() {
        try {
            return (tipoDocumento.equals("01") ? ((Element) elemento.getElementsByTagName(nodoPrecioProducto).item(0)) : elemento).getElementsByTagName(precioProducto).item(0).getTextContent();            
        } catch (NullPointerException Null_Tipo_Moneda) {
            Xml.estado = false;
            errorEtiquetas.setPrecioProducto(precioProducto);
            LOGGER.log(Level.WARNING, "EL XML NO CUENTA CON LA ETIQUETA '<PRECIO>'");
        }
        return null;
    }

    protected String getValorVenta() {
        try {
            return elemento.getElementsByTagName(valorVenta).item(0).getTextContent();
        } catch (NullPointerException Null_Tipo_Moneda) {
            Xml.estado = false;
            errorEtiquetas.setValorVenta(valorVenta);
            LOGGER.log(Level.WARNING, "EL XML NO CUENTA CON LA ETIQUETA '<VALOR DE VENTA>'");
        }
        return null;
    }

    protected String getTipoTotal() {
        try {
            return nodo().elemento(nodoPrincipalSubTotal).elemento(nodoSubTotal).etiquetaAnidada(tipoTotal);
        } catch (NullPointerException nullEx) {
            return "";
        }
    }

    protected String getValorSubTotal() {
        try {
            return nodo().elemento(nodoPrincipalSubTotal).elemento(nodoSubTotal).etiquetaAnidada(valorSubTotal);
        } catch (NullPointerException nullEx) {
            return "";
        }
    }

    protected String getTotalIGV() {
        try {
            return nodo().elemento(nodoTotalIGV).etiquetaAnidada(totalIGV);
        } catch (NullPointerException Null_Tipo_Moneda) {
            Xml.estado = false;
            errorEtiquetas.setTotalIGV(totalIGV);
            LOGGER.log(Level.WARNING, "EL XML NO CUENTA CON LA ETIQUETA '<TOTAL DE I.G.V>'");
        }
        return "0";
    }

    protected String getTotalVenta() {
        try {
            return nodo().elemento(nodoTotalVenta).etiquetaAnidada(totalVenta);
        } catch (NullPointerException Null_Tipo_Moneda) {
            Xml.estado = false;
            errorEtiquetas.setTotalVenta(totalVenta);
            LOGGER.log(Level.WARNING, "EL XML NO CUENTA CON LA ETIQUETA '<TOTAL DE VENTA>'");
        }
        return "0";
    }

    public abstract void cabecera();

    public abstract void emisor();

    public abstract void receptor();

    public abstract void detalle();

    public abstract void total();

    public static ErrorEtiquetas getErrorEtiquetas() {
        errorEtiquetas = new ErrorEtiquetas();
        return errorEtiquetas;
    }
}
