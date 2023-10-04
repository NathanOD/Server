import java.io.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

/**
 * La classe Util contient des méthodes utilitaires pour diverses opérations.
 */
public final class Util {
    /**
     * Lit un fichier CSV et renvoie les données sous forme de tableau.
     *
     * @param pathCSV Le chemin du fichier CSV.
     * @return Les données lues depuis le fichier CSV.
     * @throws IOException Si une erreur d'entrée/sortie se produit.
     */
    public static String[][] readCSV(Path pathCSV) throws IOException {
        BufferedReader readerCSV = Files.newBufferedReader(pathCSV);
        ArrayList<String[]> userData = new ArrayList<>();

        String ligne;
        while ((ligne = readerCSV.readLine()) != null) {
            String[] columns = ligne.split(";");
            if (columns.length == 2) {
                userData.add(columns);
            } else {
                System.out.println("La ligne du fichier CSV n'a pas le format attendu : " + ligne);
            }
        }
        readerCSV.close();

        if (!userData.isEmpty()) {
            String[][] tableauDonnees = new String[userData.size()][2];
            return userData.toArray(tableauDonnees);
        } else {
            return null;
        }
    }
    /**
     * Sauvegarde un tableau de données dans un fichier CSV.
     *
     * @param tableau Le tableau de données à sauvegarder.
     * @param pathCSV Le chemin du fichier CSV de destination.
     * @throws IOException Si une erreur d'entrée/sortie se produit.
     */
    public static void saveCSV(String[][] tableau, Path pathCSV) throws IOException {
        File fileCSV = pathCSV.toFile();
        if (!fileCSV.exists()) {
            boolean fileCreated = fileCSV.createNewFile();
            if (fileCreated) {
                System.out.println("Le fichier a été créé avec succès.");
            } else {
                System.out.println("La création du fichier a échoué.");
            }
        }

        FileWriter fileWriter = new FileWriter(fileCSV);
        BufferedWriter buffWriter = new BufferedWriter(fileWriter);

        for (String[] strings : tableau) {
            for (int j = 0; j < strings.length; j++) {
                buffWriter.write(strings[j]);
                if (j < strings.length - 1) {
                    buffWriter.write(";");
                }
            }
            // Aller à la ligne après chaque ligne du tableau
            buffWriter.newLine();
        }
        // Fermer le fichier
        buffWriter.close();
    }
    /**
     * Valide et récupère une adresse IP à partir de l'entrée de l'utilisateur.
     *
     * @param scanner Le scanner pour lire l'entrée de l'utilisateur.
     * @return Une adresse IP valide sous forme de chaîne de caractères.
     */
    public static String getValidIP(Scanner scanner) {
        String ipPattern = "^([0-9]{1,3}\\.){3}[0-9]{1,3}$";
        Pattern pattern = Pattern.compile(ipPattern);

        while (true) {
            System.out.print("Entrez l'adresse IP: ");

            if (scanner.hasNext()) {
                String userInputIP = scanner.next();
                Matcher matcher = pattern.matcher(userInputIP);

                if (matcher.matches()) {
                    String[] parts = userInputIP.split("\\.");
                    boolean valid = true;

                    for (String part : parts) {
                        int value = Integer.parseInt(part);
                        if (value < 0 || value > 255) {
                            valid = false;
                            break;
                        }
                    }

                    if (valid) {
                        return userInputIP;
                    } else {
                        System.out.println("IP invalide.");
                    }
                } else {
                    System.out.println("IP invalide.");
                }
            }
        }
    }
    /**
     * Valide et récupère un numéro de port à partir de l'entrée de l'utilisateur.
     *
     * @param scanner  Le scanner pour lire l'entrée de l'utilisateur.
     * @param minPort  Le port minimum autorisé.
     * @param maxPort  Le port maximum autorisé.
     * @return Un numéro de port valide.
     */
    public static int getValidPort(Scanner scanner, int minPort, int maxPort) {
        while (true) {
            System.out.print("Entrez le port: ");
            if (scanner.hasNextInt()) {
                int userInputPort = scanner.nextInt();
                if (userInputPort >= minPort && userInputPort <= maxPort) {
                    return userInputPort;
                } else {
                    System.out.println("Port invalide. Le port doit être compris entre " + minPort + " et " + maxPort + ".");
                }
            } else {
                System.out.println("Port invalide. Veuillez entrer un nombre entre " + minPort + " et " + maxPort + ".");
                scanner.next();
            }
        }
    }
    /**
     * Authentifie un utilisateur en vérifiant le nom d'utilisateur et le mot de passe dans les données utilisateur.
     *
     * @param userData     Les données utilisateur.
     * @param inputStream  Le flux d'entrée pour lire les données de l'utilisateur.
     * @param outputStream Le flux de sortie pour envoyer des messages à l'utilisateur.
     * @return Les données utilisateur mises à jour après l'authentification.
     */
    public static String[][] authenticateUser(
            String[][] userData,
            DataInputStream inputStream,
            DataOutputStream outputStream) {

        try {
            // Demander le nom d'utilisateur au client
            outputStream.writeUTF("Entrez le nom d'utilisateur: ");
            String username = inputStream.readUTF();
            boolean userExists = false;

            // Vérifier si le nom d'utilisateur existe dans la base de données
            for (String[] userDatum : userData) {
                if (userDatum[0].equals(username)) {
                    userExists = true;
                    ClientHandler.username = username;
                    break;
                }
            }

            if (userExists) {
                // Le nom d'utilisateur existe, demander le mot de passe
                outputStream.writeUTF("Entrez le mot de passe: ");
                String password = inputStream.readUTF();

                // Vérifier si le mot de passe est correct
                for (String[] userDatum : userData) {
                    if (userDatum[0].equals(username) && userDatum[1].equals(password)) {
                        ClientHandler.isAuthenticated = true;
                        ClientHandler.username = username;
                        outputStream.writeUTF("Authentification réussie." + "\n");
                        return userData;
                    }
                }

                // Le mot de passe est incorrect
                outputStream.writeUTF("Le mot de passe est incorrect." + "\n");
            } else {
                // Le nom d'utilisateur n'existe pas dans la base de données
                outputStream.writeUTF("Le nom d'utilisateur n'existe pas dans la base de données.");
                outputStream.writeUTF("Voulez-vous l'ajouter ? (Oui/Non): ");
                String response = inputStream.readUTF();

                if (response.equalsIgnoreCase("Oui")) {
                    // Demander à l'utilisateur d'entrer le mot de passe associé
                    outputStream.writeUTF("Entrez le mot de passe associé: ");
                    String newPassword = inputStream.readUTF();

                    // Ajouter le nouvel utilisateur à la base de données
                    String[][] newData = new String[userData.length + 1][2];
                    System.arraycopy(userData, 0, newData, 0, userData.length);
                    newData[userData.length] = new String[]{username, newPassword};
                    userData = newData;

                    outputStream.writeUTF("Utilisateur ajouté avec succès." + "\n");
                    ClientHandler.isAuthenticated = true;
                    ClientHandler.username = username;
                } else {
                    outputStream.writeUTF("L'opération a été annulée." + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return userData;
    }
    /**
     * Charge une image à partir d'un fichier.
     *
     * @param nameImage Le nom du fichier image.
     * @return L'image chargée en mémoire.
     */
    public static BufferedImage loadImage(String nameImage) {
        BufferedImage image = null;
        Path imagePath = Paths.get(nameImage);
        try {
            if (imagePath.toFile().exists()) {
                image = ImageIO.read(imagePath.toFile());
                System.out.println("L'image '" + nameImage + "' a été chargée avec succès.");
            } else {
                System.out.println("Le fichier spécifié n'existe pas: " + nameImage);
            }
        } catch (IOException e) {
            System.out.println("Une erreur s'est produite lors du chargement de l'image: " + e.getMessage());
        }
        return image;
    }
    /**
     * Enregistre une image dans un fichier.
     *
     * @param image L'image à enregistrer.
     */
    public static void saveImage(BufferedImage image) {
        System.out.print("Veuillez entrer le chemin où enregistrer l'image filtrée (ex: image.jpg): ");
        Scanner scanner = new Scanner(System.in);
        Path outputPath = Paths.get(scanner.nextLine());
        try {
            File outputFile = new File(outputPath.toUri());
            ImageIO.write(image, "JPEG", outputFile);
            System.out.println("L'image a été enregistrée à cet emplacement: " + outputPath);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Une erreur s'est produite lors de l'enregistrement de l'image à cet emplacement: " + outputPath);
        }
    }
    /**
     * Convertit une image en tableau de bytes.
     *
     * @param image L'image à convertir.
     * @return Le tableau de bytes représentant l'image.
     */
    public static byte[] imageToByte(BufferedImage image) {
        byte[] byteArray = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            javax.imageio.ImageIO.write(image, "jpg", byteArrayOutputStream);
            byteArray = byteArrayOutputStream.toByteArray();
            //System.out.println("Image convertie en tableau avec succès.");
        } catch (IOException e) {
            System.out.println("Erreur lors de la conversion de l'image");
        }
        return byteArray;
    }
    /**
     * Convertit un tableau de bytes en image.
     *
     * @param byteArray Le tableau de bytes à convertir en image.
     * @return L'image convertie à partir du tableau de bytes.
     */
    public static BufferedImage byteToImage(byte[] byteArray) {
        BufferedImage image = null;
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
            image = ImageIO.read(byteArrayInputStream);
            //System.out.println("Tableau de bytes converti en image avec succès.");
        } catch (IOException e) {
            System.out.println("Erreur lors de la conversion du tableau de bytes en image.");
        }
        return image;
    }
}