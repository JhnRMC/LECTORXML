/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.readerxml.bean;

/**
 *
 * @author TechEra-01
 */
public class Buzon {
    private int cantidadCorreos;
    private int cantidadCorreosLeidos;
    private int cantidadCorreosNuevos;
    private String[] correosNoDeseados;

    public int getCantidadCorreos() {
        return cantidadCorreos;
    }

    public void setCantidadCorreos(int cantidadCorreos) {
        this.cantidadCorreos = cantidadCorreos;
    }

    public int getCantidadCorreosLeidos() {
        return cantidadCorreosLeidos;
    }

    public void setCantidadCorreosLeidos(int cantidadCorreosLeidos) {
        this.cantidadCorreosLeidos = cantidadCorreosLeidos;
    }

    public int getCantidadCorreosNuevos() {
        return cantidadCorreosNuevos;
    }

    public void setCantidadCorreosNuevos(int cantidadCorreosNuevos) {
        this.cantidadCorreosNuevos = cantidadCorreosNuevos;
    }

    public String[] getCorreosNoDeseados() {
        return correosNoDeseados;
    }

    public void setCorreosNoDeseados(String[] correosNoDeseados) {
        this.correosNoDeseados = correosNoDeseados;
    }
}
