package Logico;

import java.time.LocalDate;
import java.util.ArrayList;

public class Fachada {

    private static Fachada singleton;

    private Funcionarios funcionarios;
    private Clientes clientes;
    private Sucursales sucursales;

    private Fachada()  {
        funcionarios = new Funcionarios();
        clientes = new Clientes();
        sucursales = new Sucursales();
    }

    public static Fachada getInstancia() {

            if (singleton == null) {
            singleton = new Fachada();
         }

        return singleton;
    }



    public void nuevoFuncionario(String nombre, String nombreSucursal, String rol) {

      
        Sucursal sucursal = null;

        for (Sucursal s : sucursales.mostrarSucursales()) {

            if (s.getNombre().equals(nombreSucursal)) {
                sucursal = s;
                break;
            }
        }

        // se comprueba que la sucursal exista
        if (sucursal == null) {
            throw new IllegalArgumentException(
                    "La sucursal no existe."
            );
        }

      
        String idFuncionario = "F" + (funcionarios.mostrarFuncionarios().size() + 1);

        // Crear funcionario
        Funcionario f = new Funcionario(
                idFuncionario,
                nombre,
                rol,
                LocalDate.now(),
                sucursal
        );

        // se guarda en la lista de funcionarios
        funcionarios.nuevoFuncionario(f);

        // se asocia a la sucursal
        sucursal.agregarFuncionario(f);
    }

    public ArrayList<Funcionario> mostrarFuncionarios() {
        return funcionarios.mostrarFuncionarios();
    }


        public void nuevoCliente(Cliente c) {

    
          for (Cliente cliente : clientes.mostrarClientes()) {

            if (cliente.getDocumento().equals(c.getDocumento())) {
                 throw new IllegalArgumentException(
                        "Ya existe un cliente con esa CI."
                );
            }
        }

        clientes.nuevoCliente(c);
    }

        
        
    public ArrayList<Cliente> mostrarClientes() {
        return clientes.mostrarClientes();
    }

    
    
    public ArrayList<Sucursal> mostrarSucursales() {
        return sucursales.mostrarSucursales();
    }


    
    
    public Cliente login(String documento, int password) {

        for (Cliente c : clientes.mostrarClientes()) {

            if (c.getDocumento().equals(documento)
                     && c.getPassword() == password) {

                 return c;
            }
         }

        return null;
    }
}

