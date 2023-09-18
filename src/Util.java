import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
}