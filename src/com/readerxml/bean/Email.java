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
public class Email {

    private String numero;
    private String emailEmisor;
    private String fecha;
    private String asunto;
    private String[] adjuntos;

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getEmailEmisor() {
        return emailEmisor;
    }

    public void setEmailEmisor(String emailEmisor) {
        this.emailEmisor = emailEmisor;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String[] getAdjuntos() {
        return adjuntos;
    }

    public void setAdjuntos(String[] adjuntos) {
        this.adjuntos = adjuntos;
    }

    @Override
    public String toString() {
        return "Email{" + "numero=" + numero + ", emailEmisor=" + emailEmisor + ", fecha=" + fecha + ", asunto=" + asunto + ", adjuntos=" + adjuntos + '}';
    }
}
