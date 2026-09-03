package Logico;

import java.util.ArrayList;

public class Sucursales {

    private ArrayList<Sucursal> lista;

    public Sucursales() {
        this.lista = new ArrayList<>();

        // Sucursales preestablecidas
        lista.add(new Sucursal(
                1,
                "Sucursal Centro",
                "18 de Julio 1234",
                "Montevideo"
        ));

        lista.add(new Sucursal(
                2,
                "Sucursal Pocitos",
                "Avenida Brasil 2456",
                "Montevideo"
        ));

        lista.add(new Sucursal(
                3,
                "Sucursal Cordón",
                "18 de Julio 1876",
                "Montevideo"
        ));
    }

    public ArrayList<Sucursal> mostrarSucursales() {
        return this.lista;
    }
}