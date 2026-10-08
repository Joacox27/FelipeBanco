package Logico;

import java.time.LocalDateTime;

public class Retiro extends Operacion {
    public Retiro(String idOperacion, LocalDateTime fechaHora, double monto, CuentaBancaria cuenta) {
        super(idOperacion, fechaHora, monto, cuenta);
    }

    @Override
    public void ejecutar() { cuenta.retirar(monto); }
}
