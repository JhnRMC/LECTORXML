/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.readerxml.controller;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author TechEra-01
 */
public class XmlBuilder {
    Document xml;
    private Element elemento;
    private String etiqueta;
    private String etiquetaGlobal;
    public XmlBuilder(Document xml) {
        this.xml = xml;
    }

    public String getEtiquetaGlobal() {
        return etiquetaGlobal;
    }

    public void setEtiquetaGlobal(String etiquetaGlobal) {
        this.etiquetaGlobal = etiquetaGlobal;
    }
    
    
    public XmlBuilder nodo() {
        Node nodeList = xml.getElementsByTagName(etiquetaGlobal).item(0);
        elemento = (Element) nodeList;
        return this;
    }

    public XmlBuilder elemento(String etiqueta) {
        if (!etiqueta.equals("")) {
            Node nodeList = elemento.getElementsByTagName(etiqueta).item(0);
            elemento = (Element) nodeList;
        }
        return this;
    }

    public String etiquetaAnidada(String etiqueta) {
        return elemento.getElementsByTagName(etiqueta).item(0).getTextContent();
    }

    public XmlBuilder etiqueta(String etq) {
        etiqueta = xml.getElementsByTagName(etq).item(0).getTextContent();
        return this;
    }

    public String builder() {
        return etiqueta;
    }
}
