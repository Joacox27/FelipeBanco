package Logico;

import java.time.LocalDate;
import java.util.ArrayList;

public class Clientes {

    private ArrayList<Cliente> lista;

    public Clientes() {
        this.lista = new ArrayList<>();

        // Clientes preestablecidos para pruebas
        lista.add(new Cliente(
                "68063513",
                911,
                "Felipe",
                LocalDate.of(2001, 9, 11),
                "en un avion",
                "676767676"
        ));

        lista.add(new Cliente(
                "58089064",
                2345,
                "Joaquín Salazar",
                LocalDate.of(1998, 10, 22),
                "Avenida Brasil 2456",
                "098234567"
        ));

        lista.add(new Cliente(
                "53456789",
                9012,
                "Juan perro",
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