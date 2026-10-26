package Datos;

import Logico.CuentaAhorro;
import Logico.CuentaBancaria;
import Logico.CuentaCorriente;
import Logico.Sucursal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

public class CuentasDAO {
    
    public CuentaBancaria insertar(
        String tipo,
        int idSucursal,
        double tasaInteres,
        double limiteDescubierto,
        String documentoCliente) throws SQLException {

    if (!"Ahorro".equals(tipo) && !"Corriente".equals(tipo)) {
        throw new IllegalArgumentException(
                "El tipo de cuenta no es válido."
        );
    }

    if (documentoCliente == null || documentoCliente.isBlank()) {
        throw new IllegalArgumentException(
                "El documento del titular es obligatorio."
        );
    }
    
    if (!Double.isFinite(tasaInteres) || tasaInteres < 0 || !Double.isFinite(limiteDescubierto) || limiteDescubierto < 0) {
        throw new IllegalArgumentException(
                "Los valores de la cuenta no son válidos."
        );
    }
    
    try (Connection conexion = Conexion.getConexion()) {
        
        conexion.setAutoCommit(false);
        
        try {
            Sucursal sucursal = buscarSucursal(conexion, idSucursal);
            
            if (sucursal == null) {
                throw new IllegalArgumentException(
                        "La sucursal seleccionada no existe."
                );
            }
            
            String numeroCuenta;
            
            do {
                numeroCuenta = "CTA-"
                        + UUID.randomUUID()
                                .toString()
                                .replace("-", "")
                                .substring(0, 16)
                                .toUpperCase();
            } while (existe(conexion, numeroCuenta));
            
            LocalDate fechaApertura = LocalDate.now();
            double saldoInicial = 0.00;
            
            String sqlCuenta = """
                    INSERT INTO cuentas
                    (numero_cuenta, tipo, fecha_apertura, saldo,
                     id_sucursal, tasa_interes, limite_descubierto)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    """;
            
            try (PreparedStatement ps =
                         conexion.prepareStatement(sqlCuenta)) {
                
                ps.setString(1, numeroCuenta);
                ps.setString(2, tipo);
                ps.setDate(3, java.sql.Date.valueOf(fechaApertura));
                ps.setDouble(4, saldoInicial);
                ps.setInt(5, idSucursal);
                ps.setDouble(6,"Ahorro".equals(tipo) ? tasaInteres : 0.00);
                ps.setDouble(7,"Corriente".equals(tipo) ? limiteDescubierto : 0.00);
                
                ps.executeUpdate();
            }
            
            String sqlTitular = """
                    INSERT INTO titulares_cuenta
                    (documento_cliente, numero_cuenta)
                    VALUES (?, ?)
                    """;
            
            try (PreparedStatement ps =
                        conexion.prepareStatement(sqlTitular)) {
                
                ps.setString(1, documentoCliente);
                ps.setString(2, numeroCuenta);
                
                ps.executeUpdate();
            }
            
            CuentaBancaria cuenta;
            
            if ("Ahorro".equals(tipo)) {
                cuenta = new CuentaAhorro(numeroCuenta, fechaApertura, saldoInicial, sucursal, tasaInteres);
            } else {
                cuenta = new CuentaCorriente(numeroCuenta, fechaApertura, saldoInicial, sucursal, limiteDescubierto);
            }
            
            conexion.commit();

            return cuenta;

        } catch (SQLException | RuntimeException e) {

            try {
                conexion.rollback();
            } catch (SQLException errorRollback) {
                e.addSuppressed(errorRollback);
            }

            throw e;
            
         }
        
        }
    }
    
