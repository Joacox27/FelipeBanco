package Datos;

import Logico.Cliente;
import Logico.Prestamo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;

public class PrestamosDAO {


    public void insertar(Prestamo prestamo) throws SQLException {

        if (prestamo == null || prestamo.getCliente() == null) {
            throw new IllegalArgumentException(
                    "El préstamo y su cliente son obligatorios."
            );
        }

        String sql = """
                INSERT INTO prestamos
                (id_prestamo, monto, tasa_interes,
                 fecha_otorgamiento, cuotas, documento_cliente)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection conexion = Conexion.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, prestamo.getIdPrestamo());
            ps.setDouble(2, prestamo.getMonto());
            ps.setDouble(3, prestamo.getTasaInteres());

            
            
            ps.setDate(
                    4,
                    Date.valueOf(prestamo.getFechaOtorgamiento())
            );

            ps.setInt(5, prestamo.getCuotas());
            
            ps.setString(
                    6,
                    prestamo.getCliente().getDocumento()
            );

            ps.executeUpdate();
            
            
        }
    }


    


    public ArrayList<Prestamo> listarPorCliente(Cliente cliente)
            throws SQLException {

        if (cliente == null) {
            throw new IllegalArgumentException(
                    "El cliente es obligatorio."
            );
        }

        ArrayList<Prestamo> lista = new ArrayList<>();

        String sql = """
                SELECT id_prestamo, monto, tasa_interes,
                       fecha_otorgamiento, cuotas
                FROM prestamos
                WHERE documento_cliente = ?
                ORDER BY fecha_otorgamiento DESC
                """;

        
        
        try (Connection conexion = Conexion.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, cliente.getDocumento());

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    
                   
                    Date fecha = rs.getDate("fecha_otorgamiento");

                    Prestamo prestamo = new Prestamo(
                            rs.getString("id_prestamo"),
                            rs.getDouble("monto"),
                            rs.getDouble("tasa_interes"),
                            fecha != null
                                    ? fecha.toLocalDate()
                                    : null,
                            rs.getInt("cuotas"),
                            cliente
                    );

                    
                    lista.add(prestamo);
                }
                
            }
        }

        
        
        return lista;
    }
}



