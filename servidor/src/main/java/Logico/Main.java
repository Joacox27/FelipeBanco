package Logico;

import red.ServidorSocket;

import java.time.LocalDate;
import java.util.ArrayList;

public class Main {

    
    public static void main(String[] args) {
        ServidorSocket servidor = new ServidorSocket();
        servidor.iniciar();
        
        
        
    }

    public static String procesarMensaje(String mensaje) {

        Fachada fachada = Fachada.getInstancia();

        try {

            String[] partes = mensaje.split(";", 2);

            int opcion = Integer.parseInt(partes[0]);

            String datos = partes.length > 1 ? partes[1] : "";

            switch (opcion) {

           
                case 1:
                    return fachada.probarDriver();



                case 2: {

                    
                    String[] d = datos.split(":", -1);

                    if (d.length != 9) {
                        
                        return "ERROR|Datos de cliente incorrectos.";
                        
                    }

                    Cliente cliente = new Cliente( d[0], d[2], d[3], LocalDate.parse(d[4]), d[5], d[6], d[7], d[8] );

                    int password = Integer.parseInt(d[1]);

                    fachada.nuevoCliente(cliente, password);

                    return "OK|Cliente registrado correctamente.";
                }


                case 3: {

                    ArrayList<Cliente> clientes = fachada.mostrarClientes();

                    StringBuilder respuesta = new StringBuilder();

                    for (Cliente c : clientes) {
                        respuesta.append(c.getDocumento()).append(",");
                        respuesta.append(c.getNombre()).append(",");
                        respuesta.append(c.getApellido()).append(",");
                        respuesta.append(c.getFechaNacimiento()).append(",");
                        respuesta.append(c.getCalle()).append(",");
                        respuesta.append(c.getNumero()).append(",");
                        respuesta.append(c.getCiudad()).append(",");
                        respuesta.append(c.getTelefono()).append("|");
                    }

                    return respuesta.toString();
                }


                case 4: {

                    String[] d = datos.split(":", -1);

                    if (d.length != 3) { 
                        return "ERROR|Datos de funcionario incorrectos.";
                    }

                    fachada.nuevoFuncionario( d[0], d[1], d[2] );

                    return "OK|Funcionario registrado correctamente.";
                }


                case 5: {

                    ArrayList<Funcionario> funcionarios =
                            fachada.mostrarFuncionarios();

                    StringBuilder respuesta = new StringBuilder();

                    for (Funcionario f : funcionarios) {

                        respuesta.append(f.getIdFuncionario()).append(",");
                        respuesta.append(f.getNombre()).append(",");
                        respuesta.append(f.getRol()).append(",");
                        respuesta.append(f.getFechaIngreso()).append(",");

                        Sucursal s = f.getSucursal();

                        if (s != null) {
                            
                            respuesta.append(s.getIdSucursal()).append(",");
                            respuesta.append(s.getNombre()).append(",");
                            respuesta.append(s.getDireccion()).append(",");
                            respuesta.append(s.getCiudad());
                            
                        } else {
                            
                            respuesta.append("0,,,");
                        }

                        respuesta.append("|");
                    }

                    return respuesta.toString();
                }


            
                case 6: {

                    ArrayList<Sucursal> sucursales =
                            fachada.mostrarSucursales();

                    StringBuilder respuesta = new StringBuilder();

                    for (Sucursal s : sucursales) {

                        respuesta.append(s.getIdSucursal()).append(",");
                        respuesta.append(s.getNombre()).append(",");
                        respuesta.append(s.getDireccion()).append(",");
                        respuesta.append(s.getCiudad()).append("|");
                    }

                    return respuesta.toString();
                }


                case 7: {

                    String[] d = datos.split(":", -1);

                    if (d.length != 2) {
                        return "ERROR|Datos de login incorrectos.";
                    }

                    String documento = d[0];
                    int password = Integer.parseInt(d[1]);

                    Cliente cliente = fachada.login(documento, password);

                    if (cliente == null) {
                        return "ERROR|Documento o contraseña incorrectos.";
                    }

                    return "OK|"
                            + cliente.getDocumento() + "|"
                            + cliente.getNombre() + "|"
                            + cliente.getApellido() + "|"
                            + cliente.getFechaNacimiento() + "|"
                            + cliente.getCalle() + "|"
                            + cliente.getNumero() + "|"
                            + cliente.getCiudad() + "|"
                            + cliente.getTelefono();
                }



                case 8: {

                    String[] d = datos.split(":", -1);

                    if (d.length != 3) {
                        return "ERROR|Datos de préstamo incorrectos.";
                    }

                    double monto = Double.parseDouble(d[0]);
                    int cuotas = Integer.parseInt(d[1]);
                    String documento = d[2];

                    fachada.solicitarPrestamo(
                            monto,
                            cuotas,
                            documento
                    );

                    return "OK|Préstamo solicitado correctamente.";
                }

                case 9: {

                    String documento = datos;

                    Cliente clienteEncontrado = null;

                    for (Cliente c : fachada.mostrarClientes()) {
                        if (c.getDocumento().equals(documento)) {
                            clienteEncontrado = c;
                            break;
                        }
                    }

                    if (clienteEncontrado == null) {
                        return "ERROR|Cliente no encontrado.";
                    }

                    ArrayList<Prestamo> prestamos =
                            fachada.mostrarPrestamos(clienteEncontrado);

                    StringBuilder respuesta = new StringBuilder();

                    for (Prestamo p : prestamos) {

                        respuesta.append(p.getIdPrestamo()).append(",");
                        respuesta.append(p.getMonto()).append(",");
                        respuesta.append(p.getTasaInteres()).append(",");
                        respuesta.append(p.getFechaOtorgamiento()).append(",");
                        respuesta.append(p.getCuotas()).append("|");
                    }

                    return respuesta.toString();
                }


            
                
                case 10: {

                    String[] d = datos.split(":", -1);

                    if (d.length != 3) {
                        return "ERROR|Datos de depósito incorrectos.";
                    }

                    String numeroCuenta = d[0];
                    double monto = Double.parseDouble(d[1]);
                    String documento = d[2];

                    fachada.depositar(
                            numeroCuenta,
                            monto,
                            documento
                    );

                    return "OK|Depósito realizado correctamente.";
                }



                
                case 11: {

                    String[] d = datos.split(":", -1);

                    if (d.length != 3) {
                        return "ERROR|Datos de retiro incorrectos.";
                    }

                    String numeroCuenta = d[0];
                    double monto = Double.parseDouble(d[1]);
                    String documento = d[2];

                    fachada.retirar(
                            numeroCuenta,
                            monto,
                            documento
                    );

                    return "OK|Retiro realizado correctamente.";
                }



                
                case 12: {

                    String[] d = datos.split(":", -1);

                    if (d.length != 4) {
                        return "ERROR|Datos de transferencia incorrectos.";
                    }

                    String cuentaOrigen = d[0];
                    String cuentaDestino = d[1];
                    double monto = Double.parseDouble(d[2]);
                    String documento = d[3];

                    fachada.transferir(
                            cuentaOrigen,
                            cuentaDestino,
                            monto,
                            documento
                    );

                    return "OK|Transferencia realizada correctamente.";
                }

                                // CREAR CUENTA BANCARIA
                // Protocolo: 13;tipo:idSucursal:tasaInteres:limiteDescubierto

                case 13: {

                    String[] d = datos.split(":", -1);

                    if (d.length != 5) {
                        return "ERROR|Datos de cuenta incorrectos.";
                    }

                    String tipo = d[0];
                    int idSucursal = Integer.parseInt(d[1]);
                    double tasaInteres = Double.parseDouble(d[2]);
                    double limiteDescubierto = Double.parseDouble(d[3]);
                    String documentoCliente = d[4];

                    CuentaBancaria cuenta = fachada.nuevaCuenta(
                            tipo,
                            idSucursal,
                            tasaInteres,
                            limiteDescubierto,
                            documentoCliente
                    );

                    String tipoCuenta = cuenta instanceof CuentaAhorro
                            ? "Ahorro"
                            : "Corriente";

                    return "OK|"
                            + cuenta.numeroCuenta + "|"
                            + tipoCuenta + "|"
                            + cuenta.fechaApertura + "|"
                            + cuenta.saldo + "|"
                            + cuenta.sucursal.getIdSucursal();
                }
                
                

                // CONSULTAR TODAS LAS CUENTAS BANCARIAS
                // Protocolo: 14
                // Respuesta:
                // numeroCuenta,tipo,fechaApertura,saldo,
                // idSucursal,nombreSucursal,direccion,ciudad,
                // tasaInteres,limiteDescubierto|

                case 14: {

                    ArrayList<CuentaBancaria> cuentas =
                            fachada.mostrarCuentas();

                    StringBuilder respuesta = new StringBuilder(); 
                            //string builder nos permite construir un texto progresivamente,
                            // agregándole partes sin tener que crear una cadena nueva en cada concatenación.
                            //.append le va agregando algo alfinal del texto

                    for (CuentaBancaria cuenta : cuentas) {

                        String tipo;
                        double tasaInteres = 0;
                        double limiteDescubierto = 0;

                        if (cuenta instanceof CuentaAhorro ahorro) {

                            tipo = "Ahorro";
                            tasaInteres = ahorro.getTasaInteres();

                        } else if (cuenta instanceof CuentaCorriente corriente) {

                            tipo = "Corriente";
                            limiteDescubierto =
                                    corriente.getLimiteDescubierto();

                        } else {
                            continue;
                        }

                        Sucursal sucursal = cuenta.sucursal;

                        respuesta.append(cuenta.numeroCuenta).append(",");
                        respuesta.append(tipo).append(",");
                        respuesta.append(cuenta.fechaApertura).append(",");
                        respuesta.append(cuenta.saldo).append(",");

                        respuesta.append(sucursal.getIdSucursal()).append(",");
                        respuesta.append(sucursal.getNombre()).append(",");
                        respuesta.append(sucursal.getDireccion()).append(",");
                        respuesta.append(sucursal.getCiudad()).append(",");

                        respuesta.append(tasaInteres).append(",");
                        respuesta.append(limiteDescubierto).append("|");
                    }

                    return respuesta.toString();
                }

                
                

                // alta de una sucursal
                case 15: {
                    
                    String[] d = datos.split(":", -1);
                    
                    if (d.length != 3) {
                        return "ERROR|Datos de sucursal incorrectos.";
                    }
                    
                    String nombre = d[0];
                    String direccion = d[1];
                    String ciudad = d[2];
                    
                    fachada.nuevaSucursal(nombre, direccion,ciudad);
                    
                    return "OK|Sucursal registrada correctamente.";
                }


                // MODIFICAR SUCURSAL
                case 16: {
                    
                    String[] d = datos.split(":", -1);
                    
                    if (d.length != 4) {
                        return "ERROR|Datos de modificación incorrectos.";
                    }
                    
                    int idSucursal = Integer.parseInt(d[0]);
                    String nombre = d[1];
                    String direccion = d[2];
                    String ciudad = d[3];

                    fachada.modificarSucursal(idSucursal, nombre, direccion, ciudad);

                    return "OK|Sucursal modificada correctamente.";
                }


                // eliminar una sucursal (por el id)
                case 17: {
                    
                    int idSucursal = Integer.parseInt(datos);
                    
                    fachada.eliminarSucursal(idSucursal);
                    
                    return "OK|Sucursal " + idSucursal + "eliminada correctamente.";
                }


                // MODIFICAR CLIENTE
                case 18: {
                    
                    String[] d = datos.split(":", -1);
                    
                    if (d.length != 8) {
                        return "ERROR|Datos de modificación de cliente incorrectos.";
                    }
                    
                    Cliente cliente = new Cliente(
                            d[0],
                            d[1],
                            d[2],
                            LocalDate.parse(d[3]),
                            d[4],
                            d[5],
                            d[6],
                            d[7]
                    );

                    fachada.modificarCliente(cliente);

                    return "OK|Cliente modificado correctamente.";
                }


                // ELIMINAR CLIENTE (POR CI)
                case 19: {

                    String documento = datos.trim();

                    if (documento.isEmpty()) {
                        return "ERROR|El documento del cliente es obligatorio.";
                    }

                    fachada.eliminarCliente(documento);

                    return "OK|Cliente eliminado correctamente.";
                }


                // MODIFICAR FUNCIONARIO
                case 20: {
                    // Aquí irá la modificación de los datos de un funcionario.
                    return "ERROR|Funcionalidad pendiente: modificar funcionario.";
                }


                // ELIMINAR FUNCIONARIO
                case 21: {
                    // Aquí irá la eliminación de un funcionario.
                    return "ERROR|Funcionalidad pendiente: eliminar funcionario.";
                }


                // ASOCIAR TITULAR A UNA CUENTA
                case 22: {
                    // Aquí irá la asociación de un cliente como titular de una cuenta.
                    return "ERROR|Funcionalidad pendiente: asociar titular.";
                }


                // DESASOCIAR TITULAR DE UNA CUENTA
                case 23: {
                    // Aquí irá la eliminación de la relación entre un cliente y una cuenta.
                    return "ERROR|Funcionalidad pendiente: desasociar titular.";
                }


                // CONSULTAR CUENTAS DE UN CLIENTE
                case 24: {
                    // Aquí irá la consulta de las cuentas asociadas a un documento de cliente.
                    return "ERROR|Funcionalidad pendiente: cuentas del cliente.";
                }


                // BUSCAR CUENTA POR NÚMERO
                case 25: {
                    // Aquí irá la búsqueda de una cuenta bancaria por su número.
                    return "ERROR|Funcionalidad pendiente: buscar cuenta.";
                }


                // MODIFICAR CUENTA BANCARIA
                case 26: {
                    // Aquí irá la modificación de los datos permitidos de una cuenta.
                    return "ERROR|Funcionalidad pendiente: modificar cuenta.";
                }


                // ELIMINAR CUENTA BANCARIA
                case 27: {
                    // Aquí irá la eliminación de una cuenta bancaria.
                    return "ERROR|Funcionalidad pendiente: eliminar cuenta.";
                }


                // CONSULTAR OPERACIONES DE UNA CUENTA
                case 28: {
                    // Aquí irá el historial de depósitos, retiros y transferencias de una cuenta.
                    return "ERROR|Funcionalidad pendiente: historial de operaciones.";
                }


                // BUSCAR OPERACIÓN POR IDENTIFICADOR
                case 29: {
                    // Aquí irá la búsqueda de una operación por su identificador.
                    return "ERROR|Funcionalidad pendiente: buscar operación.";
                }


                // BUSCAR CLIENTE POR DOCUMENTO
                case 30: {
                    // Aquí irá la búsqueda de un cliente por su documento.
                    return "ERROR|Funcionalidad pendiente: buscar cliente.";
                }


                // BUSCAR FUNCIONARIO POR IDENTIFICADOR
                case 31: {
                    // Aquí irá la búsqueda de un funcionario por su identificador.
                    return "ERROR|Funcionalidad pendiente: buscar funcionario.";
                }


                // BUSCAR SUCURSAL POR IDENTIFICADOR
                case 32: {
                    // Aquí irá la búsqueda de una sucursal por su identificador.
                    return "ERROR|Funcionalidad pendiente: buscar sucursal.";
                }
                
                

                default:
                    return "ERROR|Opción no válida.";
            }

        } catch (Exception e) {

            System.err.println(
                    "Error procesando la solicitud: " + e.getMessage()
            );

            String mensajeError = e.getMessage();

            if (mensajeError == null) {
                mensajeError = "Error interno del servidor.";
            }

            return "ERROR|" + mensajeError.replace("|", "/");
        }
    }
}