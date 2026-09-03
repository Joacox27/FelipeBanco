package Logico;

import java.time.LocalDate;

public class Transferencia {

    private CuentaBancaria cuentaOrigen;
    private CuentaBancaria cuentaDestino;
    private double monto;
    private LocalDate fecha;

    public Transferencia(CuentaBancaria cuentaOrigen,
                         CuentaBancaria cuentaDestino,
                         double monto) {

        this.cuentaOrigen = cuentaOrigen;
        this.cuentaDestino = cuentaDestino;
        this.monto = monto;
        this.fecha = LocalDate.now();
    }

    public void realizarTransferencia() {

        if (monto <= 0) {
            throw new IllegalArgumentException(
                    "El monto debe ser mayor a 0."
            );
        }

        if (cuentaOrigen == cuentaDestino) {
            throw new IllegalArgumentException(
                    "La cuenta origen y destino no pueden ser la misma."
            );
        }

        if (cuentaOrigen.saldo < monto) {
            throw new IllegalArgumentException(
                    "Saldo insuficiente."
            );
        }

        cuentaOrigen.retirar(monto);
        cuentaDestino.depositar(monto);
    }

    public CuentaBancaria getCuentaOrigen() {
        return cuentaOrigen;
    }

    public CuentaBancaria getCuentaDestino() {
        return cuentaDestino;
    }

    public double getMonto() {
        return monto;
    }

    public LocalDate getFecha() {
        return fecha;
    }
}