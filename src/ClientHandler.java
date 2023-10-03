import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler extends Thread { // pour traiter la demande de chaque client sur un socket particulier
    private final Socket socket;
    private final int clientNumber;
    public ClientHandler(Socket socket, int clientNumber) {
        this.socket = socket;
        this.clientNumber = clientNumber;
        System.out.println("New connection with client#" + clientNumber + " at" + socket);
    }
    public void run() { // Création de thread qui envoi un message à un client
        new Util();
        try {
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            String username = inputStream.readUTF();
            String password = inputStream.readUTF();
            Server.userData = Util.authenticateUser(username, password, Server.userData, inputStream, outputStream);
        }
        catch (IOException e) {
            System.out.println("Error handling client# " + clientNumber + ": " + e);
        }
        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream()); // création de canal d’envoi
            out.writeUTF("Hello from server - you are client#" + clientNumber); // envoi de message
        }
        catch (IOException e) {
            System.out.println("Error handling client# " + clientNumber + ": " + e);
        }
        finally {
            try {
                socket.close();
            }
            catch (IOException e) {
                System.out.println("Couldn't close a socket, what's going on?");
            }
            System.out.println("Connection with client# " + clientNumber + " closed");
            try {
                Util.saveCSV(Server.userData, Server.pathCSV);
            } catch (IOException e) {
                System.out.println("Impossible d'enregistrer le CSV.");
            }
        }
    }
}
