package Datos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CuentasDAO {

    
    
    public boolean existe(
            Connection conexion,
            String numeroCuenta) throws SQLException {

        String sql = """
                SELECT 1
                FROM cuentas
                WHERE numero_cuenta = ?
                """;

        
        try (PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setString(1, numeroCuenta);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }


    public boolean perteneceACliente(
            Connection conexion,
            String numeroCuenta,
            String documentoCliente) throws SQLException {

        String sql = """
                SELECT 1
                FROM cuentas
                WHERE numero_cuenta = ?
                  AND documento_cliente = ?
                """;

        try (PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setString(1, numeroCuenta);
            ps.setString(2, documentoCliente);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Obtener el saldo y bloquear la fila durante la transacción
    public double obtenerSaldoBloqueando(
            Connection conexion,
            String numeroCuenta) throws SQLException {

        String sql = """
                SELECT saldo
                FROM cuentas
                WHERE numero_cuenta = ?
                FOR UPDATE
                """;

        try (PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setString(1, numeroCuenta);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getDouble("saldo");
                }

                throw new SQLException(
                        "La cuenta " + numeroCuenta + " no existe."
                );
            }
        }
    }


    public void actualizarSaldo(
            Connection conexion,
            String numeroCuenta,
            double nuevoSaldo) throws SQLException {

        if (!Double.isFinite(nuevoSaldo)) {
            throw new IllegalArgumentException(
                    "El saldo no es válido."
            );
        }

        String sql = """
                UPDATE cuentas
                SET saldo = ?
                WHERE numero_cuenta = ?
                """;

        try (PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setDouble(1, nuevoSaldo);
            ps.setString(2, numeroCuenta);

            int filas = ps.executeUpdate();

            if (filas != 1) {
                throw new SQLException(
                        "No se pudo actualizar el saldo de la cuenta."
                );
            }
        }
    }
}