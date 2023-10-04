import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * La classe ClientHandler est responsable du traitement des demandes de chaque client sur un socket particulier.
 */
public class ClientHandler extends Thread {
    private final Socket socket;
    private final int clientNumber;
    public static Boolean isAuthenticated;
    public static String username;

    /**
     * Constructeur de la classe ClientHandler.
     *
     * @param socket       Le socket de communication avec le client.
     * @param clientNumber Le numéro d'identification du client.
     */
    public ClientHandler(Socket socket, int clientNumber) {
        this.socket = socket;
        this.clientNumber = clientNumber;
        System.out.println("Nouvelle connexion avec le client#" + clientNumber + " à " + socket);
    }

    /**
     * Méthode principale de traitement des demandes du client.
     */
    public void run() {
        isAuthenticated = false;

        // Connexion du client
        try {
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            Server.userData = Util.authenticateUser(Server.userData, inputStream, outputStream);
            if (!isAuthenticated) {
                System.out.println("Échec de l'authentification pour le client#" + clientNumber);
                socket.close();
                return;
            } else {
                System.out.println("Authentification réussie pour le client#" + clientNumber);
                outputStream.writeUTF("Bonjour depuis le serveur - vous êtes le client#" + clientNumber + "\n");
            }
        } catch (IOException e) {
            System.out.println("Erreur d'authentification du client# " + clientNumber + ": " + e);
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
                System.out.println(("[" + username + socket.getLocalAddress() + ":" + socket.getLocalPort() +
                        " - " + dateTime + "] : Image '" + imageName + "' reçue pour traitement."));
            } else {
                outputStream.writeUTF("L'image n'a pas pu être envoyée au serveur.");
            }

            BufferedImage filteredImage = Sobel.process(originalImage);
            byte[] filteredTable = Util.imageToByte(filteredImage);
            outputStream.writeUTF("Réception de l'image '" + imageName + "' filtrée depuis le serveur.");
            outputStream.writeInt(filteredTable.length);
            outputStream.write(filteredTable);
            System.out.println("Image '" + imageName + "' envoyée avec succès au client#" + clientNumber);
        } catch (IOException e) {
            System.out.println("Erreur avec l'image du client# " + clientNumber + ": " + e);
        }

        // Fermeture du socket
        finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Impossible de fermer le socket, que se passe-t-il ?");
            }
            System.out.println("Connexion avec le client#" + clientNumber + " fermée");
            try {
                Util.saveCSV(Server.userData, Server.pathCSV);
            } catch (IOException e) {
                System.out.println("Échec de l'enregistrement du CSV.");
            }
        }
    }
}
