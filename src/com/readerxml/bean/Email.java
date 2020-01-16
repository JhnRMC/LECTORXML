/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.readerxml.bean;

import java.util.List;

/**
 *
 * @author TechEra-01
 */
public class Email {
    public static final String ETIQUETA_ERRADA="1000";
    public static final String CORREO_RECHAZADO="2000";
    private String correo;
    private String fecha;
    private String asunto;
    private Adjunto adjunto;
    private EtiquetaError etiquetaError;
    private String tipo;

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Adjunto getAdjunto() {
        return adjunto;
    }

    public void setAdjunto(Adjunto adjunto) {
        this.adjunto = adjunto;
    }

    public EtiquetaError getEtiquetaError() {
        return etiquetaError;
    }

    public void setEtiquetaError(EtiquetaError etiquetaError) {
        this.etiquetaError = etiquetaError;
    }

    @Override
    public String toString() {
        return "Email{" + ", emailEmisor=" + correo + ", fecha=" + fecha + ", asunto=" + asunto + ", adjuntos=" + adjunto + '}';
    }
}
