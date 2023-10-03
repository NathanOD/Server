import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.nio.file.Path;
import java.io.File;
import java.nio.file.Files;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public final class Util {
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
    public static String[][] authenticateUser(
            String username,
            String password,
            String[][] userData,
            DataInputStream inputStream,
            DataOutputStream outputStream) {

        boolean isAuthenticated = false;
        //int indexOfUser = -1;

        // Vérifier si le combo username/password est correct
        for (String[] userDatum : userData) {
            if (userDatum[0].equals(username) && userDatum[1].equals(password)) {
                isAuthenticated = true;
                //indexOfUser = i;
                break;
            }
        }

        if (!isAuthenticated) {
            // Le combo username/password n'est pas correct
            boolean userExists = false;

            // Vérifier si le nom d'utilisateur existe dans la base de données
            for (String[] userDatum : userData) {
                if (userDatum[0].equals(username)) {
                    userExists = true;
                    break;
                }
            }

            if (!userExists) {
                // Proposer d'ajouter le nouvel utilisateur à la base de données
                try {
                    outputStream.writeUTF("Le nom d'utilisateur n'existe pas dans la base de données.");
                    outputStream.writeUTF("Voulez-vous l'ajouter ? (Oui/Non): ");
                    String response = inputStream.readUTF();

                    if (response.equalsIgnoreCase("Oui")) {
                        // Demander à l'utilisateur d'entrer le mot de passe associé
                        outputStream.writeUTF("Entrez le mot de passe associé : ");
                        String newPassword = inputStream.readUTF();

                        // Ajouter le nouvel utilisateur à la base de données
                        String[][] newData = new String[userData.length + 1][2];
                        System.arraycopy(userData, 0, newData, 0, userData.length);
                        newData[userData.length] = new String[]{username, newPassword};
                        userData = newData;

                        outputStream.writeUTF("Utilisateur ajouté avec succès." + "\n");
                    } else {
                        outputStream.writeUTF("L'opération a été annulée." + "\n");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // Le nom d'utilisateur existe déjà dans la base de données, mais le mot de passe est incorrect
                try {
                    //PrintStream printStream = new PrintStream(outputStream);
                    outputStream.writeUTF("Le nom d'utilisateur existe déjà dans la base de données, mais le mot de passe est incorrect." + "\n");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            // Authentification réussie
            try {
                //PrintStream printStream = new PrintStream(outputStream);
                outputStream.writeUTF("Authentification réussie." + "\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return userData;
    }

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
}