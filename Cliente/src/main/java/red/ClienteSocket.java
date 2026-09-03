package red;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClienteSocket {

    private static final String HOST = "localhost";
    private static final int PUERTO = 12345;

    public Object enviarPeticion(Object peticion) {
        try (Socket socket = new Socket(HOST, PUERTO);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.writeObject(peticion);
            out.flush();
            return in.readObject();

        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR: No se pudo conectar con el servidor.";
        }
    }
}