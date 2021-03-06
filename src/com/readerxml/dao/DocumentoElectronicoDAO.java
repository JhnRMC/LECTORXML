package com.readerxml.dao;

import com.readerxml.LectorEmail;
import com.readerxml.bean.Documento;
import com.readerxml.bean.Detalle;
import com.readerxml.conexion.Conexion;
import com.readerxml.bean.Cabecera;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.readerxml.bean.Total;
import com.readerxml.util.Propiedades;
import java.io.IOException;

public class DocumentoElectronicoDAO {

    private final static Logger LOGGER = Logger.getLogger(DocumentoElectronicoDAO.class.getName());
    private CallableStatement cs;
    private int codigoDocumento;
    Conexion conexion;

    public boolean verificarDocumentoExistente(String serieDocumento, String correlativoDocumento, String rucEmpresa, int tipoDocumento) {

        conexion = new Conexion();
        cs = null;
        boolean isExiste = false;
        ResultSet rs = null;
        try {
            cs = conexion.prepareCall("{call PANAAUTOS.PKG_DOCUMENTOS_ELECTRONICOS.VALIDAR_DOCUMENTO(?,?,?,?,?)}");
            cs.setString(1, serieDocumento);
            cs.setString(2, correlativoDocumento);
            cs.setString(3, rucEmpresa);
            cs.setInt(4, tipoDocumento);
            cs.registerOutParameter(5, oracle.jdbc.OracleTypes.CURSOR);
            cs.execute();
            rs = (ResultSet) cs.getObject(5);

            if (rs.next()) {
                System.out.println("DOCUMENTO EXISTENTE : " + rs.getString(7) + "-" + rs.getString(3) + "-" + rs.getString(4));
                isExiste = true;
            }
            rs.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            LOGGER.log(Level.SEVERE, "ERROR EN LA VERIFICACION DE EXISTENCIA DE LA CUENTA");
            corregirConexion();
        } catch (NullPointerException nullExp) {
            corregirConexion();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (cs != null) {
                    cs.close();
                }
                if (conexion != null) {
                    conexion.closeConnection();
                }
            } catch (NullPointerException ex) {
            } catch (SQLException ex1) {
                corregirConexion();
                LOGGER.log(Level.SEVERE, "PROBLEMAS PARA CERRAR LA CONEXION DE VERIFICACION DE EXISTENCIA DE CUENTA");
            }
        }
        return isExiste;
    }

    public boolean insertDocumentoElectronico(Documento documento) {
        conexion = new Conexion();
        boolean success = false;
        Cabecera cabecera = documento.getCabecera();
        ArrayList<Detalle> listaDetalles = documento.getDetallesDocumento();
        Total total = documento.getTotal();
        try {
            cs = conexion.prepareCall("{call PANAAUTOS.PKG_DOCUMENTOS_ELECTRONICOS.INSERT_CABTOT_DOCELEC(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
            cs.setInt(1, cabecera.getTipoDocumento());
            cs.setString(2, cabecera.getSerieDocumento());
            cs.setString(3, cabecera.getCorrelativoDocumento() + "");
            cs.setString(4, cabecera.getCodTipoMoneda());
            cs.setString(5, cabecera.getFechEmision());
            cs.setString(6, cabecera.getNroDocumentoEmis());
            cs.setInt(7, cabecera.getCodTipoDocEmis());
            cs.setString(8, cabecera.getNombreEmis());
            cs.setString(9, cabecera.getDireccionEmis());
            cs.setString(10, cabecera.getNroDocumentoRecep());
            cs.setInt(11, cabecera.getCodTipoDocRecep());
            cs.setDouble(12, total.getTotalOG());
            cs.setDouble(13, total.getTotalOI());
            cs.setDouble(14, total.getTotalOE());
            cs.setDouble(15, total.getTotalOGR());
            cs.setDouble(16, total.getTotalSV());
            cs.setDouble(17, total.getTotalIGV());
            cs.setDouble(18, total.getTotalVenta());
            cs.setString(19, documento.getPathXML());
            cs.setString(20, documento.getPathPDF());
            cs.registerOutParameter(21, java.sql.Types.NUMERIC);
            success = cs.execute();
            codigoDocumento = cs.getInt(21);

            listaDetalles.forEach(detalle -> {
                try {
                    cs = conexion.prepareCall("{call PANAAUTOS.PKG_DOCUMENTOS_ELECTRONICOS.INSERT_DET_DOCELEC(?,?,?,?,?,?,?)}");
                    cs.setInt(1, codigoDocumento);
                    cs.setInt(2, detalle.getItemProducto());
                    cs.setString(3, detalle.getCodProducto());
                    cs.setString(4, detalle.getDescProducto().length() > 190 ? "Contenido no disponible" : detalle.getDescProducto());
                    cs.setDouble(5, detalle.getCantProducto());
                    cs.setDouble(6, detalle.getPrecioUnitarioProducto());
                    cs.setDouble(7, detalle.getValorVentaProducto());
                    cs.execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                    LOGGER.log(Level.SEVERE, "ERROR AL LLAMAR AL PROCEDIMIENTO ALMACENADO DE INSERCION DE DOCUMENTO");
                } catch (NullPointerException ex) {
                    corregirConexion();
                }

            });
            LOGGER.log(Level.INFO, "DOCUMENTO: {0}-{1} REGISTRADO CORRECTAMENTE.", new Object[]{cabecera.getSerieDocumento(), cabecera.getCorrelativoDocumento()});

        } catch (NullPointerException nullExp) {
            LOGGER.log(Level.SEVERE, "PROBLEMAS CON LA CONEXIÓN DE RED");
            corregirConexion();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "ERROR AL INSERTAR EL DOCUMENTO");
            corregirConexion();
        } finally {
            try {
                conexion.closeConnection();// libera cn
                if (cs != null) {
                    cs.close();
                }
            } catch (NullPointerException ex) {
                corregirConexion();
            } catch (SQLException e) {
                corregirConexion();
                LOGGER.log(Level.SEVERE, "ERROR AL CERRAR LA CONEXION DE LA INSERCION DE DOCUMENTO");
            }
        }
        return success;

    }

    public void corregirConexion() {
        try {
            --LectorEmail.cantidadCorreosLeidos;
            Propiedades.escribirPropiedad("cantidad.msg.buzon.leidos", String.valueOf(LectorEmail.cantidadCorreosLeidos));
            LOGGER.log(Level.SEVERE, "PROBLEMAS CON LA CONEXIÓN DE RED");
        } catch (IOException ex) {
            Logger.getLogger(DocumentoElectronicoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
