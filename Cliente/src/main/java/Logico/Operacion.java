package Logico;

import java.time.LocalDateTime;

public abstract class Operacion {

    protected String idOperacion;
    protected LocalDateTime fechaHora;
    protected double monto;
    protected CuentaBancaria cuenta;

    public Operacion(String idOperacion, LocalDateTime fechaHora, double monto, CuentaBancaria cuenta) {
        
        if (idOperacion == null || idOperacion.isBlank()) {
            throw new IllegalArgumentException(
                    "El ID de la operación es obligatorio."
            );
        }
        
        if (fechaHora == null) {
            throw new IllegalArgumentException(
                    "La fecha y hora son obligatorias."
            );
        }
        
        if (!Double.isFinite(monto) || monto <= 0) {
            throw new IllegalArgumentException(
                    "El monto debe ser mayor a cero."
            );
        }
        
        if (cuenta == null) {
            throw new IllegalArgumentException(
                    "La cuenta es obligatoria."
            );
        }
        
        this.idOperacion = idOperacion;
        this.fechaHora = fechaHora;
        this.monto = monto;
        this.cuenta = cuenta;
    }
    
    public String getIdOperacion() {
        return idOperacion;
    }
    
    public LocalDateTime getFechaHora() {
        return fechaHora;
    }
    
    public double getMonto() {
        return monto;
    }
    
    public CuentaBancaria getCuenta() {
        return cuenta;
    }
    
    public abstract void ejecutar();
}