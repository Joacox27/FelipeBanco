package red;

import Logico.Cliente;
import Logico.Funcionario;
import Logico.Prestamo;
import Logico.Sucursal;
import Logico.Fachada;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.util.ArrayList;

public class ServidorSocket {
    
    private static final int PUERTO = 2050;
    
    private final Fachada fachada;
    
    public ServidorSocket() {
        fachada = Fachada.getInstancia();
    }

    public void iniciar() {
        try (ServerSocket servidor = new ServerSocket(PUERTO)) {
            
            System.out.println("FELIPEBAKSERVER iniciado.");
            System.out.println("Escuchando en el puerto " + PUERTO + "...");
            
            while (true) {
                Socket cliente = servidor.accept();

                System.out.println(
                        "Cliente conectado: "
                        + cliente.getInetAddress().getHostAddress()
                );

                Thread hilo = new Thread(() -> atenderCliente(cliente));
                hilo.start();
            }
            
        } catch (IOException e) {
            System.err.println("Error al iniciar el felipeserver: " + e.getMessage());
        }
    }

    
    
    
    
    private void atenderCliente(Socket socket) {

        try (
                Socket cliente = socket;
                BufferedReader entrada = new BufferedReader(
                        new InputStreamReader(cliente.getInputStream())
                );
                PrintWriter salida = new PrintWriter(
                        cliente.getOutputStream(), true
                )
        ) {

            String mensaje;

            while ((mensaje = entrada.readLine()) != null) {
                
                System.out.println("Mensaje recibido: " + mensaje);

                if (mensaje.equals("0")) {
                    System.out.println("Cliente desconectado.");
                    break;
                }

                String respuesta = procesarMensaje(mensaje);

                salida.println(respuesta);

                System.out.println("Respuesta enviada: " + respuesta);
            }

        } catch (IOException e) {
            System.err.println(
                    "Error atendiendo al cliente: " + e.getMessage()
            );
        }
    }

    
    
    
    
