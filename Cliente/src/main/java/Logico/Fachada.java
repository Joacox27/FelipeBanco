package Logico;

import java.time.LocalDate;
import java.util.ArrayList;

public class Fachada {

    private static Fachada singleton;

    private Funcionarios funcionarios;
    private Clientes clientes;
    private Sucursales sucursales;

    private Fachada() {
        funcionarios = new Funcionarios();
        clientes = new Clientes();
    }

    public static Fachada getInstancia() {
        if (singleton == null) {
            singleton = new Fachada();
        }
        return singleton;
    }

    // Funcionarios

    public void nuevoFuncionario(String nombre, String nombreSucursal, String cargo) {

    Sucursal sucursal = null;

    for (Sucursal s : sucursales.mostrarSucursales()) {
        
        if (s.getNombre().equals(nombreSucursal)) {
            
            sucursal = s;
            break;
        }
    }

    if (sucursal == null) {
        throw new IllegalArgumentException("La sucursal no existe.");
    }

    String idFuncionario = "F" + (funcionarios.mostrarFuncionarios().size() + 1);

    Funcionario f = new Funcionario(
            idFuncionario,
            nombre,
            cargo,
            LocalDate.now(),
            sucursal
    );

    funcionarios.nuevoFuncionario(f);
    sucursal.agregarFuncionario(f);
}

    public ArrayList<Funcionario> mostrarFuncionarios() {
        return this.funcionarios.mostrarFuncionarios();
    }

    // Clientes

    public void nuevoCliente(Cliente c) {
        this.clientes.nuevoCliente(c);
    }

    public ArrayList<Cliente> mostrarClientes() {
        return this.clientes.mostrarClientes();
    }
    
    //sucursales
    
     public ArrayList<Sucursal> mostrarSucursales() {
        return this.sucursales.mostrarSucursales();
    }
     
     
}