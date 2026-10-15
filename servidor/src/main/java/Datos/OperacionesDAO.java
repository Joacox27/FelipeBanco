package Datos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.time.LocalDateTime;
import java.util.UUID;

public class OperacionesDAO {

    private final CuentasDAO cuentasDAO;

    public OperacionesDAO() {
        cuentasDAO = new CuentasDAO();
    }


    
    public void registrarDeposito(
            String numeroCuenta,
            double monto,
            String documentoCliente) throws SQLException {

        validarDatos(numeroCuenta, monto, documentoCliente);

        try (Connection conexion = Conexion.getConexion()) {

            conexion.setAutoCommit(false);

            try {
                comprobarTitular(
                        conexion, numeroCuenta, documentoCliente
                );

                double saldoActual =
                        cuentasDAO.obtenerSaldoBloqueando(
                                conexion, numeroCuenta
                        );

                double nuevoSaldo = saldoActual + monto;

                comprobarSaldoValido(nuevoSaldo);

                cuentasDAO.actualizarSaldo(
                        conexion, numeroCuenta, nuevoSaldo
                );

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


    public void registrarRetiro(
            String numeroCuenta,
            double monto,
            String documentoCliente) throws SQLException {

        validarDatos(numeroCuenta, monto, documentoCliente);

        try (Connection conexion = Conexion.getConexion()) {

            conexion.setAutoCommit(false);

            try {
                comprobarTitular(
                        conexion, numeroCuenta, documentoCliente
                );

                double saldoActual =
                        cuentasDAO.obtenerSaldoBloqueando(
                                conexion, numeroCuenta
                        );

                if (saldoActual < monto) {
                    throw new IllegalArgumentException(
                            "Saldo insuficiente."
                    );
                }

                double nuevoSaldo = saldoActual - monto;

                cuentasDAO.actualizarSaldo(
                        conexion, numeroCuenta, nuevoSaldo
                );

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



    public void registrarTransferencia(
            String cuentaOrigen,
            String cuentaDestino,
            double monto,
            String documentoCliente) throws SQLException {

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


    private void guardarOperacion(
            Connection conexion,
            String tipo,
            double monto,
            String cuentaOrigen,
            String cuentaDestino,
            String documentoCliente) throws SQLException {

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