    public void eliminar(String numeroCuenta) throws SQLException {
        
        if (numeroCuenta == null || numeroCuenta.isBlank()) {
            throw new IllegalArgumentException(
                    "El número de cuenta es obligatorio."
            );
        }
        
        try (Connection conexion = Conexion.getConexion()) {
            
            conexion.setAutoCommit(false);
            
            try {
                
                String sqlCuenta = """
                        SELECT numero_cuenta
                        FROM cuentas
                        WHERE numero_cuenta = ?
                        FOR UPDATE
                        """;
                
                try (PreparedStatement ps = conexion.prepareStatement(sqlCuenta)) {
                    
                    ps.setString(1, numeroCuenta);
                    
                    try (ResultSet rs = ps.executeQuery()) {
                        
                        if (!rs.next()) {
                            throw new IllegalArgumentException(
                                    "La cuenta bancaria no existe."
                            );
                        }
                    }
                }
                
                String sqlOperaciones = """
                        SELECT 1
                        FROM operaciones
                        WHERE numero_cuenta = ?
                           OR numero_cuenta_destino = ?
                        LIMIT 1
                        """;
                
                try (PreparedStatement ps = conexion.prepareStatement(sqlOperaciones)) {
                    
                    ps.setString(1, numeroCuenta);
                    ps.setString(2, numeroCuenta);
                    
                    try (ResultSet rs = ps.executeQuery()) {
                        
                        if (rs.next()) {
                            throw new IllegalArgumentException(
                                    "No se puede eliminar la cuenta porque tiene operaciones bancarias registradas."
                            );
                        }
                    }
                }
                
                String sqlTitulares = """
                        DELETE FROM titulares_cuenta
                        WHERE numero_cuenta = ?
                        """;
                
                try (PreparedStatement ps =
                             conexion.prepareStatement(sqlTitulares)) {
                    
                    ps.setString(1, numeroCuenta);
                    ps.executeUpdate();
                }
                
                String sqlEliminar = """
                        DELETE FROM cuentas
                        WHERE numero_cuenta = ?
                        """;
                
                try (PreparedStatement ps = conexion.prepareStatement(sqlEliminar)) {
                    
                    ps.setString(1, numeroCuenta);
                    
                    int filas = ps.executeUpdate();
                    
                    if (filas != 1) {
                        throw new SQLException(
                                "No se pudo eliminar la cuenta bancaria."
                        );
                    }
                }
                
                conexion.commit();
                
            } catch (SQLException | RuntimeException e) {
                
                try {
                    conexion.rollback();
                } catch (SQLException errorRollback) {
                    e.addSuppressed(errorRollback);
                }
                
                throw e;
            }
        }
    }
    
