package Logico;

import java.time.LocalDate;

public class CuentaAhorro extends CuentaBancaria {
    private double tasaInteres;

    public CuentaAhorro(String numeroCuenta, LocalDate fechaApertura, double saldo,
                        Sucursal sucursal, double tasaInteres) {
        super(numeroCuenta, fechaApertura, saldo, sucursal);
        this.tasaInteres = tasaInteres;
    }

    @Override
    public void depositar(double monto) { saldo += monto; }

    @Override
    public void retirar(double monto) { if (saldo >= monto) saldo -= monto; }
}
