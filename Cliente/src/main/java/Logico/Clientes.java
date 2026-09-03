package Logico;

import java.time.LocalDate;
import java.util.ArrayList;

public class Clientes {

    private ArrayList<Cliente> lista;

    public Clientes() {
        this.lista = new ArrayList<>();

        // Clientes preestablecidos para pruebas
        lista.add(new Cliente(
                "51234567",
                1234,
                "Juan Pérez",
                LocalDate.of(2000, 5, 15),
                "18 de Julio 1234",
                "099123456"
        ));

        lista.add(new Cliente(
                "47890123",
                5678,
                "María González",
                LocalDate.of(1998, 10, 22),
                "Avenida Brasil 2456",
                "098234567"
        ));

        lista.add(new Cliente(
                "53456789",
                9012,
                "Pedro Rodríguez",
                LocalDate.of(2002, 3, 8),
                "8 de Octubre 1876",
                "097345678"
        ));
    }

    public void nuevoCliente(Cliente c) {
        this.lista.add(c);
    }

    public ArrayList<Cliente> mostrarClientes() {
        return this.lista;
    }
}