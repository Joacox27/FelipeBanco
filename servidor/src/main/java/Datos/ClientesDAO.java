package Datos;

import Logico.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ClientesDAO {

    public void insertar(Cliente cliente, int password)
            throws SQLException {

        String sql = """
                INSERT INTO clientes
                (documento, nombre, apellido, fecha_nacimiento,
                 calle, numero, ciudad, telefono, password)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conexion = Conexion.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, cliente.getDocumento());
            ps.setString(2, cliente.getNombre());
            ps.setString(3, cliente.getApellido());

            if (cliente.getFechaNacimiento() != null) {
                ps.setDate(4,java.sql.Date.valueOf(cliente.getFechaNacimiento()));
            } else {
                ps.setNull(4, java.sql.Types.DATE);
            }

            ps.setString(5, cliente.getCalle());
            ps.setString(6, cliente.getNumero());
            ps.setString(7, cliente.getCiudad());
            ps.setString(8, cliente.getTelefono());
            ps.setInt(9, password);

            ps.executeUpdate();
        }
    }

    public Cliente buscarPorDocumento(String documento)
            throws SQLException {

        String sql = """
                SELECT documento, nombre, apellido,
                       fecha_nacimiento, calle, numero,
                       ciudad, telefono
                FROM clientes
                WHERE documento = ?
                """;

        try (Connection conexion = Conexion.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, documento);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearCliente(rs);
                }
            }
        }

        return null;
    }

    public ArrayList<Cliente> listar() throws SQLException {

        ArrayList<Cliente> clientes = new ArrayList<>();

        String sql = """
                SELECT documento, nombre, apellido,
                       fecha_nacimiento, calle, numero,
                       ciudad, telefono
                FROM clientes
                ORDER BY apellido, nombre
                """;

        try (Connection conexion = Conexion.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                clientes.add(mapearCliente(rs));
            }
        }

        return clientes;
    }


    public Cliente autenticar(String documento, int password)
            throws SQLException {

        String sql = """
                SELECT documento, nombre, apellido,
                       fecha_nacimiento, calle, numero,
                       ciudad, telefono
                FROM clientes
                WHERE documento = ? AND password = ?
                """;

        try (Connection conexion = Conexion.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, documento);
            ps.setInt(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearCliente(rs);
                }
            }
        }

        return null;
    }

    
    public void modificar(Cliente cliente) throws SQLException {

        if (cliente == null) {
            throw new IllegalArgumentException(
                    "Los datos del cliente son obligatorios."
            );
        }

        String sql = """
                UPDATE clientes
                SET nombre = ?,
                    apellido = ?,
                    fecha_nacimiento = ?,
                    calle = ?,
                    numero = ?,
                    ciudad = ?,
                    telefono = ?
                WHERE documento = ?
                """;

        try (Connection conexion = Conexion.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getApellido());
            ps.setDate(3,java.sql.Date.valueOf(cliente.getFechaNacimiento()));
            ps.setString(4, cliente.getCalle());
            ps.setString(5, cliente.getNumero());
            ps.setString(6, cliente.getCiudad());
            ps.setString(7, cliente.getTelefono());
            ps.setString(8, cliente.getDocumento());

            int filas = ps.executeUpdate();

            if (filas == 0) {
                throw new SQLException("No existe un cliente con ese documento.");
            }
        }
    }
    
    public void eliminar(String documento) throws SQLException {
        
        String sql = """
                DELETE FROM clientes
                WHERE documento = ?
                """;
        
        try (Connection conexion = Conexion.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            
            ps.setString(1, documento);
            
            try {
                int filas = ps.executeUpdate();

                if (filas == 0) {
                    throw new SQLException(
                            "No existe un cliente con esa cedula."
                    );
                }
                
            } catch (SQLException e) {

                if (e.getSQLState() != null
                        && e.getSQLState().startsWith("23")) {

                    throw new SQLException("No se puede eliminar el cliente porque tiene " + "cuentas bancarias, préstamos u operaciones asociados.",e);
                    //La eliminación se bloquea cuando existen registros relacionados mediante claves foráneas.
                    //Así se mantiene el historial correctamente
                    
                    
                    
                }

                throw e;
            }
        }
    }
    
    // Convertir una fila de MySQL en un objeto Cliente
    private Cliente mapearCliente(ResultSet rs)
            throws SQLException {

        java.sql.Date fecha = rs.getDate("fecha_nacimiento");

        return new Cliente(
                rs.getString("documento"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                fecha != null ? fecha.toLocalDate() : null,
                rs.getString("calle"),
                rs.getString("numero"),
                rs.getString("ciudad"),
                rs.getString("telefono")
        );
    }
}