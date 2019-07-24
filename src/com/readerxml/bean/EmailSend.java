package com.readerxml.bean;



public class EmailSend {
    private String usuario;
    private String contra;
    private String rutaArchivo; //xml
    private String nombreArchivo; //xml
    private String rutaArchivo2; //pdf
    private String nombreArchivo2; //pdf
    private String destino ;
    private String asunto;
    private String mensaje;
    private String destinoDesarrollo;
    private int tipoMensaje;

    public int getTipoMensaje() {
        return tipoMensaje;
    }

    public void setTipoMensaje(int tipoMensaje) {
        this.tipoMensaje = tipoMensaje;
    }

    public String getDestinoDesarrollo() {
        return destinoDesarrollo;
    }

    public void setDestinoDesarrollo(String destinoDesarrollo) {
        this.destinoDesarrollo = destinoDesarrollo;
    }
    
    public String getRutaArchivo2() {
        return rutaArchivo2;
    }

    public void setRutaArchivo2(String rutaArchivo2) {
        this.rutaArchivo2 = rutaArchivo2;
    }

    public String getNombreArchivo2() {
        return nombreArchivo2;
    }

    public void setNombreArchivo2(String nombreArchivo2) {
        this.nombreArchivo2 = nombreArchivo2;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
    
    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContra() {
        return contra;
    }

    public void setContra(String contra) {
        this.contra = contra;
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }
    
    
}
