package Logico;

import red.ClienteSocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

public class Fachada {
    
    private static Fachada singleton;
    
    private final ClienteSocket clienteSocket;
    
    
    private Cliente cliente;
    
    private Fachada() {
        
        clienteSocket = new ClienteSocket();
        
        try {
            
            clienteSocket.conectar();
            
        } catch (IOException e) {

            throw new RuntimeException(
                    "No se pudo conectar con el servidor felipe banco.",
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
    
    
    //Funcionariossss
    
    public void nuevoFuncionario(
            String nombre,
            String nombreSucursal,
            String rol) {
        
        try {
            
            String respuesta = clienteSocket.nuevoFuncionario(nombre, nombreSucursal, rol);
            
            comprobarRespuesta(respuesta);
            
        } catch (IOException e) {
            
            throw new RuntimeException(
                    "Error de comunicación con el servidor.",
                    e
            );
        }
    }
    
    public void modificarFuncionario(
            String idFuncionario,
            String nombre,
            String nombreSucursal,
            String rol) {

        try {

            String respuesta = clienteSocket.modificarFuncionario(
                    idFuncionario,
                    nombre,
                    nombreSucursal,
                    rol
            );

            comprobarRespuesta(respuesta);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Error de comunicación al modificar el funcionario.",
                    e
            );
        }
    }


    public void eliminarFuncionario(String idFuncionario) {

        try {

            String respuesta = clienteSocket.eliminarFuncionario(
                    idFuncionario
            );

            comprobarRespuesta(respuesta);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Error de comunicación al eliminar el funcionario.",
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
    
    
    
    
    //Clientesss
    
    public void nuevoCliente( Cliente c, int password) {
        
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
    
    public void modificarCliente(Cliente cliente) {
        
        try {
            String respuesta = clienteSocket.modificarCliente(cliente);
            
            comprobarRespuesta(respuesta);
            
        } catch (IOException e) {
            
            throw new RuntimeException("Error de comunicación al modificar el cliente.", e);
            
        }
    }


    public void eliminarCliente(String documento) {
        
        try {
            String respuesta = clienteSocket.eliminarCliente(documento);
            
            comprobarRespuesta(respuesta);
            
        } catch (IOException e) {
            throw new RuntimeException("Error de comunicación al eliminar el cliente.", e);
        }
    }
    
    
    //Sucursalesss
    
    public ArrayList<Sucursal> mostrarSucursales() {
        
        try {
            
            return clienteSocket.mostrarSucursales();
            
        } catch (IOException e) {
            
            throw new RuntimeException(
                    "Error al obtener las sucursales.", e
            );
        }
    }
    
    public void nuevaSucursal(
            String nombre,
            String direccion,
            String ciudad) {

        try {

            String respuesta = clienteSocket.nuevaSucursal(
                    nombre,
                    direccion,
                    ciudad
            );

            comprobarRespuesta(respuesta);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Error de comunicación al registrar la sucursal.",
                    e
            );
        }
    }


    public void modificarSucursal(
            int idSucursal,
            String nombre,
            String direccion,
            String ciudad) {

        try {

            String respuesta = clienteSocket.modificarSucursal(
                    idSucursal,
                    nombre,
                    direccion,
                    ciudad
            );

            comprobarRespuesta(respuesta);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Error de comunicación al modificar la sucursal.",
                    e
            );
        }
    }


    public void eliminarSucursal(int idSucursal) {

        try {

            String respuesta = clienteSocket.eliminarSucursal(
                    idSucursal
            );

            comprobarRespuesta(respuesta);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Error de comunicación al eliminar la sucursal.",
                    e
            );
        }
    }
    
    
    

        //cuentasss
    
    public String nuevaCuenta(
            String tipo,
            int idSucursal,
            double tasaInteres,
            double limiteDescubierto) {

        try {

            String respuesta = clienteSocket.nuevaCuenta(
                    tipo,
                    idSucursal,
                    tasaInteres,
                    limiteDescubierto
            );

            comprobarRespuesta(respuesta);

            return respuesta;

        } catch (IOException e) {

            throw new RuntimeException(
                    "Error de comunicación al crear la cuenta bancaria.",
                    e
            );
        }
    }

    public ArrayList<CuentaBancaria> mostrarCuentasCliente(
            String documentoCliente) {

        try {

            return clienteSocket.mostrarCuentasCliente(
                    documentoCliente
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Error al obtener las cuentas del cliente.",
                    e
            );
        }
    }

    public ArrayList<CuentaBancaria> mostrarCuentas() {

        try {

            return clienteSocket.mostrarCuentas();

        } catch (IOException e) {

            throw new RuntimeException(
                    "Error al obtener las cuentas bancarias.",
                    e
            );
        }
    }
    
    public CuentaBancaria buscarCuenta(String numeroCuenta) {

        try {

            return clienteSocket.buscarCuenta(numeroCuenta);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Error al buscar la cuenta bancaria.",
                    e
            );
        }
    }
    
    public void modificarCuenta(
            String numeroCuenta,
            int idSucursal,
            double tasaInteres,
            double limiteDescubierto) {

        try {

            String respuesta = clienteSocket.modificarCuenta(
                    numeroCuenta,
                    idSucursal,
                    tasaInteres,
                    limiteDescubierto
            );

            comprobarRespuesta(respuesta);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Error de comunicación al modificar la cuenta.",
                    e
            );
        }
    }
    