    public void asociarTitular(String numeroCuenta, String documentoCliente) throws SQLException {
        
        if (numeroCuenta == null || numeroCuenta.isBlank() || documentoCliente == null || documentoCliente.isBlank()) {
            
            throw new IllegalArgumentException(
                    
                    "El número de cuenta y el documento son obligatorios."
            );
        }
        
        try (Connection conexion = Conexion.getConexion()) {
            
            conexion.setAutoCommit(false);
            
            try {
                
                String sqlCuenta = """
                        SELECT 1
                        FROM cuentas
                        WHERE numero_cuenta = ?
                        FOR UPDATE
                        """;
                
                try (PreparedStatement ps = conexion.prepareStatement(sqlCuenta)) {
                    
                    ps.setString(1, numeroCuenta);
                    
                    try (ResultSet rs = ps.executeQuery()) {
                        
                        if (!rs.next()) {
                            throw new IllegalArgumentException(
                                    "La cuenta bancaria no existe."
                            );
                        }
                    }
                }
                
                String sqlCliente = """
                        SELECT 1
                        FROM clientes
                        WHERE documento = ?
                        """;
                
                try (PreparedStatement ps = conexion.prepareStatement(sqlCliente)) {
                    
                    ps.setString(1, documentoCliente);
                    
                    try (ResultSet rs = ps.executeQuery()) {
                        
                        if (!rs.next()) {
                            throw new IllegalArgumentException(
                                    "El cliente no existe."
                            );
                        }
                    }
                }
                
                if (perteneceACliente(conexion, numeroCuenta, documentoCliente)) {
                    
                    throw new IllegalArgumentException(
                            "El cliente ya es titular de esta cuenta."
                    );
                }
                
                String sql = """
                        INSERT INTO titulares_cuenta
                        (documento_cliente, numero_cuenta)
                        VALUES (?, ?)
                        """;
                
                try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                    
                    ps.setString(1, documentoCliente);
                    ps.setString(2, numeroCuenta);
                    
                    ps.executeUpdate();
                }
                
                conexion.commit();
                
            } catch (SQLException | RuntimeException e) {
                
                try {
                    conexion.rollback();
                } catch (SQLException errorRollback) {
                    e.addSuppressed(errorRollback);
                }
                
                throw e;
            }
        }
    }
    
    public void desasociarTitular(String numeroCuenta, String documentoCliente) throws SQLException {
        
        if (numeroCuenta == null || numeroCuenta.isBlank() || documentoCliente == null || documentoCliente.isBlank()) {
            
            throw new IllegalArgumentException(
                    "El número de cuenta y el documento son obligatorios."
            );
        }
        
        try (Connection conexion = Conexion.getConexion()) {
            
            conexion.setAutoCommit(false);
            
            try {
                
                String sqlCuenta = """
                        SELECT 1
                        FROM cuentas
                        WHERE numero_cuenta = ?
                        FOR UPDATE
                        """;
                
                try (PreparedStatement ps = conexion.prepareStatement(sqlCuenta)) {
                    
                    ps.setString(1, numeroCuenta);
                    
                    try (ResultSet rs = ps.executeQuery()) {
                        
                        if (!rs.next()) {
                            throw new IllegalArgumentException(
                                    "La cuenta bancaria no existe."
                            );
                        }
                    }
                }
                
                if (!perteneceACliente(conexion, numeroCuenta, documentoCliente)) {
                    
                    throw new IllegalArgumentException(
                            "El cliente no es titular de esta cuenta."
                    );
                }
                
                String sqlCantidad = """
                        SELECT COUNT(*) AS cantidad
                        FROM titulares_cuenta
                        WHERE numero_cuenta = ?
                        """;

                try (PreparedStatement ps = conexion.prepareStatement(sqlCantidad)) {
                    
                    ps.setString(1, numeroCuenta);
                    
                    try (ResultSet rs = ps.executeQuery()) {
                        
                        if (rs.next() && rs.getInt("cantidad") <= 1) {
                            throw new IllegalArgumentException(
                                    "No se puede eliminar al último titular de una cuenta."
                            );
                        }
                    }
                }

                String sqlEliminar = """
                        DELETE FROM titulares_cuenta
                        WHERE numero_cuenta = ?
                          AND documento_cliente = ?
                        """;

                try (PreparedStatement ps =
                             conexion.prepareStatement(sqlEliminar)) {

                    ps.setString(1, numeroCuenta);
                    ps.setString(2, documentoCliente);

                    ps.executeUpdate();
                }

                conexion.commit();

            } catch (SQLException | RuntimeException e) {

                try {
                    conexion.rollback();
                } catch (SQLException errorRollback) {
                    e.addSuppressed(errorRollback);
                }

                throw e;
            }
        }
    }
    
    public ArrayList<CuentaBancaria> listar() throws SQLException {

            ArrayList<CuentaBancaria> lista = new ArrayList<>();

            String sql = """
                    SELECT
                        c.numero_cuenta,
                        c.tipo,
                        c.fecha_apertura,
                        c.saldo,
                        c.tasa_interes,
                        c.limite_descubierto,
                        s.id_sucursal,
                        s.nombre AS nombre_sucursal,
                        s.direccion,
                        s.ciudad
                    FROM cuentas c
                    INNER JOIN sucursales s
                        ON c.id_sucursal = s.id_sucursal
                    ORDER BY c.fecha_apertura DESC
                    """;

            try (Connection conexion = Conexion.getConexion();
                 PreparedStatement ps = conexion.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Sucursal sucursal = new Sucursal(
                            rs.getInt("id_sucursal"),
                            rs.getString("nombre_sucursal"),
                            rs.getString("direccion"),
                            rs.getString("ciudad")
                    );

                    String numeroCuenta =
                            rs.getString("numero_cuenta");

                    String tipo = rs.getString("tipo");

                    LocalDate fechaApertura =
                            rs.getDate("fecha_apertura").toLocalDate();

                    double saldo = rs.getDouble("saldo");

                    CuentaBancaria cuenta;

                    if (tipo.equals("Ahorro")) {

                        cuenta = new CuentaAhorro(
                                numeroCuenta,
                                fechaApertura,
                                saldo,
                                sucursal,
                                rs.getDouble("tasa_interes")
                        );

                    } else if (tipo.equals("Corriente")) {

                        cuenta = new CuentaCorriente(
                                numeroCuenta,
                                fechaApertura,
                                saldo,
                                sucursal,
                                rs.getDouble("limite_descubierto")
                        );

                    } else {

                        throw new SQLException(
                                "Tipo de cuenta desconocido: " + tipo
                        );
                    }

                    lista.add(cuenta);
                }
            }

        return lista;
    }
    
    
    public ArrayList<CuentaBancaria> listarPorCliente(
            String documentoCliente) throws SQLException {

        ArrayList<CuentaBancaria> lista = new ArrayList<>();

        String sql = """
                SELECT
                    c.numero_cuenta,
                    c.tipo,
                    c.fecha_apertura,
                    c.saldo,
                    c.tasa_interes,
                    c.limite_descubierto,
                    s.id_sucursal,
                    s.nombre AS nombre_sucursal,
                    s.direccion,
                    s.ciudad
                FROM cuentas c
                INNER JOIN titulares_cuenta tc
                    ON c.numero_cuenta = tc.numero_cuenta
                INNER JOIN sucursales s
                    ON c.id_sucursal = s.id_sucursal
                WHERE tc.documento_cliente = ?
                ORDER BY c.fecha_apertura DESC
                """;

        try (Connection conexion = Conexion.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, documentoCliente);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    lista.add(mapearCuenta(rs));
                }
            }
        }

        return lista;
    }
    
    public CuentaBancaria buscarPorNumero(
            String numeroCuenta) throws SQLException {

        try (Connection conexion = Conexion.getConexion()) {
            return buscarPorNumero(conexion, numeroCuenta);
        }
    }

    public CuentaBancaria buscarPorNumero(
            Connection conexion,
            String numeroCuenta) throws SQLException {

        String sql = """
                SELECT
                    c.numero_cuenta,
                    c.tipo,
                    c.fecha_apertura,
                    c.saldo,
                    c.tasa_interes,
                    c.limite_descubierto,
                    s.id_sucursal,
                    s.nombre AS nombre_sucursal,
                    s.direccion,
                    s.ciudad
                FROM cuentas c
                INNER JOIN sucursales s
                    ON c.id_sucursal = s.id_sucursal
                WHERE c.numero_cuenta = ?
                """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, numeroCuenta);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearCuenta(rs);
                }
            }
        }

        return null;
    }

    private CuentaBancaria mapearCuenta(ResultSet rs)
            throws SQLException {

        Sucursal sucursal = new Sucursal(
                rs.getInt("id_sucursal"),
                rs.getString("nombre_sucursal"),
                rs.getString("direccion"),
                rs.getString("ciudad")
        );

        String numeroCuenta = rs.getString("numero_cuenta");
        String tipo = rs.getString("tipo");

        LocalDate fechaApertura =
                rs.getDate("fecha_apertura").toLocalDate();

        double saldo = rs.getDouble("saldo");

        if ("Ahorro".equals(tipo)) {

            return new CuentaAhorro(
                    numeroCuenta,
                    fechaApertura,
                    saldo,
                    sucursal,
                    rs.getDouble("tasa_interes")
            );

        } else if ("Corriente".equals(tipo)) {

            return new CuentaCorriente(
                    numeroCuenta,
                    fechaApertura,
                    saldo,
                    sucursal,
                    rs.getDouble("limite_descubierto")
            );
        }

        throw new SQLException(
                "Tipo de cuenta desconocido: " + tipo
        );
    }

    public void modificar(
            String numeroCuenta,
            int idSucursal,
            double tasaInteres,
            double limiteDescubierto) throws SQLException {

        if (numeroCuenta == null || numeroCuenta.isBlank()) {
            throw new IllegalArgumentException(
                    "El número de cuenta es obligatorio."
            );
        }

        if (idSucursal <= 0) {
            throw new IllegalArgumentException(
                    "La sucursal seleccionada no es válida."
            );
        }

        if (!Double.isFinite(tasaInteres) || tasaInteres < 0
                || !Double.isFinite(limiteDescubierto)
                || limiteDescubierto < 0) {

            throw new IllegalArgumentException(
                    "Los parámetros de la cuenta no son válidos."
            );
        }

        try (Connection conexion = Conexion.getConexion()) {

            conexion.setAutoCommit(false);

            try {

                int idSucursalActual;
                String tipo;

                String sqlBuscar = """
                        SELECT id_sucursal, tipo
                        FROM cuentas
                        WHERE numero_cuenta = ?
                        FOR UPDATE
                        """;

                try (PreparedStatement ps =
                             conexion.prepareStatement(sqlBuscar)) {

                    ps.setString(1, numeroCuenta);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            throw new IllegalArgumentException(
                                    "La cuenta bancaria no existe."
                            );
                        }

                        idSucursalActual = rs.getInt("id_sucursal");
                        tipo = rs.getString("tipo");
                    }
                }

                if (idSucursalActual != idSucursal) {

                    String sqlOperaciones = """
                            SELECT 1
                            FROM operaciones
                            WHERE numero_cuenta = ?
                               OR numero_cuenta_destino = ?
                            LIMIT 1
                            """;

                    try (PreparedStatement ps =
                                 conexion.prepareStatement(sqlOperaciones)) {

                        ps.setString(1, numeroCuenta);
                        ps.setString(2, numeroCuenta);

                        try (ResultSet rs = ps.executeQuery()) {

                            if (rs.next()) {
                                throw new IllegalArgumentException(
                                        "No se puede cambiar la sucursal "
                                        + "porque la cuenta ya tiene operaciones "
                                        + "registradas."
                                );
                            }
                        }
                    }
                }

                Sucursal sucursal = buscarSucursal(
                        conexion,
                        idSucursal
                );

                if (sucursal == null) {
                    throw new IllegalArgumentException(
                            "La sucursal seleccionada no existe."
                    );
                }

                String sqlModificar = """
                        UPDATE cuentas
                        SET id_sucursal = ?,
                            tasa_interes = ?,
                            limite_descubierto = ?
                        WHERE numero_cuenta = ?
                        """;

                try (PreparedStatement ps =
                             conexion.prepareStatement(sqlModificar)) {

                    ps.setInt(1, idSucursal);

                    ps.setDouble(
                            2,
                            "Ahorro".equals(tipo) ? tasaInteres : 0.00
                    );

                    ps.setDouble(
                            3,
                            "Corriente".equals(tipo)
                                    ? limiteDescubierto : 0.00
                    );

                    ps.setString(4, numeroCuenta);

                    ps.executeUpdate();
                }

                conexion.commit();

            } catch (SQLException | RuntimeException e) {

                try {
                    conexion.rollback();
                } catch (SQLException errorRollback) {
                    e.addSuppressed(errorRollback);
                }

                throw e;
            }
        }
    }

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
                FROM titulares_cuenta
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

    private Sucursal buscarSucursal(
            Connection conexion,
            int idSucursal) throws SQLException {

        String sql = """
                SELECT id_sucursal, nombre, direccion, ciudad
                FROM sucursales
                WHERE id_sucursal = ?
                """;

        try (PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setInt(1, idSucursal);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return new Sucursal(
                            rs.getInt("id_sucursal"),
                            rs.getString("nombre"),
                            rs.getString("direccion"),
                            rs.getString("ciudad")
                    );
                }
            }
        }

        return null;
    }
}