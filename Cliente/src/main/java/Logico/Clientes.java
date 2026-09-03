package Logico;

import Logico.Cliente;
import java.util.ArrayList;

/**
 *
 * @author gabriel
 */
public class Clientes {
    private ArrayList<Cliente> lista; 
    public Clientes(){
        this.lista = new ArrayList<>();
    }
    public void nuevoCliente(Cliente c){
        this.lista.add(c);
    }
    public ArrayList<Cliente> mostrarClientes(){
        return this.lista;
    }
    
}
