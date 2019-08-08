/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.readerxml.dao;

import com.readerxml.LectorEmail;
import com.readerxml.bean.ErrorEtiquetas;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.readerxml.bean.EmailSend;

/**
 *
 * @author iMac02
 */
public class SendEmailSqliteDAO {

    private final static Logger LOGGER = Logger.getLogger("com.readerxml.dao.SendEmailSqliteDAO");

    String pathSqlite = LectorEmail.propiedades.getProperty("path.sqlite");
    //String lite = "D:\\lite.db";
    String url = "jdbc:sqlite:" + pathSqlite;

    public boolean registrarSuccessEnvioCorreo(EmailSend emailSend, ErrorEtiquetas error) {
        boolean status = false;
        Connection conn = null;
        Statement stmt;
        try {
            conn = DriverManager.getConnection(url);
            conn.setAutoCommit(false);
            int idMensaje = registrarErrores(error);
            stmt = conn.createStatement();

            String sql = "INSERT INTO CORREO (ASUNTO,DESTINATARIO,MENSAJE,NOMBRE_ADJUNTO1,ADJUNTO1,NOMBRE_ADJUNTO2,ADJUNTO2,ERROR, ESTADO)"
                    + "VALUES ('" + emailSend.getAsunto() + "','" + emailSend.getDestino() + "'," + idMensaje + ",'" + emailSend.getNombreArchivo() + "'"
                    + ",'" + emailSend.getRutaArchivo() + "','" + emailSend.getNombreArchivo2() + "','" + emailSend.getRutaArchivo2() + "'," + emailSend.getTipoMensaje() + ",0);";
            stmt.executeUpdate(sql);
            conn.commit();
            stmt.close();
            status = true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "ERROR AL REGISTRAR EL CORREO{0}", e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "ERROR AL CERRAR LA CONEXION SQLITE");
            }
        }
        return status;
    }

    private int registrarErrores(ErrorEtiquetas error) {

        Connection conn = null;
        Statement stmt = null;
        int idInsert = 0;
        try {
            conn = DriverManager.getConnection(url);
            conn.setAutoCommit(false);
            if (conn != null) {
                System.out.println("Conectado");
            }
            stmt = conn.createStatement();
            String sql = "INSERT INTO MENSAJE (etiqueta_global, "
                    + "numeroDocumento,"
                    + "tipoMoneda,"
                    + "fechaEmision,"
                    + "numeroDocumentoEmisor,"
                    + "nombreEmisor,"
                    + "numeroDocumentoReceptor,"
                    + "nombreReceptor, "
                    + "codigoItem,"
                    + "codigoProducto, "
                    + "descripcionProducto, "
                    + "precioProducto,"
                    + "valorVenta, "
                    + "valorSubtotal, "
                    + "totalIGV, "
                    + "totalVenta,"
                    + "email,"
                    + "asunto,"
                    + "fecha_correo)"
                    + "VALUES('" + error.getETIQUETA_GLOBAL() + "','"
                    + error.getNumeroDocumento() + "','"
                    + error.getTipoMoneda() + "','"
                    + error.getFechaEmision() + "','"
                    + error.getNumeroDocumentoEmisor() + "','"
                    + error.getNombreEmisor() + "', '"
                    + error.getNumeroDocumentoReceptor() + "','"
                    + error.getNombreReceptor() + "', '"
                    + error.getCodigoitem() + "', '"
                    + error.getCodigoProducto() + "', '"
                    + error.getDescripcionProducto() + "', '"
                    + error.getPrecioProducto() + "', '"
                    + error.getValorVenta() + "', '"
                    + error.getValorSubTotal() + "','"
                    + error.getTotalIGV() + "', '"
                    + error.getTotalVenta() + "', '"
                    + error.getEmail() + "', '"
                    + error.getAsunto() + "', '"
                    + error.getFecha() + "')";
            stmt.executeUpdate(sql);
            ResultSet rs = stmt.getGeneratedKeys();
            while (rs.next()) {
                idInsert = rs.getInt(1);
            }
            conn.commit();
            stmt.close();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "ERROR AL INSERTAR EL MENSAJE DE ERROR");
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "ERROR AL CERRAR LA CONEXION SQLITE");
            }
        }
        return idInsert;
    }
}
