package Logico;

import red.ClienteSocket;

import java.io.IOException;
import java.util.ArrayList;

public class Fachada {

    private static Fachada singleton;

    private final ClienteSocket clienteSocket;

    // el Cliente que inició sesión en este programa cliente
    private Cliente cliente;

    private Fachada() {

        clienteSocket = new ClienteSocket();

        try {

            clienteSocket.conectar();

        } catch (IOException e) {

            throw new RuntimeException(
                    "No se pudo conectar con el servidor.",
                    e
            );
        }
    }

    public static Fachada getInstancia() {

        if (singleton == null) {
            singleton = new Fachada();
        }

        return singleton;
    }



    // FUNCIONARIOS


    public void nuevoFuncionario(
            String nombre,
            String nombreSucursal,
            String rol) {

        try {

            String respuesta = clienteSocket.nuevoFuncionario(
                    nombre,
                    nombreSucursal,
                    rol
            );

            comprobarRespuesta(respuesta);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Error de comunicación con el servidor.",
                    e
            );
        }
    }


    public ArrayList<Funcionario> mostrarFuncionarios() {

        try {

            return clienteSocket.mostrarFuncionarios();

        } catch (IOException e) {

            throw new RuntimeException(
                    "Error al obtener los funcionarios.",
                    e
            );
        }
    }



    // CLIENTES


    public void nuevoCliente(
            Cliente c,
            int password) {

        try {

            String respuesta = clienteSocket.nuevoCliente(
                    c,
                    password
            );

            comprobarRespuesta(respuesta);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Error de comunicación con el servidor.",
                    e
            );
        }
    }


    public ArrayList<Cliente> mostrarClientes() {

        try {

            return clienteSocket.mostrarClientes();

        } catch (IOException e) {

            throw new RuntimeException(
                    "Error al obtener los clientes.",
                    e
            );
        }
    }



    // SUCURSALES


    public ArrayList<Sucursal> mostrarSucursales() {

        try {

            return clienteSocket.mostrarSucursales();

        } catch (IOException e) {

            throw new RuntimeException(
                    "Error al obtener las sucursales.",
                    e
            );
        }
    }


    // LOGIN


    public Cliente login(
            String documento,
            int password) {

        try {

            Cliente c = clienteSocket.login(
                    documento,
                    password
            );

            if (c != null) {
                cliente = c;
            }

            return c;

        } catch (IOException e) {

            throw new RuntimeException(
                    "Error de comunicación con el servidor.",
                    e
            );
        }
    }



    // PRESTAMOS


    public void solicitarPrestamo(
            double monto,
            int cuotas) {

        if (cliente == null) {

            throw new IllegalStateException(
                    "No hay un cliente autenticado."
            );
        }

        try {

            String respuesta = clienteSocket.solicitarPrestamo(
                    monto,
                    cuotas,
                    cliente.getDocumento()
            );

            comprobarRespuesta(respuesta);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Error al solicitar el préstamo.",
                    e
            );
        }
    }


    public ArrayList<Prestamo> mostrarPrestamos() {

        if (cliente == null) {

            throw new IllegalStateException(
                    "No hay un cliente autenticado."
            );
        }

        try {

            return clienteSocket.mostrarPrestamos(cliente);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Error al obtener los préstamos.",
                    e
            );
        }
    }



    // CALCULO DE TASA


    public double calcularTasaInteres(int cuotas) {

        if (cuotas <= 12) {

            return 10;

        } else if (cuotas <= 24) {

            return 15;

        } else {

            return 20;
        }
    }

    // RESPUESTAS DEL SERVIDOR


    private void comprobarRespuesta(String respuesta) {

        if (respuesta == null) {

            throw new RuntimeException(
                    "El servidor cerró la conexión."
            );
        }

        if (respuesta.startsWith("ERROR|")) {

            throw new IllegalArgumentException(
                    respuesta.substring(6)
            );
        }
    }
}

