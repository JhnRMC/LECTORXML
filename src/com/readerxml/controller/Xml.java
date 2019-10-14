package com.readerxml.controller;

import com.Log;
import com.readerxml.bean.ErrorEtiquetas;
import com.readerxml.util.Etiqueta;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public abstract class Xml {

    private Element elemento;
    private Document xml;
    protected String etiquetaGlobal = "";
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
    protected String codigoItem = Etiqueta.CBC_ID.obtenerEtiqueta();
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
    protected boolean etiquetaValida;
    private ErrorEtiquetas errorEtiquetas;
    public static int ERROR_AVISO = 0;
    public static int ERROR_ETIQUETA = 1;
    private Xml.Callback callback;
    private XmlBuilder xmlBuilder;
    private final static Logger LOGGER = Logger.getLogger(Xml.class.getName());

    protected void iniciar(Part xml, Xml.Callback callback) {
        this.callback = callback;
        generarDocumento(xml);
        obtenerVersion();
        generarEtiquetas();
    }

    private void generarDocumento(Part xml) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder;
            dBuilder = dbFactory.newDocumentBuilder();
            this.xml = dBuilder.parse(xml.getInputStream());
            this.xml.getDocumentElement().normalize();
            etiquetaGlobal = this.xml.getDocumentElement().getTagName();
            xmlBuilder = new XmlBuilder(this.xml);
            xmlBuilder.setEtiquetaGlobal(etiquetaGlobal);
        } catch (ParserConfigurationException | SAXException | MessagingException ex) {
            LOGGER.log(Level.SEVERE, "LECTURA NO PERMITIDA: {0}", Log.getStackTrace(ex));
            etiquetaValida = false;
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "ERROR CON EL ARCHIVO");
            etiquetaValida = false;
        }
    }

    protected void obtenerVersion() {
        try {
            version = xmlBuilder.etiqueta(Etiqueta.CBC_UBLVERSIONID.obtenerEtiqueta()).builder();
        } catch (NullPointerException Null_Version) {
            etiquetaValida = false;
            LOGGER.log(Level.WARNING, "EL XML NO CUENTA CON ETIQUETA DE VERSION( cbc:UBLVersionID )");
        }
    }

    private void generarEtiquetas() {

        switch (etiquetaGlobal) {
            case "Invoice": {
                System.out.println("DOCUMENTO: " + etiquetaGlobal + "(" + " FACTURA " + ")");
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
                System.out.println("DOCUMENTO: " + etiquetaGlobal + "(" + " NOTA-CREDITO " + ")");
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
                System.out.println("DOCUMENTO: " + etiquetaGlobal + "(" + " NOTA-DEBITO " + ")");
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
                etiquetaValida = false;
                LOGGER.log(Level.SEVERE, "ERROR DE ETIQUETA PRINCIPAL: {0}", etiquetaGlobal);
                callback.onFail(Etiqueta.INVOICE);
                //errorEtiquetas.setEtiquetaGlobal(etiquetaGlobal);
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

    protected String getTipoDocumento() {
        String value = tipoDocumento;
        System.out.println("TIPO DE DOCUMENTO: " + value);
        return value;
    }

    protected String[] getNumeroDocumento() {

        NodeList lista = xml.getElementsByTagName(numeroDocumento);

        for (int i = 0; i < lista.getLength(); i++) {
            String[] validator = null;
            String numDocumento = xml.getElementsByTagName(numeroDocumento).item(i).getTextContent();
            validator = numDocumento.split("-");
            if (validator.length == 2) {
                System.out.println("SERIE Y CORRELATIVO: " + validator[0] + "-" + validator[1]);
                return validator;
            }
        }
        etiquetaValida = false;
        LOGGER.log(Level.WARNING, "ERROR EN LA ETIQUETA DE NUMERO DE DOCUMENTO: {0}", numeroDocumento);
        callback.onFail(Etiqueta.CBC_ID);
        //errorEtiquetas.setNumeroDocumento(numeroDocumento);
        return new String[]{"NO_FOUND", "NO_FOUND"};

    }

    protected String getTipoMoneda() {
        try {
            return xmlBuilder.etiqueta(tipoMoneda).builder();
        } catch (NullPointerException tipo_moneda) {
            etiquetaValida = false;
            callback.onFail(Etiqueta.DOCUMENTCURRENCYCODE);
            //errorEtiquetas.setTipoMoneda(tipoMoneda);
            LOGGER.log(Level.WARNING, "EL XML NO CUENTA CON LA ETIQUETA TIPO DE MONEDA ( cbc:DocumentCurrencyCode )");
        }
        return null;
    }

    protected String getFechaEmision() {
        String value;
        try {
            value = xmlBuilder
                    .etiqueta(fechaEmision)
                    .builder();
            System.out.println("FECHA DE EMISION: " + value);
            return value;
        } catch (NullPointerException fecha_emision) {
            etiquetaValida = false;
            callback.onFail(Etiqueta.CBC_ISSUEDATE);
            //errorEtiquetas.setFechaEmision(fechaEmision);
            LOGGER.log(Level.WARNING, "EL XML NO CUENTA CON LA ETIQUETA FECHA DE EMISION ( cbc:IssueDate )");
        }
        return null;
    }

    protected String getNumeroDocumentoEmisor() {
        try {
            return xmlBuilder.nodo()
                    .elemento(nodoPrincipalEmisor)
                    .elemento(nodoEmisor)
                    .etiquetaAnidada(numeroDocumentoEmisor);
        } catch (NullPointerException documento_emisor) {
            etiquetaValida = false;
            callback.onFail(Etiqueta.CBC_CUSTOMERASSIGNEDACCOUNTID);
            //errorEtiquetas.setNumeroDocumentoEmisor(numeroDocumentoEmisor);
            LOGGER.log(Level.WARNING, "EL XML NO CUENTA CON LA ETIQUETA '<NUMERO DE DOCUMENTO EMISOR>'.");
        }
        return null;
    }

    protected String getTipoDocumentoEmisor() {
        try {
            return "6";

        } catch (NullPointerException tipo_documento) {
            etiquetaValida = false;
            LOGGER.log(Level.WARNING, "EL XML NO CUENTA CON LA ETIQUETA '<TIPO DE DOCUMENTO EMISOR>'");
        }
        return null;
    }

    protected String getNombreEmisor() {
        try {
            return xmlBuilder.nodo()
                    .elemento(nodoPrincipalEmisor)
                    .etiqueta(nombreEmisor).builder();
        } catch (NullPointerException nombre_emisor) {
            //etiquetaValida = false;
            callback.onFail(Etiqueta.CBC_REGISTRATIONNAME);
            //errorEtiquetas.setNombreEmisor(nombreEmisor);
            LOGGER.log(Level.WARNING, "EL XML NO CUENTA CON LA ETIQUETA '<cbc:RegistrationName>'");
        }
        return null;
    }

    protected String getNumeroDocumentoReceptor() {
        try {
            return xmlBuilder.nodo().elemento(nodoPrincipalReceptor)
                    .elemento(nodoReceptor)
                    .etiquetaAnidada(numeroDocumentoReceptor);
        } catch (NullPointerException Null_Tipo_Moneda) {
            etiquetaValida = false;
            callback.onFail(Etiqueta.CBC_CUSTOMERASSIGNEDACCOUNTID);
            //errorEtiquetas.setNumeroDocumentoReceptor(numeroDocumentoReceptor);
            LOGGER.log(Level.WARNING, "EL XML NO CUENTA CON LA ETIQUETA '<NUMERO DE DOCUMENTO RECEPTOR>'");
        }
        return null;
    }

    protected String getTipoDocumentoReceptor() {
        try {
            return "6";
        } catch (NullPointerException Null_Tipo_Moneda) {
            etiquetaValida = false;
            LOGGER.log(Level.WARNING, "EL XML NO CUENTA CON LA ETIQUETA '<TIPO DE DOCUMENTO RECEPTOR>'");
        }
        return null;
    }

    protected String getNombreReceptor() {
        try {
            return xmlBuilder.nodo()
                    .elemento(nodoPrincipalReceptor)
                    .etiquetaAnidada(nombreReceptor);
        } catch (NullPointerException Null_Tipo_Moneda) {
            //etiquetaValida = false;
            callback.onFail(Etiqueta.CBC_REGISTRATIONNAME);
            //errorEtiquetas.setNombreReceptor(nombreReceptor);
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
            return elemento.getElementsByTagName(codigoItem).item(0).getTextContent();
        } catch (NullPointerException Null_Tipo_Moneda) {
            callback.onFail(Etiqueta.CBC_ID);
            //errorEtiquetas.setCodigoitem(codigoItem);
            LOGGER.log(Level.WARNING, "EL XML NO CUENTA CON LA ETIQUETA '<NUMERO ITEM>'");
        }
        return null;
    }

    protected String getCodigoProducto() {
        try {
            return (tipoDocumento.equals("01") ? ((Element) elemento.getElementsByTagName(nodoCodigoProducto).item(0)) : elemento).getElementsByTagName(codigoProducto).item(0).getTextContent();
        } catch (NullPointerException Null_Tipo_Moneda) {
            //Xml.estado = false;
            callback.onFail(Etiqueta.CBC_ID);
            //errorEtiquetas.setCodigoProducto(codigoProducto);
            LOGGER.log(Level.INFO, "EL XML NO CUENTA CON LA ETIQUETA '<CODIGO PRODUCTO>'");
        }
        return null;
    }

    protected String getDescripcionProducto() {
        try {
            return elemento.getElementsByTagName(descripcionProducto).item(0).getTextContent();
        } catch (NullPointerException Null_Tipo_Moneda) {
            //Xml.estado = false;
            callback.onFail(Etiqueta.CBC_DESCRIPTION);
            //errorEtiquetas.setDescripcionProducto(descripcionProducto);
            //LOGGER.log(Level.INFO, "EL XML NO CUENTA CON LA ETIQUETA '<DESCRIPCION>'");
        }
        return null;
    }

    protected String getCantidadProducto() {
        try {
            return elemento.getElementsByTagName(cantidadProducto).item(0).getTextContent();
        } catch (NullPointerException Null_Tipo_Moneda) {
            etiquetaValida = false;
            callback.onFail(Etiqueta.CBC_INVOICEDQUANTITY);
            //errorEtiquetas.setCantidadProducto(cantidadProducto);
            LOGGER.log(Level.WARNING, "EL XML NO CUENTA CON LA ETIQUETA '<CANTIDAD>'");
        }
        return null;
    }

    protected String getPrecioProducto() {
        try {
            return (tipoDocumento.equals("01") ? ((Element) elemento.getElementsByTagName(nodoPrecioProducto).item(0)) : elemento).getElementsByTagName(precioProducto).item(0).getTextContent();
        } catch (NullPointerException Null_Tipo_Moneda) {
            etiquetaValida = false;
            callback.onFail(Etiqueta.CBC_PRICEAMOUNT);
            //errorEtiquetas.setPrecioProducto(precioProducto);
            LOGGER.log(Level.WARNING, "EL XML NO CUENTA CON LA ETIQUETA '<PRECIO>'");
        }
        return "0.0";
    }

    protected String getValorVenta() {
        try {
            return elemento.getElementsByTagName(valorVenta).item(0).getTextContent();
        } catch (NullPointerException Null_Tipo_Moneda) {
            etiquetaValida = false;
            callback.onFail(Etiqueta.CBC_LINEEXTENSIONAMOUNT);
            //errorEtiquetas.setValorVenta(valorVenta);
            LOGGER.log(Level.WARNING, "EL XML NO CUENTA CON LA ETIQUETA '<VALOR DE VENTA>'");
        }
        return "0.0";
    }

    protected String getTipoTotal() {
        try {
            return xmlBuilder.nodo().elemento(nodoPrincipalSubTotal).elemento(nodoSubTotal).etiquetaAnidada(tipoTotal);
        } catch (NullPointerException nullEx) {
            return "";
        }
    }

    protected String getValorSubTotal() {
        try {
            return xmlBuilder.nodo().elemento(nodoPrincipalSubTotal).elemento(nodoSubTotal).etiquetaAnidada(valorSubTotal);
        } catch (NullPointerException nullEx) {
            return "";
        }
    }

    protected String getTotalIGV() {
        try {
            return xmlBuilder.nodo().elemento(nodoTotalIGV).etiquetaAnidada(totalIGV);
        } catch (NullPointerException Null_Tipo_Moneda) {
            etiquetaValida = false;
            callback.onFail(Etiqueta.CBC_TAXAMOUNT);
            //errorEtiquetas.setTotalIGV(totalIGV);
            LOGGER.log(Level.WARNING, "EL XML NO CUENTA CON LA ETIQUETA '<TOTAL DE I.G.V>'");
        }
        return "0";
    }

    protected String getTotalVenta() {
        try {
            return xmlBuilder.nodo().elemento(nodoTotalVenta).etiquetaAnidada(totalVenta);
        } catch (NullPointerException Null_Tipo_Moneda) {
            etiquetaValida = false;
            callback.onFail(Etiqueta.CBC_PAYABLEAMOUNT);
            //errorEtiquetas.setTotalVenta(totalVenta);
            LOGGER.log(Level.WARNING, "EL XML NO CUENTA CON LA ETIQUETA '<TOTAL DE VENTA>'");
        }
        return "0";
    }

    public abstract void cabecera();

    public abstract void emisor();

    public abstract void receptor();

    public abstract void detalle();

    public abstract void total();

    public boolean isEtiquetaValida() {
        return etiquetaValida;
    }

    interface Callback {

        void onFail(Etiqueta etiqueta);
    }
}
