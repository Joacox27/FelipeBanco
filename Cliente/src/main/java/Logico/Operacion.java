package Logico;

import java.time.LocalDateTime;

public abstract class Operacion {
    protected String idOperacion;
    protected LocalDateTime fechaHora;
    protected double monto;
    protected CuentaBancaria cuenta;

    public Operacion(String idOperacion, LocalDateTime fechaHora, double monto, CuentaBancaria cuenta) {
        this.idOperacion = idOperacion;
        this.fechaHora = fechaHora;
        this.monto = monto;
        this.cuenta = cuenta;
    }

    public abstract void ejecutar();
}
