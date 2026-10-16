package Logico;

import Datos.Conexion;
import Datos.ClientesDAO;
import Datos.FuncionariosDAO;
import Datos.SucursalesDAO;
import Datos.PrestamosDAO;
import Datos.OperacionesDAO;
import Datos.CuentasDAO;

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
    private final CuentasDAO cuentasDAO;
    
    
    private Fachada() {
        clientesDAO = new ClientesDAO();
        funcionariosDAO = new FuncionariosDAO();
        sucursalesDAO = new SucursalesDAO();
        prestamosDAO = new PrestamosDAO();
        operacionesDAO = new OperacionesDAO();
        cuentasDAO = new CuentasDAO();
    }

    public static synchronized Fachada getInstancia() {
        if (instancia == null) {
            instancia = new Fachada();
        }
        return instancia;
    }

//conectar con la base de datos
 
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


    
    //clientes

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

    public void modificarCliente(Cliente cliente) {
        
        if (cliente == null) {
            throw new IllegalArgumentException(
                    "Los datos del cliente son obligatorios."
            );
        }
        
        if (cliente.getDocumento() == null
                || cliente.getDocumento().isBlank()
                 || cliente.getNombre() == null
                  || cliente.getNombre().isBlank()
                   || cliente.getApellido() == null
                    || cliente.getApellido().isBlank()
                     || cliente.getFechaNacimiento() == null
                    || cliente.getCalle() == null
                   || cliente.getCalle().isBlank()
                  || cliente.getNumero() == null
                 || cliente.getNumero().isBlank()
                || cliente.getCiudad() == null
               || cliente.getCiudad().isBlank()) {
            
            throw new IllegalArgumentException( "Todos los datos obligatorios del cliente TIENEN QUE ESTAR.");
        }
        
        try {
            clientesDAO.modificar(cliente);
            
        } catch (SQLException e) {
            
            throw new RuntimeException("No se pudo modificar el cliente: " + e.getMessage(), e);
        }
    }


    public void eliminarCliente(String documento) {
        
        if (documento == null || documento.isBlank()) {
            throw new IllegalArgumentException(
                    "El documento del cliente es obligatorio."
            );
        }

        try {
            clientesDAO.eliminar(documento);

        } catch (SQLException e) {
            
            throw new RuntimeException(e.getMessage(), e);
            
        }
    }
    
    
    public ArrayList<Cliente> mostrarClientes() {
        try {
            
            return clientesDAO.listar();
            
        } catch (SQLException e) {
            
            throw new RuntimeException("No se pudieron obtener los clientes.", e);
            
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


    
    
    //funcionarios
 
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
    
    public void modificarFuncionario(
            int idFuncionario,
            String nombre,
            String nombreSucursal,
            String rol) {

        if (idFuncionario <= 0) {
            throw new IllegalArgumentException(
                    "El identificador del funcionario no es válido."
            );
        }

        if (nombre == null || nombre.isBlank()
                || nombreSucursal == null || nombreSucursal.isBlank()
                || rol == null || rol.isBlank()) {

            throw new IllegalArgumentException(
                    "Todos los datos del funcionario son obligatorios."
            );
        }

        try {

            funcionariosDAO.modificar(
                    idFuncionario,
                    nombre,
                    nombreSucursal,
                    rol
            );

        } catch (SQLException e) {

            throw new RuntimeException(
                    "No se pudo modificar el funcionario: " + e.getMessage(),
                    e
            );
        }
    }
    
    public void eliminarFuncionario(int idFuncionario) {

        if (idFuncionario <= 0) {
            throw new IllegalArgumentException(
                    "El identificador del funcionario no es válido."
            );
        }

        try {

            funcionariosDAO.eliminar(idFuncionario);

        } catch (SQLException e) {

            throw new RuntimeException(
                    "No se pudo eliminar el funcionario: " + e.getMessage(),
                    e
            );
        }
    }
    
    

    //metodos para sucursales
  

    public ArrayList<Sucursal> mostrarSucursales() {
        try {
            return sucursalesDAO.listar();
        } catch (SQLException e) {
            throw new RuntimeException(
                    "No se pudieron obtener las sucursales.", e
            );
        }
    }

    public void nuevaSucursal(String nombre, String direccion, String ciudad) {
        
        if (nombre == null || nombre.isBlank()
                || direccion == null || direccion.isBlank()
                || ciudad == null || ciudad.isBlank()) {

            throw new IllegalArgumentException(
                    "Todos los datos de la sucursal son obligatorios."
            );
        }
        
        try {
            
            sucursalesDAO.insertar(nombre, direccion, ciudad);
            
        } catch (SQLException e) {
            
            throw new RuntimeException(
                    "No se pudo registrar la sucursal: " + e.getMessage(),
                    e
            );
        }
    }
    
    public void modificarSucursal(
            int idSucursal,
            String nombre,
            String direccion,
            String ciudad) {
        
        if (idSucursal <= 0) {
            throw new IllegalArgumentException(
                    "El identificador de la sucursal no es válido."
            );
        }
        
        if (nombre == null || nombre.isBlank() || direccion == null || direccion.isBlank() || ciudad == null || ciudad.isBlank()) {
            throw new IllegalArgumentException("todos los datos de la sucursal son obligatorios");
        }
        
        try {

            sucursalesDAO.modificar(idSucursal, nombre, direccion, ciudad);

        } catch (SQLException e) {

            throw new RuntimeException("No se pudo modificar la sucursal: " + e.getMessage(), e);
        }
    }
    
    public void eliminarSucursal(int idSucursal) {

        if (idSucursal <= 0) {
            throw new IllegalArgumentException("El id de la sucursal no es válido");
        }

        try {

            sucursalesDAO.eliminar(idSucursal);

        } catch (SQLException e) {

            throw new RuntimeException(e.getMessage(),e);
        }
    }
    
    

    // metodos para prestamo


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


    // metodos para las operaciones


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
    
    
    // metodos de las cuentas bancarias
    
     public CuentaBancaria nuevaCuenta(
        String tipo,
        int idSucursal,
        double tasaInteres,
        double limiteDescubierto,
        String documentoCliente) {

    if (tipo == null
            || (!tipo.equals("Ahorro")
            && !tipo.equals("Corriente"))) {
        throw new IllegalArgumentException(
                "Debe seleccionar un tipo de cuenta válido."
        );
    }

    if (idSucursal <= 0) {
        throw new IllegalArgumentException(
                "Debe seleccionar una sucursal válida."
        );
    }

    if (documentoCliente == null || documentoCliente.isBlank()) {
        throw new IllegalArgumentException(
                "El documento del titular es obligatorio."
        );
    }

    if (!Double.isFinite(tasaInteres) || tasaInteres < 0
            || !Double.isFinite(limiteDescubierto)
            || limiteDescubierto < 0) {
        throw new IllegalArgumentException(
                "Los valores específicos de la cuenta no son válidos."
        );
    }

    try {
        Cliente titular = clientesDAO.buscarPorDocumento(documentoCliente);

        if (titular == null) {
            throw new IllegalArgumentException(
                    "El cliente que será titular no existe."
            );
        }

        return cuentasDAO.insertar(
                tipo,
                idSucursal,
                tasaInteres,
                limiteDescubierto,
                documentoCliente
        );

    } catch (SQLException e) {
        throw new RuntimeException(
                "No se pudo crear la cuenta bancaria.",
                e
        );
    }
}

    public ArrayList<CuentaBancaria> mostrarCuentas() {

        try {
            return cuentasDAO.listar();

        } catch (SQLException e) {
            throw new RuntimeException("No se pudieron obtener las cuentas bancarias.", e );
        }
    }




}