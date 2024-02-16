
import java.sql.*;
import java.util.List;
import java.util.Scanner;


public class BankManagementSystem {
    public static void createAccount(KycInformation kycInformation) throws SQLException, ClassNotFoundException {

        Scanner scanner = new Scanner(System.in);

        boolean flag = true;
        int maxAttempts = 3;  // Set a maximum number of attempts
        System.out.print("Enter your Address: ");
        kycInformation.setAddress(scanner.nextLine());

        do {
            System.out.print("Enter 10-digit Phone Number: ");
            String input = scanner.nextLine().trim();

            try {
                long phoneNo = Long.parseLong(input);

                List<String> phoneNumbers = DatabaseOperations.getPhoneNumbersFromDatabase();
                if (String.valueOf(phoneNo).length() == 10 && !phoneNumbers.contains(String.valueOf(phoneNo))) {
                    kycInformation.setPhoneNumber(phoneNo);
                    flag = false;
                } else {
                    System.out.println("Invalid or duplicate phone number.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a numeric value.");
            }

            maxAttempts--;
            if (maxAttempts == 0) {
                System.out.println("Maximum attempts reached. Exiting.");
                System.exit(0);
            }
        } while (flag);

        System.out.print("Enter your account password: ");
        kycInformation.setPassword(scanner.nextLine());
        // Ask for a 4-digit PIN

        do {
            System.out.print("Enter initial deposit amount: ");
            kycInformation.setCurrentBalance(scanner.nextDouble());
            scanner.nextLine();
        } while (kycInformation.getCurrentBalance() < 0);
        kycInformation.setAccountNumber(Utility.generateRandomAccountNumber());
        kycInformation.setPin(Utility.generatePin(kycInformation));
        Utility.clearConsole();
        System.out.println("\nACCOUNT CREATION PORTAL\n");
        System.out.println("Account created successfully!\n");
        Utility.consoleOutputDelay();
        Utility.clearConsole();
        System.out.println("\nACCOUNT CREATION PORTAL\n");
        BankManagementSystem.showCustomerDetails(kycInformation);
    }


    public static void performDeposit(KycInformation kycInformation) throws SQLException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        boolean exitMenu = false;
        while (!exitMenu) {
            System.out.println("╔══════════════════════════════════════╗");
            System.out.println("║             Options                  ║");
            System.out.println("║──────────────────────────────────────║");
            System.out.println("║ [1] Deposit into your OWN account    ║");
            System.out.println("║ [2] Deposit into a DIFFERENT account ║");
            System.out.println("║ [3] Go Back                          ║");
            System.out.println("║ [4] Exit System                      ║");
            System.out.println("╚══════════════════════════════════════╝");
            System.out.print("\nChoose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume the newline
            switch (choice) {
                case 1:
                    Utility.clearConsole();
                    System.out.println("\nDEPOSIT INTO OWN ACCOUNT PORTAL");
                    System.out.print("\nEnter deposit amount: ");
                    double amount = scanner.nextDouble();
                    scanner.nextLine();

                    if (amount > 0) {
                        kycInformation.setTransactionId(Utility.generateTransactionId());
                        // Update current balance in the database
                        DatabaseOperations.updateCurrentBalanceWhenDepositIntoOwn(amount, kycInformation);
                        Utility.clearConsole();
                        System.out.println("\nDEPOSIT INTO OWN ACCOUNT PORTAL ");
                        // Print deposit success message
                        System.out.println("\nDeposited: Rs." + amount);
                        String transactionType = "Deposit";
                        DatabaseOperations.insertIntoTableTransactionHistory(kycInformation, amount, transactionType);
                        // Retrieve the updated balance from the database
                        double updatedBalance = DatabaseOperations.getCurrentBalanceFromDatabase(kycInformation);

                        // Set the updated balance in the KycInformation instance
                        kycInformation.setCurrentBalance(updatedBalance);

                        // Print the current balance
                        System.out.println("Current Balance: Rs." + kycInformation.getCurrentBalance());
                        Utility.wantToContinue();
                    } else {
                        System.out.println("Invalid deposit amount. Please enter a positive value.");
                    }

                    break;
                case 2:
                    Utility.clearConsole();
                    System.out.println("\nDEPOSIT INTO A DIFFERENT ACCOUNT PORTAL");
                    System.out.print("\nEnter the ACCOUNT number to be deposited: ");
                    String accountNo = scanner.nextLine();
                    if (doesAccountExist(accountNo)) {
                        System.out.print("Enter deposit amount: ");
                        double amount2 = scanner.nextDouble();
                        DatabaseOperations.updateOwnCurrentBalanceWhenDepositedIntoDifferent(amount2, kycInformation);
                        String transactionType = "Deposit ";
                        kycInformation.setTransactionId(Utility.generateTransactionId());
                        DatabaseOperations.insertIntoTableTransactionHistory(kycInformation, amount2, transactionType);
                        DatabaseOperations.updateTheirCurrentBalanceWhenDepositIntoDifferent(amount2, accountNo);
                        Utility.clearConsole();
                        System.out.println("\nDEPOSIT INTO A DIFFERENT ACCOUNT PORTAL\n ");
                        System.out.println("Deposited to " + accountNo + ": Rs." + amount2);
                        System.out.println("Current Balance: Rs." + DatabaseOperations.getCurrentBalanceFromDatabase(kycInformation));
                        Utility.wantToContinue();
                    } else {
                        System.out.println("Account not found or invalid account number.");
                    }
                    break;
                case 3:
                    exitMenu = true;
                    Utility.clearConsole();
                    break;
                case 4:
                    Utility.clearConsole();
                    System.out.println("\n\nThank you for banking with us. Have a good day!");
                    System.out.println("SUMAN MEGA BANK\n\n");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }


        }
    }

    public static void performWithdraw(KycInformation kycInformation) throws SQLException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nWITHDRAWAL PORTAL");
        System.out.print("\nEnter withdrawal amount: Rs.");
        double withdrawalAmount = scanner.nextDouble();
        scanner.nextLine();

        if (withdrawalAmount > 0 && withdrawalAmount <= DatabaseOperations.getCurrentBalanceFromDatabase(kycInformation)) {
            kycInformation.setTransactionId(Utility.generateTransactionId());
            // Update current balance in the database
            DatabaseOperations.updateCurrentBalanceWhenWithdrawal(withdrawalAmount, kycInformation);
            String transactionType = "Withdrawal";
            DatabaseOperations.insertIntoTableTransactionHistory(kycInformation, withdrawalAmount, transactionType);
            // Print deposit success message
            Utility.clearConsole();
            System.out.println("\nWITHDRAWAL PORTAL");
            System.out.println("\nWithdrawn: Rs." + withdrawalAmount);

            // Retrieve the updated balance from the database
            double updatedBalance = DatabaseOperations.getCurrentBalanceFromDatabase(kycInformation);

            // Set the updated balance in the KycInformation instance
            kycInformation.setCurrentBalance(updatedBalance);

            // Print the current balance
            System.out.println("Current Balance: " + kycInformation.getCurrentBalance());
            Utility.wantToContinue();

        } else {
            Utility.clearConsole();
            System.out.println("Invalid withdrawal amount. Withdrawal denied.");
            Utility.wantToContinue();
        }


    }

