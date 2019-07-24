package com.readerxml.bean;

public class Cabecera {
    //Cabecera
    private int tipoDocumento;
    private String serieDocumento;
    private String correlativoDocumento;
    private String codTipoMoneda;
    private String fechEmision;
    //Emisor:
    private String nroDocumentoEmis;
    private int codTipoDocEmis;
    private String nombreEmis;
    private String direccionEmis;
    //Receptor:
    private String nroDocumentoRecep;
    private int codTipoDocRecep;
    private String nombreRecep;
    private String direccionRecep;

    public int getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(int tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getSerieDocumento() {
        return serieDocumento;
    }

    public void setSerieDocumento(String serieDocumento) {
        this.serieDocumento = serieDocumento;
    }

    public String getCorrelativoDocumento() {
        return correlativoDocumento;
    }

    public void setCorrelativoDocumento(String correlativoDocumento) {
        this.correlativoDocumento = correlativoDocumento;
    }

    public String getCodTipoMoneda() {
        return codTipoMoneda;
    }

    public void setCodTipoMoneda(String codTipoMoneda) {
        this.codTipoMoneda = codTipoMoneda;
    }

    public String getFechEmision() {
        return fechEmision;
    }

    public void setFechEmision(String fechEmision) {
        this.fechEmision = fechEmision;
    }

    public String getNroDocumentoEmis() {
        return nroDocumentoEmis;
    }

    public void setNroDocumentoEmis(String nroDocumentoEmis) {
        this.nroDocumentoEmis = nroDocumentoEmis;
    }

    public int getCodTipoDocEmis() {
        return codTipoDocEmis;
    }

    public void setCodTipoDocEmis(int codTipoDocEmis) {
        this.codTipoDocEmis = codTipoDocEmis;
    }

    public String getNombreEmis() {
        return nombreEmis;
    }

    public void setNombreEmis(String nombreEmis) {
        this.nombreEmis = nombreEmis;
    }

    public String getDireccionEmis() {
        return direccionEmis;
    }

    public void setDireccionEmis(String direccionEmis) {
        this.direccionEmis = direccionEmis;
    }

    public String getNroDocumentoRecep() {
        return nroDocumentoRecep;
    }

    public void setNroDocumentoRecep(String nroDocumentoRecep) {
        this.nroDocumentoRecep = nroDocumentoRecep;
    }

    public int getCodTipoDocRecep() {
        return codTipoDocRecep;
    }

    public void setCodTipoDocRecep(int codTipoDocRecep) {
        this.codTipoDocRecep = codTipoDocRecep;
    }

    public String getNombreRecep() {
        return nombreRecep;
    }

    public void setNombreRecep(String nombreRecep) {
        this.nombreRecep = nombreRecep;
    }

    public String getDireccionRecep() {
        return direccionRecep;
    }

    public void setDireccionRecep(String direccionRecep) {
        this.direccionRecep = direccionRecep;
    }
}
