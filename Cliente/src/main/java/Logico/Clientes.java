package Logico;

import java.util.ArrayList;

public class Clientes {
    
    private ArrayList<Cliente> lista;
    
    public Clientes() {
        this.lista = new ArrayList<>();
    }
    
    public void nuevoCliente(Cliente c) {
        lista.add(c);
    }
    
    public ArrayList<Cliente> mostrarClientes() {
        return lista;
    }
    
    public void limpiar() {
        lista.clear();
    }
}
