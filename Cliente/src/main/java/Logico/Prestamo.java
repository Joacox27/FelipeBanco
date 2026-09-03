package Logico;

import Logico.Cliente;
import java.time.LocalDate;

public class Prestamo {
    private String idPrestamo;
    private double monto;
    private double tasaInteres;
    private LocalDate fechaOtorgamiento;
    private int cuotas;
    private Cliente cliente;

    public Prestamo(String idPrestamo, double monto, double tasaInteres,
                    LocalDate fechaOtorgamiento, int cuotas, Cliente cliente) {
        this.idPrestamo = idPrestamo;
        this.monto = monto;
        this.tasaInteres = tasaInteres;
        this.fechaOtorgamiento = fechaOtorgamiento;
        this.cuotas = cuotas;
        this.cliente = cliente;
    }
}
