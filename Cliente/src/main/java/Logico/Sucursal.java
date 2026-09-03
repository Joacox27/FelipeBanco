package Logico;

import Logico.Funcionario;
import java.util.ArrayList;
import java.util.List;

public class Sucursal {
    private int idSucursal;
    private String nombre;
    private String direccion;
    private String ciudad;
    private List<Funcionario> funcionarios;
    private List<CuentaBancaria> cuentas;

    public Sucursal(int idSucursal, String nombre, String direccion, String ciudad) {
        this.idSucursal = idSucursal;
        this.nombre = nombre;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.funcionarios = new ArrayList<>();
        this.cuentas = new ArrayList<>();
    }

    public int getIdSucursal() {
        return idSucursal;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public List<Funcionario> getFuncionarios() {
        return funcionarios;
    }

    public List<CuentaBancaria> getCuentas() {
        return cuentas;
    }

    public void setIdSucursal(int idSucursal) {
        this.idSucursal = idSucursal;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public void setFuncionarios(List<Funcionario> funcionarios) {
        this.funcionarios = funcionarios;
    }

    public void setCuentas(List<CuentaBancaria> cuentas) {
        this.cuentas = cuentas;
    }

    
    
    
    public void agregarFuncionario(Funcionario f) { funcionarios.add(f); }
    public void agregarCuenta(CuentaBancaria c) { cuentas.add(c); }
}
