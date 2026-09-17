package Logico;

import java.util.ArrayList;

public class Prestamos {

    private ArrayList<Prestamo> lista;

    public Prestamos() {
        this.lista = new ArrayList<>();
    }

    public void nuevoPrestamo(Prestamo p) {
        this.lista.add(p);
    }

    public ArrayList<Prestamo> mostrarPrestamos() {
        return this.lista;
    }
}