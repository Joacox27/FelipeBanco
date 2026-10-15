package Datos;

import Logico.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ClientesDAO {

    // Registrar un cliente
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
                ps.setDate(
                        4,
                        java.sql.Date.valueOf(
                                cliente.getFechaNacimiento()
                        )
                );
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

    // Buscar un cliente por documento
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

    // Mostrar todos los clientes
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