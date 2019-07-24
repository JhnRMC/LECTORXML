package com.readerxml.bean;

public class Detalle {
    //Detalle-Producto
    private int itemProducto;
    private String codProducto;
    private String descProducto;
    private double cantProducto;
    private double precioUnitarioProducto;
    private double valorVentaProducto;

    public int getItemProducto() {
        return itemProducto;
    }

    public void setItemProducto(int itemProducto) {
        this.itemProducto = itemProducto;
    }

    public String getCodProducto() {
        return codProducto;
    }

    public void setCodProducto(String codProducto) {
        this.codProducto = codProducto;
    }

    public String getDescProducto() {
        return descProducto;
    }

    public void setDescProducto(String descProducto) {
        this.descProducto = descProducto;
    }

    public double getCantProducto() {
        return cantProducto;
    }

    public void setCantProducto(double cantProducto) {
        this.cantProducto = cantProducto;
    }

    public double getPrecioUnitarioProducto() {
        return precioUnitarioProducto;
    }

    public void setPrecioUnitarioProducto(double precioUnitarioProducto) {
        this.precioUnitarioProducto = precioUnitarioProducto;
    }

    public double getValorVentaProducto() {
        return valorVentaProducto;
    }

    public void setValorVentaProducto(double valorVentaProducto) {
        this.valorVentaProducto = valorVentaProducto;
    }
}
