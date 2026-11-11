package Datos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    
    private static final String URL = "jdbc:mysql://localhost:3306/felipebanco?serverTimezone=UTC";
    
    private static final String USUARIO = "felipe";
    
    private static final String PASSWORD = "1234";
    
    private Conexion() {
  
        
    }
    
    public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(URL,USUARIO,PASSWORD);
    }
}