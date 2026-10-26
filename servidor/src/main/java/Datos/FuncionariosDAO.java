package Datos;

import Logico.Funcionario;
import Logico.Sucursal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;

public class FuncionariosDAO {
    
    public void insertar(
            String nombre,
            String nombreSucursal,
            String rol) throws SQLException {
        
        String sqlSucursal = """
                SELECT id_sucursal
                FROM sucursales
                WHERE nombre = ?
                """;
        
        String sqlFuncionario = """
                INSERT INTO funcionarios
                (nombre, rol, fecha_ingreso, id_sucursal)
                VALUES (?, ?, ?, ?)
                """;
        
        try (Connection conexion = Conexion.getConexion()) {
            
            int idSucursal;
            
            // Buscar la sucursal
            try (PreparedStatement ps =
                         conexion.prepareStatement(sqlSucursal)) {
                
                ps.setString(1, nombreSucursal);
                
                try (ResultSet rs = ps.executeQuery()) {
                    
                    if (!rs.next()) {
                        throw new SQLException(
                                "La sucursal indicada no existe."
                        );
                    }
                    
                    idSucursal = rs.getInt("id_sucursal");
                    
                    if (rs.next()) {
                        throw new SQLException(
                                "Hay varias sucursales con ese nombre."
                        );
                    }
                }
            }
            
            // Insertar el funcionario
            try (PreparedStatement ps = conexion.prepareStatement(sqlFuncionario)) {
                
                ps.setString(1, nombre);
                ps.setString(2, rol);
                ps.setDate(3, Date.valueOf(LocalDate.now()));
                ps.setInt(4, idSucursal);
                    
                ps.executeUpdate();
            }
        }
    }

    
    
    public void modificar(int idFuncionario, String nombre, String nombreSucursal, String rol) throws SQLException {
        
        String sqlSucursal = """
                SELECT id_sucursal
                FROM sucursales
                WHERE nombre = ?
                """;
        
        String sqlFuncionario = """
                UPDATE funcionarios
                SET nombre = ?,
                    rol = ?,
                    id_sucursal = ?
                WHERE id_funcionario = ?
                """;
        
        try (Connection conexion = Conexion.getConexion()) {
            
            int idSucursal;
            
            try (PreparedStatement ps = conexion.prepareStatement(sqlSucursal)) {
                
                ps.setString(1, nombreSucursal);
                
                try (ResultSet rs = ps.executeQuery()) {
                    
                    if (!rs.next()) {
                        throw new SQLException("La sucursal indicada no existe.");
                    }
                    
                    idSucursal = rs.getInt("id_sucursal");
                    
                    if (rs.next()) {
                        throw new SQLException("Hay varias sucursales con ese nombre.");
                    }
                }
            }
            
            try (PreparedStatement ps = conexion.prepareStatement(sqlFuncionario)) {
                
                ps.setString(1, nombre);
                ps.setString(2, rol);
                ps.setInt(3, idSucursal);
                ps.setInt(4, idFuncionario);
                
                int filas = ps.executeUpdate();
                
                if (filas == 0) {
                    throw new SQLException(
                            "No existe un funcionario con ese identificador."
                    );
                }
            }
        }
    }
    
    public void eliminar(int idFuncionario) throws SQLException {
        
        String sql = """
                DELETE FROM funcionarios
                WHERE id_funcionario = ?
                """;
        
        try (Connection conexion = Conexion.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            
            ps.setInt(1, idFuncionario);
            
            int filas = ps.executeUpdate();
            
            if (filas == 0) {
                throw new SQLException(
                        "No existe un funcionario con ese identificador."
                );
            }
        }
    }
    
    public ArrayList<Funcionario> listar() throws SQLException {
        
        ArrayList<Funcionario> lista = new ArrayList<>();
        
        String sql = """
                SELECT
                    f.id_funcionario,
                    f.nombre AS nombre_funcionario,
                    f.rol,
                    f.fecha_ingreso,
                    s.id_sucursal,
                    s.nombre AS nombre_sucursal,
                    s.direccion,
                    s.ciudad
                FROM funcionarios f
                INNER JOIN sucursales s
                    ON f.id_sucursal = s.id_sucursal
                ORDER BY f.nombre
                """;
        
        
        try (Connection conexion = Conexion.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                
                Date fecha = rs.getDate("fecha_ingreso");
                
                
                Sucursal sucursal = new Sucursal(
                        rs.getInt("id_sucursal"),
                        rs.getString("nombre_sucursal"),
                        rs.getString("direccion"),
                        rs.getString("ciudad")
                );
                
                Funcionario funcionario = new Funcionario(
                        rs.getString("id_funcionario"),
                        rs.getString("nombre_funcionario"),
                        rs.getString("rol"),
                        fecha != null ? fecha.toLocalDate() : null,
                        sucursal
                );
                
                lista.add(funcionario);
            }
        }
        
        
         return lista;
    }
}