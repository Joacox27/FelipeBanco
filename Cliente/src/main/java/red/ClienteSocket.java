package red;

import Logico.Cliente;
import Logico.Funcionario;
import Logico.Prestamo;
import Logico.Sucursal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDate;
import java.util.ArrayList;

public class ClienteSocket {

    private Socket cliente;
    private BufferedReader entrada;
    private PrintWriter salida;


    // ============================================================
    // CONEXIÓN CON SERVIDOR
    // ============================================================

    public void conectar() throws IOException {

        cliente = new Socket("127.0.0.1", 2050);

        entrada = new BufferedReader(
                new InputStreamReader(cliente.getInputStream())
        );

        salida = new PrintWriter(
                cliente.getOutputStream(),
                true
        );

        System.out.println("Red: conectado al servidor.");
    }


    public void desconectar() {

        try {

            if (entrada != null) {
                entrada.close();
            }

            if (salida != null) {
                salida.close();
            }

            if (cliente != null) {
                cliente.close();
            }

            System.out.println("Red: conexión cerrada.");

        } catch (IOException e) {

            System.out.println(
                    "ERROR al cerrar la conexión: "
                    + e.getMessage()
            );
        }
    }


    // DRIVER


    public String probarDriver() throws IOException {

        salida.println("1");

        return entrada.readLine();
    }


   
    // CLIENTES
   

    /*
     * Protocolo:
     *
     * 2:documento:password:nombreCompleto:
     * fechaNacimiento:direccion:telefono
     *
     * Ejemplo:
     *
     * 2:12345678:1234:Joaquin Salazar:
     * 2009-05-10:Montevideo:099123456
    
    
    nombre completo tiene q ser atomico, osea nombre y apellido 8/10
     */
    public String nuevoCliente(Cliente c) throws IOException {

        String mensaje =
                "2:"
                + c.getDocumento() + ":"
                + c.getPassword() + ":"
                + c.getNombreCompleto() + ":"
                + c.getFechaNacimiento() + ":"
                + c.getDireccion() + ":"
                + c.getTelefono();

        salida.println(mensaje);

        return entrada.readLine();
    }


    /*
     * Protocolo:
     *
     * 3
     *
     * Respuesta:
     *
     * documento,password,nombreCompleto,
     * fechaNacimiento,direccion,telefono|
     */
    public ArrayList<Cliente> mostrarClientes()
            throws IOException {

        salida.println("3");

        String respuesta = entrada.readLine();

        ArrayList<Cliente> lista = new ArrayList<>();

        if (respuesta == null
                || respuesta.equals("No hay clientes registrados.")
                || respuesta.isEmpty()) {

            return lista;
        }

        String[] clientes = respuesta.split("\\|");

        for (String datos : clientes) {

            if (datos.isEmpty()) {
                continue;
            }

            String[] partes = datos.split(",");

            if (partes.length >= 6) {

                String documento = partes[0];
                int password = Integer.parseInt(partes[1]);
                String nombreCompleto = partes[2];
                LocalDate fechaNacimiento =
                        LocalDate.parse(partes[3]);
                String direccion = partes[4];
                String telefono = partes[5];

                Cliente c = new Cliente(
                        documento,
                        password,
                        nombreCompleto,
                        fechaNacimiento,
                        direccion,
                        telefono
                );

                lista.add(c);
            }
        }

        return lista;
    }


    // FUNCIONARIOS


    /*
     * Protocolo:
     *
     * 4:nombre:sucursal:rol
     */
    public String nuevoFuncionario(
            String nombre,
            String nombreSucursal,
            String rol) throws IOException {

        String mensaje =
                "4:"
                + nombre + ":"
                + nombreSucursal + ":"
                + rol;

        salida.println(mensaje);

        return entrada.readLine();
    }


    /*
     * Protocolo:
     *
     * 5
     */
    
