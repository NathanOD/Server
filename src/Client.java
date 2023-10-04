import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.awt.image.BufferedImage;

// Application client
public class Client {
    public static void main(String[] args) throws Exception {
        // Récupération de l'IP:Port du serveur
        Scanner scannerClient = new Scanner(System.in);
        String serverAddress = Util.getValidIPClient(scannerClient);
        int port = Util.getValidPortClient(scannerClient, 5000, 5050);

        // Création d'une nouvelle connexion aves le serveur
        Socket socket = new Socket(serverAddress, port);
        System.out.format("Client lancé sur [%s:%d]\n", serverAddress, port);

        // Création d'un canal entrant pour recevoir les messages envoyés, par le serveur
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
                if (serverMessage.endsWith("image.")) {
                    System.out.print("\nEntrez le nom de l'image: ");
                    String nameImage = scannerAnswer.next();
                    BufferedImage image = Util.loadImage(nameImage);
                    byte[] imageByte = Util.imageToByte(image);
                    out.writeUTF(nameImage);
                    out.writeInt(imageByte.length);
                    out.write(imageByte);
                }
                if (serverMessage.endsWith("filtrée depuis le serveur.")) {
                    System.out.print('\n');
                    int filteredTableLength = in.readInt();
                    byte[] filteredTable = new byte[filteredTableLength];
                    in.readFully(filteredTable);
                    BufferedImage filteredImage = Util.byteToImage(filteredTable);
                    Util.saveImage(filteredImage);
                }
            } catch (IOException e) {
                System.out.println("La connexion est close.");
                break;
            }
        }
        // Fermeture de La connexion avec le serveur
        socket.close();
    }
}