    private String procesarMensaje(String mensaje) {

        try {

            /*
             * Formato q se recibe:
             *
             * opcion;datos
             *
             */

            String[] partes = mensaje.split(";", 2);

            int opcion = Integer.parseInt(partes[0]);

            String datos = "";

            if (partes.length > 1) {
                datos = partes[1];
            }

            switch (opcion) {

                
                // 1 - Probar conexión / Driver
                case 1:
                    return fachada.probarDriver();



                // 2 - Nuevo cliente
                case 2: {

                    String[] datosCliente = datos.split(":", -1);

                    if (datosCliente.length != 9) {
                        return "ERROR|Datos de cliente incorrectos.";
                    }

                    String documento = datosCliente[0];
                    
                    int password = Integer.parseInt(datosCliente[1]);
                    String nombre = datosCliente[2];
                    String apellido = datosCliente[3];
                    
                    LocalDate fechaNacimiento = LocalDate.parse(datosCliente[4]);
                    
                    String calle = datosCliente[5];
                    String numero = datosCliente[6];
                    String ciudad = datosCliente[7];
                    String telefono = datosCliente[8];

                    Cliente cliente = new Cliente(
                             documento,
                             nombre,
                            apellido,
                            fechaNacimiento,
                            calle,
                            numero,
                            ciudad,
                            telefono
                    );

                    
                    fachada.nuevoCliente(cliente, password);

                    return "OK|Cliente creado correctamente.";
                }


                // 3 - Mostrar clientes
                case 3: {

                    ArrayList<Cliente> clientes =
                            fachada.mostrarClientes();

                    StringBuilder respuesta = new StringBuilder();

                    for (Cliente c : clientes) {

                        respuesta.append( c.getDocumento() ).append(",");

                        respuesta.append( c.getNombre() ).append(",");

                        respuesta.append( c.getApellido() ).append(",");

                        respuesta.append( c.getFechaNacimiento() ).append(",");

                        respuesta.append( c.getCalle() ).append(",");

                        respuesta.append( c.getNumero() ).append(",");

                        respuesta.append( c.getCiudad() ).append(",");

                        respuesta.append( c.getTelefono() ).append("|");
                    }

                    return respuesta.toString();
                }


                // 4 - Nuevo funcionario
                case 4: {

                    String[] datosFuncionario = datos.split(":", -1);

                    if (datosFuncionario.length != 3) {
                        return "ERROR|Datos de funcionario incorrectos.";
                    }

                    String nombre = datosFuncionario[0];
                    String nombreSucursal = datosFuncionario[1];
                    String rol = datosFuncionario[2];

                    fachada.nuevoFuncionario(
                            nombre,
                            nombreSucursal,
                            rol
                    );

                    return "OK|Funcionario creado correctamente.";
                }


                // 5 - Mostrar funcionarios
                case 5: {

                    ArrayList<Funcionario> funcionarios =
                            fachada.mostrarFuncionarios();

                    StringBuilder respuesta = new StringBuilder();

                    for (Funcionario f : funcionarios) {

                        respuesta.append(
                                f.getIdFuncionario()
                        ).append(",");

                        respuesta.append(
                                f.getNombre()
                        ).append(",");

                        respuesta.append(
                                f.getRol()
                        ).append(",");

                        respuesta.append(
                                f.getFechaIngreso()
                        ).append(",");

                        if (f.getSucursal() != null) {

                            Sucursal s = f.getSucursal();

                            respuesta.append(
                                    s.getIdSucursal()
                            ).append(",");

                            respuesta.append(
                                    s.getNombre()
                            ).append(",");

                            respuesta.append(
                                    s.getDireccion()
                            ).append(",");

                            respuesta.append(
                                    s.getCiudad()
                            );

                        } else {

                            respuesta.append(
                                    "0,,,"
                            );
                        }

                        respuesta.append("|");
                    }

                    return respuesta.toString();
                }



                // 6 - Mostrar sucursales
                case 6: {

                    ArrayList<Sucursal> sucursales =
                            fachada.mostrarSucursales();

                    StringBuilder respuesta = new StringBuilder();

                    for (Sucursal s : sucursales) {

                        respuesta.append(
                                s.getIdSucursal()
                        ).append(",");

                        respuesta.append(
                                s.getNombre()
                        ).append(",");

                        respuesta.append(
                                s.getDireccion()
                        ).append(",");

                        respuesta.append(
                                s.getCiudad()
                        ).append("|");
                    }

                    return respuesta.toString();
                }


                // 7 - Login
                case 7: {

                    String[] datosLogin = datos.split(":", -1);

                    if (datosLogin.length != 2) {
                        return "ERROR|Datos de login incorrectos.";
                    }

                    String documento = datosLogin[0];
                    int password = Integer.parseInt(datosLogin[1]);

                    Cliente cliente =
                            fachada.login(documento, password);

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



                // 8 - Solicitar préstamo
                case 8: {

                    String[] datosPrestamo = datos.split(":", -1);

                    if (datosPrestamo.length != 3) {
                        return "ERROR|Datos de préstamo incorrectos.";
                    }

                    double monto =
                            Double.parseDouble(datosPrestamo[0]);

                    int cuotas =
                            Integer.parseInt(datosPrestamo[1]);

                    String documentoCliente =
                            datosPrestamo[2];

                    fachada.solicitarPrestamo(
                            monto,
                            cuotas,
                            documentoCliente
                    );

                    return "OK|Préstamo solicitado correctamente.";
                }



                // 9 - Mostrar préstamos de un cliente
                case 9: {

                    String documentoCliente = datos;

                    ArrayList<Cliente> clientes =
                            fachada.mostrarClientes();

                    Cliente clienteEncontrado = null;

                    for (Cliente c : clientes) {

                        if (c.getDocumento().equals(documentoCliente)) {
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

                        respuesta.append( p.getIdPrestamo() ).append(",");

                        respuesta.append( p.getMonto() ).append(",");

                        respuesta.append( p.getTasaInteres() ).append(",");
                        respuesta.append( p.getFechaOtorgamiento() ).append(",");
                        respuesta.append( p.getCuotas() ).append("|");
                    }

                    return respuesta.toString();
                }


                // Opción inexistente
                default:
                    return "ERROR|Opción no válida.";
            }

        } catch (NumberFormatException e) {

            return "ERROR|Uno de los valores numéricos es inválido.";

        } catch (Exception e) {

            System.err.println(
                    "Error procesando solicitud: " + e.getMessage()
            );

            return "ERROR|" + e.getMessage();
        }
    }
}
