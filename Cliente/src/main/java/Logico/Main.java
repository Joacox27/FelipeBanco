package Logico;

import Presentacion.Login;
import java.time.LocalDate;

/**
 *
 * @author FELIPEBANCO
 */
public class Main {

    public static void main(String[] args) {
        
        Fachada.getInstancia();
        
        Login ppal= new Login();
        ppal.setVisible(true);
    }
}
