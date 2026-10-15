package Logico;

public class ControlarCedula {
    
    private static final int[] MULTIPLICADORES = {
        
        2, 9, 8, 7, 6, 3, 4
            
            
    };
    
    public static boolean esCedulaValida(String cedula) {
        
        if (cedula == null) {
            return false;
        }
        
        cedula = cedula.replaceAll("[.\\-\\s]", "");
        
        if (!cedula.matches("\\d{8}")) {
            return false;
            
        }
        
        String numero = cedula.substring(0, 7);
        int digitoVerificador = Character.getNumericValue(cedula.charAt(7));
        
        int suma = 0;
        
        for (int i = 0; i < 7; i++) {
            
            int digito = Character.getNumericValue(numero.charAt(i));
            suma += digito * MULTIPLICADORES[i];
            
            
        }
        
        int resto = suma % 10;
        int resultado = (10 - resto) % 10;
        
        return resultado == digitoVerificador;
    }
    
    public static int calcularDigitoVerificador(String numero) {
        
        if (numero == null || !numero.matches("\\d{7}")) {
            
            throw new IllegalArgumentException(
                "El número debe contener exactamente 7 dígitos"
            );
            
        }
        
        int suma = 0;
        
        for (int i = 0; i < 7; i++) {
            
            int digito = Character.getNumericValue(numero.charAt(i));
            suma += digito * MULTIPLICADORES[i];
            
        }
        
        return (10 - (suma % 10)) % 10;
    }
}