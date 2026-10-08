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

    public String getIdPrestamo() {
        return idPrestamo;
    }

    public double getMonto() {
        return monto;
    }

    public double getTasaInteres() {
        return tasaInteres;
    }

    public LocalDate getFechaOtorgamiento() {
        return fechaOtorgamiento;
    }

    public int getCuotas() {
        return cuotas;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setIdPrestamo(String idPrestamo) {
        this.idPrestamo = idPrestamo;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public void setTasaInteres(double tasaInteres) {
        this.tasaInteres = tasaInteres;
    }

    public void setFechaOtorgamiento(LocalDate fechaOtorgamiento) {
        this.fechaOtorgamiento = fechaOtorgamiento;
    }

    public void setCuotas(int cuotas) {
        this.cuotas = cuotas;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    
    
    
    
}
