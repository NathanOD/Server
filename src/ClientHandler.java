import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClientHandler extends Thread { // pour traiter la demande de chaque client sur un socket particulier
    private final Socket socket;
    private final int clientNumber;
    public static Boolean isAuthenticated = false;
    public static String username;
    public static String ipAddress;
    public static int port;
    public ClientHandler(Socket socket, int clientNumber) {
        this.socket = socket;
        this.clientNumber = clientNumber;
        System.out.println("New connection with client#" + clientNumber + " at" + socket);
    }
    public void run() { // Création de thread qui envoi un message à un client
        isAuthenticated = false;
        // Connexion
        try {
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            Server.userData = Util.authenticateUser(Server.userData, inputStream, outputStream);
            if (!isAuthenticated) {
                System.out.println("Authentication failed for client#" + clientNumber);
                socket.close();
                return;
            } else {
                System.out.println("Authentication succeed for client#" + clientNumber);
            }
        } catch (IOException e) {
            System.out.println("Error handling client# " + clientNumber + ": " + e);
        }
        try {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream()); // création de canal d’envoi
            outputStream.writeUTF("Hello from server - you are client#" + clientNumber + "\n"); // envoi de message
        } catch (IOException e) {
            System.out.println("Error handling client# " + clientNumber + ": " + e);
        }

        // Traitement de l'image
        try {
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeUTF("Bienvenue au service de traitement d'image.");
            String imageName = inputStream.readUTF();
            int imageLength = inputStream.readInt();
            byte[] imageData = new byte[imageLength];
            inputStream.readFully(imageData);
            BufferedImage originalImage = Util.byteToImage(imageData);

            LocalDateTime currentTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd@HH:mm:ss");
            String dateTime = currentTime.format(formatter);
            if (imageData.length != 0) {
                outputStream.writeUTF("Image '" + imageName + "' envoyée au serveur avec succès.\n");
                System.out.println(("[" + username + " - " + ipAddress + ":" + port +" - " + dateTime +
                        "] : Image '" + imageName + "' reçue pour traitement."));
            } else {
                outputStream.writeUTF("L'image n'a pas pu être chargée.");
            }

            BufferedImage filteredImage = Sobel.process(originalImage);
            byte[] filteredTable = Util.imageToByte(filteredImage);
            outputStream.writeUTF("Reception de l'image '" + imageName + "' filtrée depuis le serveur.");
            outputStream.writeInt(filteredTable.length);
            outputStream.write(filteredTable);
            System.out.println("Image '" + imageName + "' envoyée au client#" + clientNumber);
            outputStream.writeUTF("Image reçue !\n");
        } catch (IOException e) {
            System.out.println("Error with image of client# " + clientNumber + ": " + e);
        }
        // Fermeture du socket
        finally{
            try {
                socket.close();
            } catch (IOException e) {
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