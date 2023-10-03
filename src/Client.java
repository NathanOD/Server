import java.io.*;
import java.net.Socket;
import java.util.Scanner;

// Application client

public class Client {
    private static Socket socket;
    public static void main(String[] args) throws Exception {
        new Util();

        Scanner scannerIP = new Scanner(System.in);
        String serverAddress = Util.getValidIP(scannerIP);
        Scanner scannerPort = new Scanner(System.in);
        int port = Util.getValidPort(scannerPort, 5000, 5050);

        // Création d'une nouvelle connexion aves le serveur
        socket = new Socket(serverAddress, port);
        System.out.format("Client lancé sur [%s:%d]\n", serverAddress, port);

        Scanner scannerUser = new Scanner(System.in);
        System.out.print("Nom d'utilisateur : ");
        String username = scannerUser.nextLine();
        System.out.print("Mot de passe : ");
        String password = scannerUser.nextLine();

        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        outputStream.writeUTF(username);
        outputStream.writeUTF(password);

        // Céatien d'un canal entrant pour recevoir les messages envoyés, par le serveur
        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        Scanner scannerAnswer = new Scanner(System.in);
        // Attente de la réception d'un message envoyé par le, server sur le canal
        try {
            while (true) {
                String serverMessage = in.readUTF();
                if (serverMessage.endsWith(" closed")) {
                    // Le serveur a fermé la connexion
                    break;
                }
                System.out.print(serverMessage);
                // Vérifier si le message nécessite une réponse
                if (serverMessage.endsWith(": ")) {
                    String clientResponse = scannerAnswer.nextLine();
                    out.writeUTF(clientResponse);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Fermeture de La connexion avec le serveur
        socket.close();
    }
}