    public static void checkBalance(KycInformation kycInformation) throws SQLException, ClassNotFoundException {
        double balance = DatabaseOperations.getCurrentBalanceFromDatabase(kycInformation);
        System.out.println("\nVIEW CURRENT BALANCE PORTAL");
        System.out.println("\n+----------------------+------------------------------+");
        System.out.println("|   Account Number     |   " + kycInformation.getAccountNumber() + "           |");
        System.out.println("+----------------------+------------------------------+");
        System.out.println("|   Account Name       |   " + kycInformation.getAccountName() + "             |");
        System.out.println("+----------------------+------------------------------+");
        System.out.println("|   Current Balance    |   Rs." + balance + "                  |");
        System.out.println("+----------------------+------------------------------+");
        Utility.wantToContinue();

    }

    public static void viewTransactionHistory(KycInformation kycInformation) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseOperations.getDatabaseConnection();
        String query = "SELECT * FROM transactionhistory WHERE account_number = ? ORDER BY transaction_time DESC";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, kycInformation.getAccountNumber());
        ResultSet resultSet = statement.executeQuery();

        System.out.println("\nTRANSACTION HISTORY PORTAL \n\nAccount Number: " + kycInformation.getAccountNumber());
        System.out.println("Account Name: " + kycInformation.getAccountName() + "\n");
        System.out.println("--------------------------------------------------------------------------------------------------");
        System.out.printf("| %-20s | %-15s | %-20s | %-30s |\n", "Transaction ID", "Amount", "Transaction Type", "Transaction Time");
        System.out.println("|------------------------------------------------------------------------------------------------|");

        if (resultSet.next()) {
            do {

                String transactionId = resultSet.getString("transaction_id");
                double amount = resultSet.getDouble("amount");
                String transactionType = resultSet.getString("transaction_type");
                Timestamp transactionTime = resultSet.getTimestamp("transaction_time");

                System.out.printf("| %-20s | %-15.2f | %-20s | %-30s |\n", transactionId, amount, transactionType, transactionTime);
                System.out.println("--------------------------------------------------------------------------------------------------");

            } while (resultSet.next());

        } else {
            Utility.clearConsole();
            System.out.println("\nTRANSACTION HISTORY PORTAL");
            System.out.println("\nNo transactions found for the specified account number.");
        }

        // Close the resources
        resultSet.close();
        statement.close();
        connection.close();
    }


    public static boolean doesAccountExist(String accountNumber) throws ClassNotFoundException, SQLException {

        try {

            try (Connection connection = DatabaseOperations.getDatabaseConnection()) {
                String query = "SELECT COUNT(*) FROM CustomerInformation WHERE account_number = ?";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, accountNumber);

                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (resultSet.next()) {
                            int count = resultSet.getInt(1);
                            return count > 0;
                        }
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to check account existence in the database.");
        }

        return false;
    }

    public static void showCustomerDetails(KycInformation kycInformation) {
        System.out.println("+------------------------+------------------------+");

        int tableWidth = 45; // Adjust the width as needed
        // Assuming tableWidth is the total width of the table
        int padding = (tableWidth - "Customer Information".length()) / 2;

        System.out.printf("|%" + padding + "sCustomer Account Details %" + padding + "s|%n", "", "");
        System.out.println("+------------------------+------------------------+");
        System.out.printf("| %-22s : %-22s |%n", "Account Number", kycInformation.getAccountNumber());
        System.out.printf("| %-22s : %-22s |%n", "Account Name", kycInformation.getAccountName());
        System.out.printf("| %-22s : %-22d |%n", "Age", kycInformation.getAge());
        System.out.printf("| %-22s : %-22d |%n", "Phone Number", kycInformation.getPhoneNumber());
        System.out.printf("| %-22s : %-22s |%n", "Address", kycInformation.getAddress());
        System.out.printf("| %-22s : %-22s |%n", "Password", kycInformation.getPassword());
        System.out.printf("| %-22s : %-22d |%n", "ATM Pin", kycInformation.getPin());
        System.out.printf("| %-22s : Rs.%-19.2f |%n", "Current Balance", kycInformation.getCurrentBalance());
        System.out.println("+------------------------+------------------------+");

    }

    public static void accessAtm(KycInformation kycInformation) throws ClassNotFoundException, SQLException {
        Scanner scanner = new Scanner(System.in);
        Utility.clearConsole();
        System.out.println("\nATM WITHDRAWAL PORTAL");
//        if (amount > 0) {
//            if (amount <= DatabaseOperations.getCurrentBalanceFromDatabase(kycInformation)) {
        System.out.print("\nEnter your PIN: ");
        int pin = scanner.nextInt();
        scanner.nextLine();
        boolean exitAtmOperations = false;
        if (pin == DatabaseOperations.getPinFromDatabase(kycInformation)) {
            while (!exitAtmOperations) {
                Utility.clearConsole();
                System.out.println();
                System.out.println("               ATM WITHDRAWAL PORTAL");
                System.out.println();
                System.out.println("╔═══════════════════════════════════════════════════╗");
                System.out.println("║                     OPTIONS                       ║");
                System.out.println("╚═══════════════════════════════════════════════════╝");
                System.out.println("╔═══════════════════════════════════════════════════╗");
                System.out.println("║ [1] Rs.500     ║ [2] Rs.1000    ║ [3] Rs.2000     ║");
                System.out.println("╚════════════════║════════════════║═════════════════║");
                System.out.println("║ [4] Rs.3000    ║ [5] Rs.5000    ║ [6] Rs.10000    ║");
                System.out.println("╚════════════════╝════════════════╚═════════════════╝");
                System.out.println("╔════════════════╗                ╔═════════════════╗");
                System.out.println("║ [7] Go back    ║                ║ [8] Exit System ║");
                System.out.println("╚════════════════╝                ╚═════════════════╝");


                System.out.print("\nSelect an option: ");
                int choice = scanner.nextInt();
                scanner.nextLine();  // Consume the newline
                String transactionType = "Withdrawal";

                switch (choice) {
                    case 1:
                        if (DatabaseOperations.getCurrentBalanceFromDatabase(kycInformation) > 500) {
                            Utility.clearConsole();
                            kycInformation.setTransactionId(Utility.generateTransactionId());
                            double amount1 = 500;
                            System.out.println("\nATM WITHDRAWAL PORTAL");
                            System.out.println("\nWithdrawn: Rs." + amount1);
                            DatabaseOperations.updateCurrentBalanceWhenWithdrawal(amount1, kycInformation);
                            double updatedCurrentBalance1 = DatabaseOperations.getCurrentBalanceFromDatabase(kycInformation);
                            kycInformation.setCurrentBalance(updatedCurrentBalance1);
                            System.out.println("Current Balance: Rs." + updatedCurrentBalance1);
                            DatabaseOperations.insertIntoTableTransactionHistory(kycInformation, amount1, transactionType);
                            Utility.wantToContinue();
                            break;
                        } else {
                            System.out.println("\nATM WITHDRAWAL PORTAL\n");
                            Utility.clearConsole();
                            System.out.println("Insufficient balance. Withdrawal denied.");
                            Utility.wantToContinue();
                            break;

                        }

                    case 2:
                        if (DatabaseOperations.getCurrentBalanceFromDatabase(kycInformation) > 1000) {
                            Utility.clearConsole();
                            kycInformation.setTransactionId(Utility.generateTransactionId());
                            double amount2 = 1000;
                            System.out.println("\nATM WITHDRAWAL PORTAL");
                            System.out.println("\nWithdrawn: Rs." + amount2);
                            DatabaseOperations.updateCurrentBalanceWhenWithdrawal(amount2, kycInformation);
                            double updatedCurrentBalance2 = DatabaseOperations.getCurrentBalanceFromDatabase(kycInformation);
                            kycInformation.setCurrentBalance(updatedCurrentBalance2);
                            System.out.println("Current Balance: Rs." + updatedCurrentBalance2);
                            DatabaseOperations.insertIntoTableTransactionHistory(kycInformation, amount2, transactionType);
                            Utility.wantToContinue();
                            break;
                        } else {
                            System.out.println("\nATM WITHDRAWAL PORTAL\n");
                            Utility.clearConsole();
                            System.out.println("Insufficient balance. Withdrawal denied.");
                            Utility.wantToContinue();
                            break;
                        }
                    case 3:

                        if (DatabaseOperations.getCurrentBalanceFromDatabase(kycInformation) > 2000) {
                            Utility.clearConsole();
                            kycInformation.setTransactionId(Utility.generateTransactionId());
                            double amount3 = 2000;
                            System.out.println("\nATM WITHDRAWAL PORTAL");
                            System.out.println("\nWithdrawn: Rs." + amount3);
                            DatabaseOperations.updateCurrentBalanceWhenWithdrawal(amount3, kycInformation);
                            double updatedCurrentBalance3 = DatabaseOperations.getCurrentBalanceFromDatabase(kycInformation);
                            kycInformation.setCurrentBalance(updatedCurrentBalance3);
                            System.out.println("Current Balance: Rs." + updatedCurrentBalance3);
                            DatabaseOperations.insertIntoTableTransactionHistory(kycInformation, amount3, transactionType);
                            Utility.wantToContinue();
                            break;
                        } else {
                            System.out.println("\nATM WITHDRAWAL PORTAL\n");
                            Utility.clearConsole();
                            System.out.println("Insufficient balance. Withdrawal denied.");
                            Utility.wantToContinue();
                            break;

                        }

                    case 4:
                        if (DatabaseOperations.getCurrentBalanceFromDatabase(kycInformation) > 3000) {
                            Utility.clearConsole();
                            kycInformation.setTransactionId(Utility.generateTransactionId());
                            double amount4 = 3000;
                            System.out.println("\nATM WITHDRAWAL PORTAL");
                            System.out.println("\nWithdrawn: Rs." + amount4);
                            DatabaseOperations.updateCurrentBalanceWhenWithdrawal(amount4, kycInformation);
                            double updatedCurrentBalance4 = DatabaseOperations.getCurrentBalanceFromDatabase(kycInformation);
                            kycInformation.setCurrentBalance(updatedCurrentBalance4);
                            System.out.println("Current Balance: Rs." + updatedCurrentBalance4);
                            DatabaseOperations.insertIntoTableTransactionHistory(kycInformation, amount4, transactionType);
                            Utility.wantToContinue();
                            break;
                        } else {
                            System.out.println("\nATM WITHDRAWAL PORTAL\n");
                            Utility.clearConsole();
                            System.out.println("Insufficient balance. Withdrawal denied.");
                            Utility.wantToContinue();
                            break;

                        }

                    case 5:
                        if (DatabaseOperations.getCurrentBalanceFromDatabase(kycInformation) > 5000) {
                            Utility.clearConsole();
                            kycInformation.setTransactionId(Utility.generateTransactionId());
                            double amount5 = 5000;
                            System.out.println("\nATM WITHDRAWAL PORTAL");
                            System.out.println("\nWithdrawn: Rs." + amount5);
                            DatabaseOperations.updateCurrentBalanceWhenWithdrawal(amount5, kycInformation);
                            double updatedCurrentBalance5 = DatabaseOperations.getCurrentBalanceFromDatabase(kycInformation);
                            kycInformation.setCurrentBalance(updatedCurrentBalance5);
                            System.out.println("Current Balance: Rs." + updatedCurrentBalance5);
                            DatabaseOperations.insertIntoTableTransactionHistory(kycInformation, amount5, transactionType);
                            Utility.wantToContinue();
                            break;
                        } else {
                            System.out.println("\nATM WITHDRAWAL PORTAL\n");
                            Utility.clearConsole();
                            System.out.println("Insufficient balance. Withdrawal denied.");
                            Utility.wantToContinue();
                            break;

                        }


                    case 6:
                        if (DatabaseOperations.getCurrentBalanceFromDatabase(kycInformation) > 10000) {
                            Utility.clearConsole();
                            kycInformation.setTransactionId(Utility.generateTransactionId());
                            double amount6 = 10000;
                            System.out.println("\nATM WITHDRAWAL PORTAL");
                            System.out.println("\nWithdrawn: Rs." + amount6);
                            DatabaseOperations.updateCurrentBalanceWhenWithdrawal(amount6, kycInformation);
                            double updatedCurrentBalance6 = DatabaseOperations.getCurrentBalanceFromDatabase(kycInformation);
                            kycInformation.setCurrentBalance(updatedCurrentBalance6);
                            System.out.println("Current Balance: Rs." + updatedCurrentBalance6);
                            DatabaseOperations.insertIntoTableTransactionHistory(kycInformation, amount6, transactionType);
                            Utility.wantToContinue();
                            break;
                        } else {
                            Utility.clearConsole();
                            System.out.println("Insufficient balance. Withdrawal denied.");
                            Utility.wantToContinue();
                            break;

                        }

                    case 7:
                        Utility.clearConsole();
                        exitAtmOperations = true;
                        break;
                    case 8:
                        Utility.clearConsole();
                        System.out.println("\n\nThank you for banking with us. Have a good day!");
                        System.out.println("SUMAN MEGA BANK\n\n");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a valid option.");

                }
            }
        } else {
            Utility.clearConsole();
            System.out.println("\nATM WITHDRAWAL PORTAL");
            System.out.println("\nIncorrect pin! Withdrawal Denied.");
            Utility.wantToContinue();

        }
    }
}
