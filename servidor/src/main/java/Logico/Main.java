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

                    Cliente cliente = new Cliente(
                            d[0],
                            d[2],
                            d[3],
                            LocalDate.parse(d[4]),
                            d[5],
                            d[6],
                            d[7],
                            d[8]
                    );

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

                    fachada.nuevoFuncionario(
                            d[0],
                            d[1],
                            d[2]
                    );

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


   
                default:
                    return "ERROR|Opción no válida.";
            }

        } catch (Exception e) {

            System.err.println(
                    "Error procesando solicitud: " + e.getMessage()
            );

            String mensajeError = e.getMessage();

            if (mensajeError == null) {
                mensajeError = "Error interno del servidor.";
            }

            return "ERROR|" + mensajeError.replace("|", "/");
        }
    }
}