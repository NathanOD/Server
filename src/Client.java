import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.awt.image.BufferedImage;

/**
 * La classe Client représente une application cliente.
 */
public class Client {
    /**
     * Méthode principale de l'application cliente.
     *
     * @param args Les arguments de la ligne de commande.
     * @throws Exception Si une exception se produit pendant l'exécution.
     */
    public static void main(String[] args) throws Exception {
        // Récupération de l'IP:Port du serveur
        Scanner scannerClient = new Scanner(System.in);
        String serverAddress = Util.getValidIP(scannerClient);
        int port = Util.getValidPort(scannerClient, 5000, 5050);

        // Création d'une nouvelle connexion avec le serveur
        Socket socket = new Socket(serverAddress, port);
        System.out.format("Client lancé sur [%s:%d]\n", serverAddress, port);

        // Création d'un canal entrant pour recevoir les messages envoyés par le serveur
        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        Scanner scannerAnswer = new Scanner(System.in);

        // Connexion
        while (true) {
            try {
                String serverMessage = in.readUTF();
                System.out.print(serverMessage);
                // Vérifier si le message nécessite une réponse
                if (serverMessage.endsWith(": ")) {
                    String clientResponse = scannerAnswer.nextLine();
                    out.writeUTF(clientResponse);
                }
                // Envoi de l'image
                if (serverMessage.endsWith("traitement d'image.")) {
                    System.out.print("\nEntrez le nom de l'image: ");
                    String nameImage = scannerAnswer.next();
                    BufferedImage image = Util.loadImage(nameImage);
                    byte[] imageByte = Util.imageToByte(image);
                    out.writeUTF(nameImage);
                    out.writeInt(imageByte.length);
                    out.write(imageByte);
                }
                // Réception de l'image
                if (serverMessage.endsWith("filtrée depuis le serveur.")) {
                    System.out.print('\n');
                    int filteredTableLength = in.readInt();
                    byte[] filteredTable = new byte[filteredTableLength];
                    in.readFully(filteredTable);
                    BufferedImage filteredImage = Util.byteToImage(filteredTable);
                    Util.saveImage(filteredImage);
                }
            } catch (IOException e) {
                System.out.println("La connexion avec le serveur est close.");
                break;
            }
        }
        // Fermeture de la connexion avec le serveur
        socket.close();
    }
}