        public ArrayList<Funcionario> mostrarFuncionarios()
                throws IOException {

            salida.println("5");

            String respuesta = entrada.readLine();

            ArrayList<Funcionario> lista = new ArrayList<>();

            if (respuesta == null
                    || respuesta.equals("No hay funcionarios registrados.")
                    || respuesta.isEmpty()) {

                return lista;
            }

            /*
             * Formato recibido:
             *
             * idFuncionario,nombre,rol,fechaIngreso,
             * idSucursal,nombreSucursal,direccion,ciudad
             *
             * Cada funcionario se separa con el palo "|".
             */

            String[] funcionarios = respuesta.split("\\|");

            for (String datos : funcionarios) {

                if (datos.isEmpty()) {
                    continue;
                }

                String[] partes = datos.split(",");

                if (partes.length >= 8) {

                    String idFuncionario = partes[0];
                    String nombre = partes[1];
                    String rol = partes[2];

                    LocalDate fechaIngreso =
                            LocalDate.parse(partes[3]);

                    int idSucursal =
                            Integer.parseInt(partes[4]);

                    String nombreSucursal = partes[5];
                    String direccion = partes[6];
                    String ciudad = partes[7];

                    Sucursal sucursal = new Sucursal(
                            idSucursal,
                            nombreSucursal,
                            direccion,
                            ciudad
                    );

                    Funcionario funcionario = new Funcionario(
                            idFuncionario,
                            nombre,
                            rol,
                            fechaIngreso,
                            sucursal
                    );

                    lista.add(funcionario);
                }
            }

            return lista;
        }


    // SUCURSALES


    /*
     * Protocolo:
     *
     * 6
     */
    public ArrayList<Sucursal> mostrarSucursales()
            throws IOException {

        salida.println("6");

        String respuesta = entrada.readLine();

        /*
         * Esta parte necesita ajustarse al constructor REAL
         * de Sucursal.
         */
        ArrayList<Sucursal> lista = new ArrayList<>();

        return lista;
    }

    // LOGIN

    /*
     * Protocolo:
     *
     * 7:CI:password
     *
     * Respuesta correcta:
     *
     * OK|CI|password|nombreCompleto|
     * fechaNacimiento|direccion|telefono
     *
     * Respuesta incorrecta:
     *
     * ERROR|Credenciales incorrectas
     */
    public Cliente login(
            String documento,
            int password) throws IOException {

        String mensaje =
                "7:"
                + documento + ":"
                + password;

        salida.println(mensaje);

        String respuesta = entrada.readLine();

        if (respuesta == null) {
            return null;
        }

        if (respuesta.startsWith("ERROR|")) {
            return null;
        }

        String[] partes = respuesta.split("\\|");

        if (partes.length >= 7
                && partes[0].equals("OK")) {

            String ci = partes[1];
            int pass = Integer.parseInt(partes[2]);
            String nombreCompleto = partes[3];

            LocalDate fechaNacimiento =
                    LocalDate.parse(partes[4]);

            String direccion = partes[5];
            String telefono = partes[6];

            return new Cliente(
                    ci,
                    pass,
                    nombreCompleto,
                    fechaNacimiento,
                    direccion,
                    telefono
            );
        }

        return null;
    }



    // PRESTAMOS
  

    /*
     * Protocolo q sigue:
     *
     * 8:monto:cuotas:documentoCliente
     */
    public String solicitarPrestamo(
            double monto,
            int cuotas,
            String documentoCliente) throws IOException {

        String mensaje =
                "8:"
                + monto + ":"
                + cuotas + ":"
                + documentoCliente;

        salida.println(mensaje);

        return entrada.readLine();
    }


    /*
     * Protocolo:
     *
     * 9
     */
        public ArrayList<Prestamo> mostrarPrestamos(
             Cliente cliente) throws IOException {

         salida.println("9:" + cliente.getDocumento());

         String respuesta = entrada.readLine();

         ArrayList<Prestamo> lista = new ArrayList<>();

         if (respuesta == null
                 || respuesta.equals("No hay préstamos registrados.")
                 || respuesta.isEmpty()) {

             return lista;
         }

         String[] prestamos = respuesta.split("\\|");

         for (String datos : prestamos) {

             if (datos.isEmpty()) {
                 continue;
             }

             String[] partes = datos.split(",");

             if (partes.length >= 5) {

                 String idPrestamo = partes[0];
                 double monto = Double.parseDouble(partes[1]);
                 double tasaInteres = Double.parseDouble(partes[2]);
                 LocalDate fechaOtorgamiento =
                 LocalDate.parse(partes[3]);
                 int cuotas = Integer.parseInt(partes[4]);

                 Prestamo p = new Prestamo(
                         idPrestamo,
                         monto,
                         tasaInteres,
                         fechaOtorgamiento,
                         cuotas,
                         cliente
                 );

                 lista.add(p);
             }
         }

         return lista;
     }

    // SALIR
 

    public String salir() throws IOException {

        salida.println("0");

        return entrada.readLine();
    }
}
