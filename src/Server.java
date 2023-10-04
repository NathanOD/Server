import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * La classe Server représente une application serveur.
 */
public class Server {
    /**
     * Tableau de données utilisateur.
     */
    public static String[][] userData;

    /**
     * Chemin vers le fichier CSV de la base de données utilisateur.
     */
    public static Path pathCSV;

    /**
     * Méthode principale de l'application serveur.
     *
     * @param args Les arguments de la ligne de commande.
     * @throws Exception Si une exception se produit pendant l'exécution.
     */
    public static void main(String[] args) throws Exception {
        // Compteur incrémenté à chaque connexion d'un client au serveur
        int clientNumber = 0;

        // Chargement de la base de données utilisateurs
        pathCSV = Paths.get("user_database.csv");
        userData = Util.readCSV(pathCSV);

        // Choix de l'adresse et du port du serveur
        Scanner scannerServer = new Scanner(System.in);
        String serverAddress = Util.getValidIP(scannerServer); // Exemple : 127.0.0.1
        int serverPort = Util.getValidPort(scannerServer, 5000, 5050); // Exemple : 5000

        // Création de la connexion pour communiquer avec les clients
        ServerSocket listener = new ServerSocket();
        listener.setReuseAddress(true);
        InetAddress serverIP = InetAddress.getByName(serverAddress);

        // Association de l'adresse et du port à la connexion
        listener.bind(new InetSocketAddress(serverIP, serverPort));
        System.out.format("Le serveur est en cours d'exécution sur %s:%d%n", serverAddress, serverPort);

        try {
            // À chaque fois qu'un nouveau client se connecte, on exécute la fonction run() de l'objet ClientHandler
            while (true) {
                // Important : la fonction accept() est bloquante : elle attend qu'un prochain client se connecte
                // Une nouvelle connexion : on incrémente le compteur clientNumber
                new ClientHandler(listener.accept(), clientNumber++).start();

                // Attente d'une entrée utilisateur pour quitter la boucle
                System.out.println("Tapez 'exit' pour arrêter le serveur : ");
                String userInput = scannerServer.nextLine();
                if ("exit".equalsIgnoreCase(userInput)) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Erreur dans le lancement du ClientHandler");
        } finally {
            // Fermeture de la connexion
            listener.close();
        }
    }
}