    public ArrayList<Operacion> mostrarHistorialOperacionesCliente() {

        if (cliente == null) {
            throw new IllegalStateException(
                "Debes iniciar sesión para consultar el historial."
            );
        }

        try {

            ArrayList<CuentaBancaria> cuentas =
                clienteSocket.mostrarCuentasCliente(
                    cliente.getDocumento()
                );

            Map<String, Operacion> operacionesUnicas =
                new LinkedHashMap<>();

            for (CuentaBancaria cuenta : cuentas) {

                ArrayList<Operacion> operacionesCuenta =
                    clienteSocket.mostrarOperaciones(
                        cuenta.getNumeroCuenta(),
                        cliente.getDocumento()
                    );

                for (Operacion operacion : operacionesCuenta) {

                    operacionesUnicas.putIfAbsent(
                        operacion.getIdOperacion(),
                        operacion
                    );
                }
            }

            ArrayList<Operacion> historial =
                new ArrayList<>(operacionesUnicas.values());

            historial.sort(
                Comparator.comparing(Operacion::getFechaHora).reversed()
            );

            return historial;

        } catch (IOException e) {

            throw new RuntimeException(
                "Error al obtener el historial de operaciones.",
                e
            );
        }
    }
    
    
    public void eliminarCuenta(String numeroCuenta) {

        try {

            String respuesta = clienteSocket.eliminarCuenta(
                    numeroCuenta
            );

            comprobarRespuesta(respuesta);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Error de comunicación al eliminar la cuenta bancaria.",
                    e
            );
        }
    }
    
    public void asociarTitular(
            String numeroCuenta,
            String documentoCliente) {

        try {

            String respuesta = clienteSocket.asociarTitular(
                    numeroCuenta,
                    documentoCliente
            );

            comprobarRespuesta(respuesta);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Error de comunicación al asociar el titular.",
                    e
            );
        }
    }


    public void desasociarTitular(
            String numeroCuenta,
            String documentoCliente) {

        try {

            String respuesta = clienteSocket.desasociarTitular(
                    numeroCuenta,
                    documentoCliente
            );

            comprobarRespuesta(respuesta);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Error de comunicación al desasociar el titular.",
                    e
            );
        }
    }
    
    public Cliente getClienteActual() {

        if (cliente == null) {
            throw new IllegalStateException(
                "No hay ningún cliente autenticado."
            );
        }

        return cliente;
    }
    
    
    
    public Cliente login(String documento, int password) {
        
        try {
            
            Cliente c = clienteSocket.login(documento, password);
            
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
    
    
    
    
    
    //Prestamossss
    
    public void solicitarPrestamo(
            double monto,
            int cuotas) {
        
        if (cliente == null) {

            throw new IllegalStateException(
                    "No hay un cliente autenticado."
            );
        }
        
        try {
            
            String respuesta = clienteSocket.solicitarPrestamo(monto, cuotas, cliente.getDocumento());
            
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

    //Operaciones
    
    public ArrayList<Operacion> mostrarOperaciones(
            String numeroCuenta) {

        if (cliente == null) {
            throw new IllegalStateException(
                "Debes iniciar sesión para consultar las operaciones."
            );
        }

        try {
            return clienteSocket.mostrarOperaciones(
                numeroCuenta,
                cliente.getDocumento()
            );

        } catch (IOException e) {
            throw new RuntimeException(
                "Error al consultar las operaciones de la cuenta.",
                e
            );
        }
    }


    public Operacion buscarOperacion(String idOperacion) {

        try {
            return clienteSocket.buscarOperacion(idOperacion);

        } catch (IOException e) {
            throw new RuntimeException(
                    "Error al buscar la operación.",
                    e
            );
        }
    }
    
    public void depositar(String numeroCuenta, double monto) {

        if (cliente == null) {
            throw new IllegalStateException(
                    "Debes iniciar sesión para realizar un depósito."
            );
        }

        try {

            String respuesta = clienteSocket.depositar(
                    numeroCuenta,
                    monto,
                    cliente.getDocumento()
            );

            comprobarRespuesta(respuesta);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Error de comunicación al realizar el depósito.",
                    e
            );
        }
    }


    public void retirar(String numeroCuenta, double monto) {

        if (cliente == null) {
            throw new IllegalStateException(
                    "Debes iniciar sesión para realizar un retiro."
            );
        }

        try {

            String respuesta = clienteSocket.retirar(
                    numeroCuenta,
                    monto,
                    cliente.getDocumento()
            );

            comprobarRespuesta(respuesta);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Error de comunicación al realizar el retiro.",
                    e
            );
        }
    }


    public void transferir(
            String cuentaOrigen,
            String cuentaDestino,
            double monto) {

        if (cliente == null) {
            throw new IllegalStateException(
                    "Debes iniciar sesión para realizar una transferencia."
            );
        }

        try {

            String respuesta = clienteSocket.transferir(
                    cuentaOrigen,
                    cuentaDestino,
                    monto,
                    cliente.getDocumento()
            );

            comprobarRespuesta(respuesta);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Error de comunicación al realizar la transferencia.",
                    e
            );
        }
    }
    


    //utiles

    public double calcularTasaInteres(int cuotas) {

        if (cuotas <= 12) {

            return 10;

        } else if (cuotas <= 24) {

            return 15;

        } else {

            return 20;
        }
    }


    


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

