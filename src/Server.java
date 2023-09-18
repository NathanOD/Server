import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Scanner;

public class Server {
    private static ServerSocket Listener;

    // Server application
    public static void main(String[] args) throws Exception {
        // Compteur incrémenté à chaque connexion d'un client au serveur
        int clientNumber = 0;

        new Util();

        Scanner scannerIP = new Scanner(System.in);
        String userInputIP = Util.getValidIP(scannerIP);
        //System.out.println(userInputIP);

        Scanner scannerPort = new Scanner(System.in);
        int userInputPort = Util.getValidPort(scannerPort, 5000, 5050);
        //System.out.println(userInputPort);


        // Adresse et port du serveur
        String serverAddress = userInputIP; // 127.0.0.1
        int serverPort = userInputPort; // 5000

        // Création de la connexien pour communiquer ave les, clients
        Listener = new ServerSocket();
        Listener.setReuseAddress(true);
        InetAddress serverIP = InetAddress.getByName(serverAddress);
        // Association de l'adresse et du port à la connexien
        Listener.bind(new InetSocketAddress(serverIP, serverPort));
        System.out.format("The server is running on %s:%d%n", serverAddress, serverPort);
        try {
            // À chaque fois qu'un nouveau client se, connecte, on exécute la fonstion
            // run() de l'objet ClientHandler
            while (true) {
                // Important : la fonction accept() est bloquante: attend qu'un prochain client se connecte
                // Une nouvetle connection : on incémente le compteur clientNumber
                new ClientHandler(Listener.accept(), clientNumber++).start();
            }
        }
        finally {
            // Closing connexion
            Listener.close();
        }
    }
}
