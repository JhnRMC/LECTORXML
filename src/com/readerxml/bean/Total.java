package com.readerxml.bean;

public class Total {
    //Totales
    private double totalOG; //OG: Operación Gravada
    private double totalOI; //OI: Operación Inafecta
    private double totalOE; //OE: Operación Exonerada
    private double totalOGR; //OGR: Operación Gratuita
    private double totalSV; // SV: Subtotal de Venta
    private double totalIGV; //IGV: 18% del subtotal de la venta
    private double totalVenta; // Sumatoria del subtotal  + IGV

    public double getTotalOG() {
        return totalOG;
    }

    public void setTotalOG(double totalOG) {
        this.totalOG = totalOG;
    }

    public double getTotalOI() {
        return totalOI;
    }

    public void setTotalOI(double totalOI) {
        this.totalOI = totalOI;
    }

    public double getTotalOE() {
        return totalOE;
    }

    public void setTotalOE(double totalOE) {
        this.totalOE = totalOE;
    }

    public double getTotalOGR() {
        return totalOGR;
    }

    public void setTotalOGR(double totalOGR) {
        this.totalOGR = totalOGR;
    }

    public double getTotalSV() {
        return totalSV;
    }

    public void setTotalSV(double totalSV) {
        this.totalSV = totalSV;
    }

    public double getTotalIGV() {
        return totalIGV;
    }

    public void setTotalIGV(double totalIGV) {
        this.totalIGV = totalIGV;
    }

    public double getTotalVenta() {
        return totalVenta;
    }

    public void setTotalVenta(double totalVenta) {
        this.totalVenta = totalVenta;
    }
}
