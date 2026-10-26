package Datos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import Logico.CuentaBancaria;
import Logico.Deposito;
import Logico.Operacion;
import Logico.Retiro;
import Logico.Transferencia;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.UUID;

public class OperacionesDAO {
    
    private final CuentasDAO cuentasDAO;
    
    public OperacionesDAO() {
        
        cuentasDAO = new CuentasDAO();
    }


    
    public void registrarDeposito(String numeroCuenta, double monto, String documentoCliente) throws SQLException {
        
        validarDatos(numeroCuenta, monto, documentoCliente);
        
        try (Connection conexion = Conexion.getConexion()) {
            
            conexion.setAutoCommit(false);
            
            try {
                comprobarTitular(conexion, numeroCuenta, documentoCliente);
                
                double saldoActual = cuentasDAO.obtenerSaldoBloqueando(conexion, numeroCuenta);
                
                double nuevoSaldo = saldoActual + monto;
                
                comprobarSaldoValido(nuevoSaldo);
                
                cuentasDAO.actualizarSaldo(conexion, numeroCuenta, nuevoSaldo);
                
                guardarOperacion(
                        conexion,
                        "DEPOSITO",
                        monto,
                        numeroCuenta,
                        null,
                        documentoCliente
                );

                conexion.commit();

            } catch (SQLException | RuntimeException e) {
                revertir(conexion, e);
                throw e;
            }
        }
    }


    public void registrarRetiro(String numeroCuenta, double monto, String documentoCliente) throws SQLException {
        
        validarDatos(numeroCuenta, monto, documentoCliente);
        
        try (Connection conexion = Conexion.getConexion()) {
            
            conexion.setAutoCommit(false);
            
            try {
                comprobarTitular(conexion, numeroCuenta, documentoCliente);
                
                double saldoActual = cuentasDAO.obtenerSaldoBloqueando(conexion, numeroCuenta);
                
                if (saldoActual < monto) {
                    throw new IllegalArgumentException(
                            "Saldo insuficiente."
                    );
                }
                
                double nuevoSaldo = saldoActual - monto;
                
                cuentasDAO.actualizarSaldo(conexion, numeroCuenta, nuevoSaldo);
                
                guardarOperacion(
                        conexion,
                        "RETIRO",
                        monto,
                        numeroCuenta,
                        null,
                        documentoCliente
                );
                
                conexion.commit();
                
            } catch (SQLException | RuntimeException e) {
                revertir(conexion, e);
                throw e;
            }
        }
    }
    
    
    
    public void registrarTransferencia(String cuentaOrigen, String cuentaDestino, double monto, String documentoCliente) throws SQLException {
        
        validarDatos(cuentaOrigen, monto, documentoCliente);

        if (cuentaDestino == null || cuentaDestino.isBlank()) {
            throw new IllegalArgumentException(
                    "La cuenta de destino es obligatoria."
            );
        }

        if (cuentaOrigen.equals(cuentaDestino)) {
            throw new IllegalArgumentException(
                    "Las cuentas deben ser diferentes."
            );
        }

        try (Connection conexion = Conexion.getConexion()) {

            conexion.setAutoCommit(false);

            try {
                comprobarTitular(
                        conexion, cuentaOrigen, documentoCliente
                );

                /*
                 * Bloqueamos las dos cuentas en orden alfabético
                 * para reducir el riesgo de interbloqueos.
                 */
                String primeraCuenta;
                String segundaCuenta;

                if (cuentaOrigen.compareTo(cuentaDestino) < 0) {
                    primeraCuenta = cuentaOrigen;
                    segundaCuenta = cuentaDestino;
                } else {
                    primeraCuenta = cuentaDestino;
                    segundaCuenta = cuentaOrigen;
                }

                double saldoPrimera =
                        cuentasDAO.obtenerSaldoBloqueando(
                                conexion, primeraCuenta
                        );

                double saldoSegunda =
                        cuentasDAO.obtenerSaldoBloqueando(
                                conexion, segundaCuenta
                        );

                double saldoOrigen;
                double saldoDestino;

                if (cuentaOrigen.equals(primeraCuenta)) {
                    saldoOrigen = saldoPrimera;
                    saldoDestino = saldoSegunda;
                } else {
                    saldoOrigen = saldoSegunda;
                    saldoDestino = saldoPrimera;
                }

                if (saldoOrigen < monto) {
                    throw new IllegalArgumentException(
                            "Saldo insuficiente en la cuenta origen."
                    );
                }

                double nuevoSaldoOrigen = saldoOrigen - monto;
                double nuevoSaldoDestino = saldoDestino + monto;

                comprobarSaldoValido(nuevoSaldoDestino);

                cuentasDAO.actualizarSaldo(
                        conexion,
                        cuentaOrigen,
                        nuevoSaldoOrigen
                );

                cuentasDAO.actualizarSaldo(
                        conexion,
                        cuentaDestino,
                        nuevoSaldoDestino
                );

                guardarOperacion(
                        conexion,
                        "TRANSFERENCIA",
                        monto,
                        cuentaOrigen,
                        cuentaDestino,
                        documentoCliente
                );

                conexion.commit();

            } catch (SQLException | RuntimeException e) {
                revertir(conexion, e);
                throw e;
            }
        }
    }


