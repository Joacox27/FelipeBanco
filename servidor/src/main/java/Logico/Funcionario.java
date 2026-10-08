package Logico;

import java.time.LocalDate;

public class Funcionario {
    private String idFuncionario;
    private String nombre;
    private String rol;
    private LocalDate fechaIngreso;
    private Sucursal sucursal;

    public Funcionario(String idFuncionario, String nombre, String rol,
                       LocalDate fechaIngreso, Sucursal sucursal) {
        this.idFuncionario = idFuncionario;
        this.nombre = nombre;
        this.rol = rol;
        this.fechaIngreso = fechaIngreso;
        this.sucursal = sucursal;
    }

    public String getIdFuncionario() {
        return idFuncionario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getRol() {
        return rol;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public Sucursal getSucursal() {
        return sucursal;
    }
}