package com.readerxml.bean;

import java.util.ArrayList;


public class Documento {
    private int version;
    private Cabecera cabecera;
    private ArrayList<Detalle> detallesDocumento;
    private Total total;
    private String pathXML;
    private String pathPDF;

    public String getPathXML() {
        return pathXML;
    }

    public void setPathXML(String pathXML) {
        this.pathXML = pathXML;
    }

    public String getPathPDF() {
        return pathPDF;
    }

    public void setPathPDF(String pathPDF) {
        this.pathPDF = pathPDF;
    }

    public Total getTotal() {
        return total;
    }

    public void setTotal(Total total) {
        this.total = total;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Cabecera getCabecera() {
        return cabecera;
    }

    public void setCabecera(Cabecera cabecera) {
        this.cabecera = cabecera;
    }

    public ArrayList<Detalle> getDetallesDocumento() {
        return detallesDocumento;
    }

    public void setDetallesDocumento(ArrayList<Detalle> detallesDocumento) {
        this.detallesDocumento = detallesDocumento;
    }
}
