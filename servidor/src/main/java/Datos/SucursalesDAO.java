package Datos;

import Logico.Sucursal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SucursalesDAO {

    public void insertar(
            String nombre,
            String direccion,
            String ciudad) throws SQLException {
        
        String sql = """
                INSERT INTO sucursales
                (nombre, direccion, ciudad)
                VALUES (?, ?, ?)
                """;
        
        try (Connection conexion = Conexion.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setString(2, direccion);
            ps.setString(3, ciudad);
            
            ps.executeUpdate();
        }
    }
    
    public void modificar(
            int idSucursal,
            String nombre,
            String direccion,
            String ciudad) throws SQLException {

        String sql = """
                UPDATE sucursales
                SET nombre = ?,
                    direccion = ?,
                    ciudad = ?
                WHERE id_sucursal = ?
                """;

        try (Connection conexion = Conexion.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setString(2, direccion);
            ps.setString(3, ciudad);
            ps.setInt(4, idSucursal);

            int filas = ps.executeUpdate();

            if (filas == 0) {
                throw new SQLException(
                        "No existe una sucursal con ese identificador."
                );
            }
        }
    }
    
    public void eliminar(int idSucursal) throws SQLException {

        String sql = """
                DELETE FROM sucursales
                WHERE id_sucursal = ?
                """;

        try (Connection conexion = Conexion.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idSucursal);

            try {
                int filas = ps.executeUpdate();

                if (filas == 0) {
                    throw new SQLException(
                            "No existe una sucursal con ese identificador."
                    );
                }

            } catch (SQLException e) {

                if (e.getSQLState() != null
                        && e.getSQLState().startsWith("23")) {

                    throw new SQLException(
                            "No se puede eliminar la sucursal porque tiene "
                            + "funcionarios o cuentas bancarias asociados."
                    );
                }

                throw e;
            }
        }
    }
    
    public ArrayList<Sucursal> listar() throws SQLException {

        ArrayList<Sucursal> lista = new ArrayList<>();

        
        String sql = """
                SELECT id_sucursal, nombre, direccion, ciudad
                FROM sucursales
                ORDER BY nombre
                """;

        try (Connection conexion = Conexion.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Sucursal sucursal = new Sucursal(
                        rs.getInt("id_sucursal"),
                        rs.getString("nombre"),
                        rs.getString("direccion"),
                        rs.getString("ciudad")
                );

                  lista.add(sucursal);
            }
        }

        return lista;
    }

  
    
    
    
    public Sucursal buscarPorId(int idSucursal)
            throws SQLException {

        String sql = """
                SELECT id_sucursal, nombre, direccion, ciudad
                FROM sucursales
                WHERE id_sucursal = ?
                """;

        try (Connection conexion = Conexion.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            
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


