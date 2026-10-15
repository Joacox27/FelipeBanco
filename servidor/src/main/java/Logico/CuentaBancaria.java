package Logico;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class CuentaBancaria {
    protected String numeroCuenta;
    protected LocalDate fechaApertura;
    protected double saldo;
    protected Sucursal sucursal;
    protected List<Operacion> operaciones;
    
    
    
    public CuentaBancaria(String numeroCuenta, LocalDate fechaApertura, double saldo, Sucursal sucursal) {
        this.numeroCuenta = numeroCuenta;
        this.fechaApertura = fechaApertura;
        this.saldo = saldo;
        this.sucursal = sucursal;
        this.operaciones = new ArrayList<>();
    }
    
    public abstract void depositar(double monto);
    
    public abstract void retirar(double monto);
    
    public double getSaldo() {
        return saldo;
    }
    
    public String getNumeroCuenta() {
    return numeroCuenta;
    }
    
    public LocalDate getFechaApertura() {
        return fechaApertura;
    }
    
    public Sucursal getSucursal() {
        return sucursal;
    }
    
    public abstract String getTipo();
    
    
}