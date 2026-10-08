package Logico;

import java.time.LocalDate;

public class CuentaCorriente extends CuentaBancaria {
    private double limiteDescubierto;

    public CuentaCorriente(String numeroCuenta, LocalDate fechaApertura, double saldo,
                           Sucursal sucursal, double limiteDescubierto) {
        super(numeroCuenta, fechaApertura, saldo, sucursal);
        this.limiteDescubierto = limiteDescubierto;
    }

    @Override
    public void depositar(double monto) { saldo += monto; }

    @Override
    public void retirar(double monto) {
        if (saldo + limiteDescubierto >= monto) saldo -= monto;
    }
}
