package Logico;

import java.time.LocalDateTime;

public class Transferencia extends Operacion {
    
    private CuentaBancaria cuentaDestino;
    
    public Transferencia( String idOperacion, LocalDateTime fechaHora, double monto, CuentaBancaria cuentaOrigen, CuentaBancaria cuentaDestino) {
        
        super(idOperacion, fechaHora, monto, cuentaOrigen);
        
        if (cuentaDestino == null) {
            throw new IllegalArgumentException(
                    "La cuenta de destino es obligatoria."
            );
        }
        
        if (cuentaOrigen == cuentaDestino) {
            throw new IllegalArgumentException(
                    "Una cuenta no se puede autotransferir"
            );
        }
        
        this.cuentaDestino = cuentaDestino;
    }
    
    @Override
    public void ejecutar() {
        
        if (cuenta.getSaldo() < monto) {
            throw new IllegalArgumentException(
                    "Saldo insuficiente."
            );
        }
        
        cuenta.retirar(monto);
        cuentaDestino.depositar(monto);
    }
    
    public CuentaBancaria getCuentaOrigen() {
        return cuenta;
    }
    
    public CuentaBancaria getCuentaDestino() {
        return cuentaDestino;
    }
}