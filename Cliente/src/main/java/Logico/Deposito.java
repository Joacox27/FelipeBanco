package Logico;

import java.time.LocalDateTime;

public class Deposito extends Operacion {
    public Deposito(String idOperacion, LocalDateTime fechaHora, double monto, CuentaBancaria cuenta) {
        super(idOperacion, fechaHora, monto, cuenta);
    }

    @Override
    public void ejecutar() { 
        
        cuenta.depositar(monto);
    
    }
}
