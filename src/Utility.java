import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Utility {
    // Generate a random 16-character account number
    public static String generateRandomAccountNumber() {
        StringBuilder accountNumber = new StringBuilder("SMB");

        // Append current timestamp (milliseconds)
        accountNumber.append(System.currentTimeMillis());

        // Append random digits to reach a total length of 16
        while (accountNumber.length() < 16) {
            accountNumber.append((int) (Math.random() * 10));
        }

        return accountNumber.toString();
    }
    // Function to generate a random integer between 1000 and 9999
    public static int generatePin(KycInformation kycInformation) throws ClassNotFoundException, SQLException {
        List<Double> existingPins = DatabaseOperations.getAllPins();
        int pin;

        do {
            pin = (int) (Math.random() * 9000) + 1000;
        } while (existingPins.contains((double) pin));

        return pin;
    }




    public static String generateTransactionId() {
        StringBuilder transactionId = new StringBuilder("SMB");

        // Append random digits to reach a total length of 8
        while (transactionId.length() < 8) {
            transactionId.append((int) (Math.random() * 10));
        }

        return transactionId.toString();
    }


    public static void clearConsole() {
        try {
            // ANSI escape code to clear console
            System.out.print("\033[H\033[2J");
            System.out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean validateUser(long phoneNumber, String password) throws ClassNotFoundException, SQLException {
        Connection connection = DatabaseOperations.getDatabaseConnection();
        String query = "SELECT * FROM customerinformation WHERE phone_number = ? AND password = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setLong(1, phoneNumber);
        statement.setString(2, password);
        return statement.executeQuery().next();
    }

    public static void wantToContinue() {
        String userInput;
        Scanner scanner = new Scanner(System.in);
        do {
            System.out.print("\nDo you want to continue?[y/n]- ");
            userInput = scanner.nextLine();
            if (userInput.equalsIgnoreCase("y")) {
                Utility.clearConsole();
                break;
            } else if (userInput.equalsIgnoreCase("n")) {
                Utility.clearConsole();
                System.out.println("\n\nThank you for banking with us. Have a good day!");
                System.out.println("SUMAN MEGA BANK\n\n");
                System.exit(0);
            } else {
                Utility.clearConsole();
                System.out.println("Invalid choice. Please enter 'y' or 'n'.");
            }

        } while (!(userInput.equalsIgnoreCase("y") || userInput.equalsIgnoreCase("n")));
    }
    public static void consoleOutputDelay(){
        try {
            // Sleep for 2000 milliseconds (2 seconds)
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}



