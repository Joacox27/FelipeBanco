package Logico;

import Datos.Conexion;
import Datos.ClientesDAO;
import Datos.FuncionariosDAO;
import Datos.SucursalesDAO;
import Datos.PrestamosDAO;
import Datos.OperacionesDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

public class Fachada {

    private static Fachada instancia;

    private final ClientesDAO clientesDAO;
    private final FuncionariosDAO funcionariosDAO;
    private final SucursalesDAO sucursalesDAO;
    private final PrestamosDAO prestamosDAO;
    private final OperacionesDAO operacionesDAO;

    private Fachada() {
        clientesDAO = new ClientesDAO();
        funcionariosDAO = new FuncionariosDAO();
        sucursalesDAO = new SucursalesDAO();
        prestamosDAO = new PrestamosDAO();
        operacionesDAO = new OperacionesDAO();
    }

    public static synchronized Fachada getInstancia() {
        if (instancia == null) {
            instancia = new Fachada();
        }
        return instancia;
    }


 
    public String probarDriver() {
        try (Connection conexion = Conexion.getConexion()) {
            if (conexion != null && !conexion.isClosed()) {
                return "OK|Conexión con MySQL correcta.";
            }

            return "ERROR|No se pudo establecer la conexión con MySQL.";

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al comprobar la conexión con MySQL.", e
            );
        }
    }


    
    

    public void nuevoCliente(Cliente cliente, int password) {

        if (cliente == null) {
            throw new IllegalArgumentException(
                    "Los datos del cliente son obligatorios."
            );
        }

        if (password < 0) {
            throw new IllegalArgumentException(
                    "La contraseña no es válida."
            );
        }

        try {
            Cliente existente =
                    clientesDAO.buscarPorDocumento(cliente.getDocumento());

            if (existente != null) {
                throw new IllegalArgumentException(
                        "Ya existe un cliente con ese documento."
                );
            }

            clientesDAO.insertar(cliente, password);

        } catch (SQLException e) {
            throw new RuntimeException(
                    "No se pudo registrar el cliente.", e
            );
        }
    }

    public ArrayList<Cliente> mostrarClientes() {
        try {
            return clientesDAO.listar();
        } catch (SQLException e) {
            throw new RuntimeException(
                    "No se pudieron obtener los clientes.", e
            );
        }
    }

    public Cliente login(String documento, int password) {

        if (documento == null || documento.isBlank() || password < 0) {
            return null;
        }

        try {
            return clientesDAO.autenticar(documento, password);
        } catch (SQLException e) {
            throw new RuntimeException(
                    "No se pudo comprobar la identidad del cliente.", e
            );
        }
    }


    
    
    
 
    public void nuevoFuncionario(
            String nombre,
            String nombreSucursal,
            String rol) {

        if (nombre == null || nombre.isBlank()
                || nombreSucursal == null || nombreSucursal.isBlank()
                || rol == null || rol.isBlank()) {

            throw new IllegalArgumentException(
                    "Todos los datos del funcionario son obligatorios."
            );
        }

        try {
            funcionariosDAO.insertar(nombre, nombreSucursal, rol);
        } catch (SQLException e) {
            throw new RuntimeException(
                    "No se pudo registrar el funcionario.", e
            );
        }
    }

    public ArrayList<Funcionario> mostrarFuncionarios() {
        try {
            return funcionariosDAO.listar();
        } catch (SQLException e) {
            throw new RuntimeException(
                    "No se pudieron obtener los funcionarios.", e
            );
        }
    }


    // SUCURSALES
  

    public ArrayList<Sucursal> mostrarSucursales() {
        try {
            return sucursalesDAO.listar();
        } catch (SQLException e) {
            throw new RuntimeException(
                    "No se pudieron obtener las sucursales.", e
            );
        }
    }


    // PRESTAMOS


    public void solicitarPrestamo(
            double monto,
            int cuotas,
            String documentoCliente) {

        if (!Double.isFinite(monto) || monto <= 0) {
            throw new IllegalArgumentException(
                    "El monto debe ser mayor a cero."
            );
        }

        if (cuotas <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad de cuotas debe ser mayor a cero."
            );
        }

        if (documentoCliente == null || documentoCliente.isBlank()) {
            throw new IllegalArgumentException(
                    "El documento del cliente es obligatorio."
            );
        }

        try {
            Cliente cliente =                                                                                                                        
                    clientesDAO.buscarPorDocumento(documentoCliente);

            if (cliente == null) {
                throw new IllegalArgumentException(
                        "El cliente no existe."
                );
            }

            double tasa = calcularTasaInteres(cuotas);

            String idPrestamo =
                    "P-" + UUID.randomUUID();

            Prestamo prestamo = new Prestamo(
                    idPrestamo,
                    monto,
                    tasa,
                    LocalDate.now(),
                    cuotas,
                    cliente
            );

            prestamosDAO.insertar(prestamo);

        } catch (SQLException e) {
            throw new RuntimeException(
                    "No se pudo registrar el préstamo.", e
            );
        }
    }

    public ArrayList<Prestamo> mostrarPrestamos(Cliente cliente) {

        if (cliente == null) {
            throw new IllegalArgumentException(
                    "El cliente es obligatorio."
            );
        }

        try {
            return prestamosDAO.listarPorCliente(cliente);
        } catch (SQLException e) {
            throw new RuntimeException(
                    "No se pudieron obtener los préstamos.", e
            );
        }
    }

    public double calcularTasaInteres(int cuotas) {

        if (cuotas <= 12) {
            return 10;
        } else if (cuotas <= 24) {
            return 15;
        } else {
            return 20;
        }
    }


    // OPERACIONES BANCARIAS


    public void depositar(
            String numeroCuenta,
            double monto,
            String documentoCliente) {

        validarOperacion(numeroCuenta, monto, documentoCliente);

        try {
            operacionesDAO.registrarDeposito(
                    numeroCuenta, monto, documentoCliente
            );
        } catch (SQLException e) {
            throw new RuntimeException(
                    "No se pudo realizar el depósito.", e
            );
        }
    }

    public void retirar(
            String numeroCuenta,
            double monto,
            String documentoCliente) {

        validarOperacion(numeroCuenta, monto, documentoCliente);

        try {
            operacionesDAO.registrarRetiro(
                    numeroCuenta, monto, documentoCliente
            );
        } catch (SQLException e) {
            throw new RuntimeException(
                    "No se pudo realizar el retiro.", e
            );
        }
    }

    public void transferir(
            String cuentaOrigen,
            String cuentaDestino,
            double monto,
            String documentoCliente) {

        validarOperacion(cuentaOrigen, monto, documentoCliente);

        if (cuentaDestino == null || cuentaDestino.isBlank()) {
            throw new IllegalArgumentException(
                    "La cuenta de destino es obligatoria."
            );
        }

        if (cuentaOrigen.equals(cuentaDestino)) {
            throw new IllegalArgumentException(
                    "Las cuentas deben ser diferentes."
            );
        }

        try {
            operacionesDAO.registrarTransferencia(
                    cuentaOrigen,
                    cuentaDestino,
                    monto,
                    documentoCliente
            );
        } catch (SQLException e) {
            throw new RuntimeException(
                    "No se pudo realizar la transferencia.", e
            );
        }
    }

    private void validarOperacion(
            String numeroCuenta,
            double monto,
            String documentoCliente) {

        if (numeroCuenta == null || numeroCuenta.isBlank()) {
            throw new IllegalArgumentException(
                    "El número de cuenta es obligatorio."
            );
        }

        if (!Double.isFinite(monto) || monto <= 0) {
            throw new IllegalArgumentException(
                    "El monto debe ser mayor a cero."
            );
        }

        if (documentoCliente == null || documentoCliente.isBlank()) {
            throw new IllegalArgumentException(
                    "El documento del cliente es obligatorio."
            );
        }
    }
}