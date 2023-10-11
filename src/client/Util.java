package client;

import java.io.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

/**
 * La classe server.Util contient des méthodes utilitaires pour diverses opérations.
 */
public final class Util {
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