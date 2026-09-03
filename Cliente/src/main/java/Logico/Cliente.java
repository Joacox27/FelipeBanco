package Logico;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cliente {
    private String documento;
    private int password; //luego esto estara en la base de datos, no aca en el programa mismo
    private String nombreCompleto;
    private LocalDate fechaNacimiento;
    private String direccion;
    private String telefono;
    private List<CuentaBancaria> cuentas;
    private List<Prestamo> prestamos;

    public Cliente(String documento, int password, String nombreCompleto, LocalDate fechaNacimiento,
                   String direccion, String telefono) {
        this.documento = documento;
        this.password = password;
        this.nombreCompleto = nombreCompleto;
        this.fechaNacimiento = fechaNacimiento;
        this.direccion = direccion;
        this.telefono = telefono;
        this.cuentas = new ArrayList<>();
        this.prestamos = new ArrayList<>();
    }

    // Getters y Setters
    public String getDocumento() { return documento; }
    public String getNombreCompleto() { return nombreCompleto; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public String getDireccion() { return direccion; }
    public String getTelefono() { return telefono; }
    public int getPassword() { return password; }
    

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public void setPassword(int password) {
        this.password = password;
    }
    
    

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public void setDireccion(String direccion) { this.direccion = direccion; }

    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void agregarCuenta(CuentaBancaria cuenta) { cuentas.add(cuenta); }
    public void agregarPrestamo(Prestamo prestamo) { prestamos.add(prestamo); }
    public void setCuentas(List<CuentaBancaria> cuentas) { this.cuentas = cuentas; }
    public void setPrestamos(List<Prestamo> prestamos) { this.prestamos = prestamos; }
    
    
    
}
