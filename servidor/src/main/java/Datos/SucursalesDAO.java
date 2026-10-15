package Datos;

import Logico.Sucursal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SucursalesDAO {


    
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