    private void guardarOperacion(Connection conexion, String tipo, double monto, String cuentaOrigen, String cuentaDestino, String documentoCliente) throws SQLException {
        
        String sql = """
                INSERT INTO operaciones
                (id_operacion, tipo, fecha_hora, monto,
                 numero_cuenta, numero_cuenta_destino,
                 documento_cliente)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        
        try (PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setString(1, UUID.randomUUID().toString());
            ps.setString(2, tipo);
            ps.setTimestamp(
                    3,
                    Timestamp.valueOf(LocalDateTime.now())
            );
            ps.setDouble(4, monto);
            ps.setString(5, cuentaOrigen);

            if (cuentaDestino == null) {
                ps.setNull(6, java.sql.Types.VARCHAR);
            } else {
                ps.setString(6, cuentaDestino);
            }

            ps.setString(7, documentoCliente);

            ps.executeUpdate();
        }
    }


    private void comprobarTitular(
            Connection conexion,
            String numeroCuenta,
            String documentoCliente) throws SQLException {

        if (!cuentasDAO.perteneceACliente(
                conexion, numeroCuenta, documentoCliente)) {

            throw new IllegalArgumentException(
                    "La cuenta no existe o no pertenece al cliente."
            );
        }
    }

    private void validarDatos(
            String numeroCuenta,
            double monto,
            String documentoCliente) {

        if (numeroCuenta == null || numeroCuenta.isBlank()) {
            throw new IllegalArgumentException(
                    "El número de cuenta es obligatorio."
            );
        }

        if (!Double.isFinite(monto) || monto <= 0) {
            throw new IllegalArgumentException(
                    "El monto debe ser mayor a cero."
            );
        }

        if (documentoCliente == null
                || documentoCliente.isBlank()) {

            throw new IllegalArgumentException(
                    "El documento del cliente es obligatorio."
            );
        }
    }

    private void comprobarSaldoValido(double saldo) {
        if (!Double.isFinite(saldo) || saldo < 0) {
            throw new IllegalArgumentException(
                    "El saldo resultante no es válido."
            );
        }
    }
    
    public ArrayList<Operacion> listarPorCuenta(
            String numeroCuenta) throws SQLException {

        if (numeroCuenta == null || numeroCuenta.isBlank()) {
            throw new IllegalArgumentException(
                    "El número de cuenta es obligatorio."
            );
        }

        ArrayList<String[]> registros = new ArrayList<>();
        ArrayList<Operacion> operaciones = new ArrayList<>();

        String sql = """
                SELECT id_operacion, tipo, fecha_hora, monto,
                       numero_cuenta, numero_cuenta_destino
                FROM operaciones
                WHERE numero_cuenta = ?
                   OR numero_cuenta_destino = ?
                ORDER BY fecha_hora DESC
                """;

        try (Connection conexion = Conexion.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, numeroCuenta);
            ps.setString(2, numeroCuenta);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    registros.add(leerRegistro(rs));
                }
            }

            for (String[] registro : registros) {
                operaciones.add(mapearOperacion(conexion, registro));
            }
        }

        return operaciones;
    }


    public Operacion buscarPorId(
            String idOperacion) throws SQLException {

        if (idOperacion == null || idOperacion.isBlank()) {
            throw new IllegalArgumentException(
                    "El identificador de la operación es obligatorio."
            );
        }

        String sql = """
                SELECT id_operacion, tipo, fecha_hora, monto,
                       numero_cuenta, numero_cuenta_destino
                FROM operaciones
                WHERE id_operacion = ?
                """;

        try (Connection conexion = Conexion.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, idOperacion);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    return null;
                }

                String[] registro = leerRegistro(rs);

                return mapearOperacion(conexion, registro);
            }
        }
    }


    private String[] leerRegistro(ResultSet rs)
            throws SQLException {

        java.sql.Timestamp fechaHora = rs.getTimestamp("fecha_hora");
        java.math.BigDecimal monto = rs.getBigDecimal("monto");

        return new String[]{
            rs.getString("id_operacion"),
            rs.getString("tipo"),
            fechaHora.toLocalDateTime().toString(),
            monto.toPlainString(),
            rs.getString("numero_cuenta"),
            rs.getString("numero_cuenta_destino") == null
                    ? ""
                    : rs.getString("numero_cuenta_destino")
        };
    }


    private Operacion mapearOperacion(
            Connection conexion,
            String[] datos) throws SQLException {

        String id = datos[0];
        String tipo = datos[1];

        LocalDateTime fechaHora =
                LocalDateTime.parse(datos[2]);

        double monto = Double.parseDouble(datos[3]);

        CuentaBancaria cuentaOrigen =
                cuentasDAO.buscarPorNumero(conexion, datos[4]);

        if (cuentaOrigen == null) {
            throw new SQLException(
                    "No se encontró la cuenta de origen de la operación."
            );
        }

        switch (tipo) {

            case "DEPOSITO":
                return new Deposito(
                        id,
                        fechaHora,
                        monto,
                        cuentaOrigen
                );

            case "RETIRO":
                return new Retiro(
                        id,
                        fechaHora,
                        monto,
                        cuentaOrigen
                );

            case "TRANSFERENCIA":

                if (datos[5].isBlank()) {
                    throw new SQLException(
                            "La transferencia no tiene cuenta de destino."
                    );
                }

                CuentaBancaria cuentaDestino =
                        cuentasDAO.buscarPorNumero(conexion, datos[5]);

                if (cuentaDestino == null) {
                    throw new SQLException(
                            "No se encontró la cuenta de destino de la transferencia."
                    );
                }

                return new Transferencia(
                        id,
                        fechaHora,
                        monto,
                        cuentaOrigen,
                        cuentaDestino
                );

            default:
                throw new SQLException(
                        "Tipo de operación desconocido: " + tipo
                );
        }
    }

    // si ocurre un error durante la transaccion se revierten los cambios


    private void revertir(
            Connection conexion,
            Exception errorOriginal) {

        try {
            conexion.rollback();
        } catch (SQLException errorRollback) {
            errorOriginal.addSuppressed(errorRollback);
        }
    }
}



