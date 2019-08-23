package com.readerxml.conexion;

import com.readerxml.util.Propiedades;
import java.sql.*;

public class Conexion {

    private String executeStatement = null;
    private static Connection con = null;
    private static Statement st = null;
    private static ResultSet rs = null;
    private static String url = null;
    private static String user = null;
    private static String pass = null;

    static {

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
            }
            try {
                if (st != null) {
                    st.close();
                }
            } catch (SQLException e) {
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }

        //------------ DESARROLLO -------------------
        //url = "jdbc:oracle:thin:@ 192.168.253.108:1521:gp";
        //------------ PRODUCCION -------------------
        //url = "jdbc:oracle:thin:@ 192.168.253.180:1521:gp";
        url = Propiedades.propiedades.getProperty("url.conexion");
        user = "comercial";
        pass = "gp";

    }

    public Conexion() {
        try {
            con = DriverManager.getConnection(url, user, pass);
            //Disable auto-commit mode
            //con.setAutoCommit(false);
        } catch (SQLException ce) {
            System.out.println(ce.getMessage());
        }
    }

    public ResultSet executeQuery() throws SQLException {

        //con = DriverManager.getConnection(url, user, pass);
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(executeStatement);

        return rs;
    }

    public ResultSet executeQuery(String query) throws SQLException {

        //con = DriverManager.getConnection(url, user, pass);
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);

        return rs;
    }

    public CallableStatement prepareCall(String sp) throws SQLException {

        //con = DriverManager.getConnection(url, user, pass);
        CallableStatement cs = con.prepareCall(sp);

        return cs;
    }

    public int executeUpdate() throws SQLException {

        //con = DriverManager.getConnection(url, user, pass);
        Statement st = con.createStatement();
        int nupdate = st.executeUpdate(executeStatement);

        return nupdate;
    }

    public int executeUpdate(String query) throws SQLException {

        //con = DriverManager.getConnection(url, user, pass);
        Statement st = con.createStatement();
        int nupdate = st.executeUpdate(query);

        return nupdate;
    }

    /**
     * Returns the executeQuery.
     *
     * @return String
     */
    public String getExecuteStatement() {
        return executeStatement;
    }

    public void setExecuteStatement(String executeStatement) {
        this.executeStatement = executeStatement;
    }

    public Connection connection() {
        return con;
    }

    public void closeConnection() throws SQLException {
        con.close();
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        con.setAutoCommit(autoCommit);
    }

    public void rollback() throws SQLException {
        con.rollback();
    }

    public void commit() throws SQLException {
        con.commit();
    }

    public long Sequencia_Temporal() {
        // Se usa para generar secuencia en tabla tempo
        long dSeq = 0;
        ResultSet rs = null;

        String query = "insert into tempo(seq_temporal) values(temporal.nextval)";

        try {
            con = DriverManager.getConnection(url, user, pass);
            Statement st = con.createStatement();
            int nupdate = st.executeUpdate(query);

            query = "select temporal.currval seq from sys.dual";
            rs = st.executeQuery(query);

            //Obtener el valor de la secuencia
            while (rs.next()) {
                dSeq = rs.getLong("seq");
            }

            query = "delete from tempo where seq_temporal='" + dSeq + "'";
            nupdate = st.executeUpdate(query);

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                    closeConnection();
                }
            } catch (SQLException e) {
            }
        }

        return dSeq;
    }

    public void Delete_Temporal(long dSeq) {
        Conexion myconnection = new Conexion();
        PreparedStatement ps = null;
        String queryDel = "Delete from PANAAUTOS.TMP_DET_SER_PROV where seq_temporal=? ";
        try {
            ps = myconnection.connection().prepareStatement(queryDel);
            ps.setLong(1, dSeq);
            ps.executeUpdate();
            myconnection.closeConnection();
            if (ps != null) {
                ps.close();
            }
        } catch (SQLException e) {
        } finally {
            try {
                myconnection.closeConnection();
            } catch (Exception e) {
            }
        }
    }

    public void deleteSQL(String queryDel) {
        Conexion myconnection = new Conexion();
        PreparedStatement ps = null;
        try {
            ps = myconnection.connection().prepareStatement(queryDel);
            ps.executeUpdate();
            myconnection.closeConnection();
            if (ps != null) {
                ps.close();
            }
        } catch (SQLException e) {
        } finally {
            try {
                myconnection.closeConnection();
            } catch (SQLException e) {
            }
        }
    }
}
