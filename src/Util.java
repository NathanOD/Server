import java.io.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.nio.file.Path;
import java.nio.file.Files;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Paths;

public final class Util {
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
    public static String getValidIPServer(Scanner scanner) {
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
                        ClientHandler.ipAddress = userInputIP;
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
    public static String getValidIPClient(Scanner scanner) {
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
    public static int getValidPortServer(Scanner scanner, int minPort, int maxPort) {
        while (true) {
            System.out.print("Entrez le port: ");
            if (scanner.hasNextInt()) {
                int userInputPort = scanner.nextInt();
                if (userInputPort >= minPort && userInputPort <= maxPort) {
                    ClientHandler.port = userInputPort;
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
    public static int getValidPortClient(Scanner scanner, int minPort, int maxPort) {
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
    public static void saveImage(BufferedImage image) {
        System.out.print("Veuillez entrer le chemin du fichier de sortie (ex: image.jpg) : ");
        Scanner scanner = new Scanner(System.in);
        Path outputPath = Paths.get(scanner.nextLine());
        try {
            File outputFile = new File(outputPath.toUri());
            ImageIO.write(image, "JPEG", outputFile);
            System.out.println("L'image a été enregistrée à cet emplacement : " + outputPath);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Une erreur s'est produite lors de l'enregistrement de l'image à cet emplacement : " + outputPath);
        }
    }
    public static byte[] imageToByte(BufferedImage image) {
        byte[] byteArray = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            javax.imageio.ImageIO.write(image, "jpg", byteArrayOutputStream);
            byteArray = byteArrayOutputStream.toByteArray();
            System.out.println("Image convertie en tableau avec succès.");
        } catch (IOException e) {
            System.out.println("Erreur lors de la conversion de l'image");
        }
        return byteArray;
    }
    public static BufferedImage byteToImage(byte[] byteArray) {
        BufferedImage image = null;
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
            image = ImageIO.read(byteArrayInputStream);
            System.out.println("Tableau de bytes converti en image avec succès.");
        } catch (IOException e) {
            System.out.println("Erreur lors de la conversion du tableau de bytes en image.");
        }
        return image;
    }
}