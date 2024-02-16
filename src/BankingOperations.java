import java.sql.SQLException;
import java.util.Scanner;

public class BankingOperations {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        Utility.clearConsole();
        DatabaseOperations.createDatabase();
        DatabaseOperations.createTableCustomerInformation();
        DatabaseOperations.createTableTransactionHistory();
        KycInformation kycInformation = new KycInformation();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println();
            System.out.println("    \"BETTER SAfE THAN SORRY\"");
            System.out.println();
            System.out.println("            Welcome");
            System.out.println("╔═══════════════════════════════╗");
            System.out.println("║       SUMAN MEGA BANK         ║");
            System.out.println("║───────────────────────────────║");
            System.out.println("║ [1] Create a New Account      ║");
            System.out.println("║ [2] Log into an Account       ║");
            System.out.println("║ [3] Exit System               ║");
            System.out.println("╚═══════════════════════════════╝");


            System.out.print("\nEnter your choice: ");
            int choice1 = scanner.nextInt();
            scanner.nextLine();
            Utility.clearConsole();
            switch (choice1) {
                case 1:
                    System.out.println("\n\nINFORMATION FILL UP PORTAL\n");
                    System.out.print("Enter Account Name: ");
                    kycInformation.setAccountName(scanner.nextLine());
                    System.out.print("Enter your Age: ");
                    kycInformation.setAge(scanner.nextInt());
                    scanner.nextLine();

                    if (kycInformation.getAge() >= 18) {
                        BankManagementSystem.createAccount(kycInformation);
                        DatabaseOperations.insertIntoTableCustomerInfo(kycInformation);
                        Utility.wantToContinue();
                        break;
                    } else {
                        Utility.clearConsole();
                        System.out.println("\nERROR DISPLAY PORTAL");
                        System.out.println("\nAccount applicants must be 18 or older. Do continue to return to the main menu.");
                        Utility.wantToContinue();
                        break;
                    }
                case 2:
                    System.out.println("\nACCOUNT VALIDATION PORTAL ");
                    System.out.print("\nEnter phone number: ");
                    long phoneNumber = scanner.nextLong();
                    scanner.nextLine();  // Consume the newline


                    System.out.print("Enter password: ");
                    String password = scanner.nextLine();


                    if (Utility.validateUser(phoneNumber, password)) {
                        Utility.clearConsole();
                        System.out.println("\nWelcome to our SERVICES, " + DatabaseOperations.getAccountNameFromDatabase(phoneNumber) + ".\n");
                        kycInformation.setTransactionId(Utility.generateTransactionId());
                        kycInformation.setPhoneNumber(phoneNumber);
                        kycInformation.setPassword(password);
                        DatabaseOperations.setCurrentLoggedInUserInformation(kycInformation);
                        performBankOperations(scanner, kycInformation);
                    } else {
                        Utility.clearConsole();
                        System.out.println("\nERROR DISPLAY PORTAL");
                        System.out.println("\nInvalid phone number or password. Access denied.");
                        Utility.wantToContinue();
                    }
                    break;

                case 3:
                    Utility.clearConsole();
                    System.out.println("\n\nThank you for banking with us. Have a good day!");
                    System.out.println("SUMAN MEGA BANK\n\n");
                    System.exit(0);
            }
        }
    }


    private static void performBankOperations(Scanner scanner, KycInformation kycInformation) throws SQLException, ClassNotFoundException {
        boolean exitBankOperations = false;

        while (!exitBankOperations) {
            System.out.println("╔═══════════════════════════════════════════╗");
            System.out.println("║          SUMAN MEGA BANK SERVICES         ║");
            System.out.println("║═══════════════════════════════════════════║");
            System.out.println("║ [1]  Deposit                              ║");
            System.out.println("║ [2]  Withdraw                             ║");
            System.out.println("║ [3]  Check Balance                        ║");
            System.out.println("║ [4]  ATM                                  ║");
            System.out.println("║ [5]  Transaction History                  ║");
            System.out.println("║ [6]  Log Out                              ║");
            System.out.println("║ [7]  Exit System                          ║");
            System.out.println("╚═══════════════════════════════════════════╝");


            System.out.print("\nEnter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume the newline

            switch (choice) {
                case 1:
                    Utility.clearConsole();
                    BankManagementSystem.performDeposit(kycInformation);
                    break;
                case 2:
                    Utility.clearConsole();
                    BankManagementSystem.performWithdraw(kycInformation);
                    break;
                case 3:
                    Utility.clearConsole();
                    BankManagementSystem.checkBalance(kycInformation);
                    break;
                case 4:
                    Utility.clearConsole();
                    BankManagementSystem.accessAtm(kycInformation);
                    break;
                case 5:
                    Utility.clearConsole();
                    BankManagementSystem.viewTransactionHistory(kycInformation);
                    Utility.wantToContinue();
                    break;
                case 6:
                    Utility.clearConsole();
                    exitBankOperations = true;  // Set the exit flag to true to break out of the loop
                    break;
                case 7:
                    Utility.clearConsole();
                    System.out.println("\n\nThank you for banking with us. Have a good day!");
                    System.out.println("SUMAN MEGA BANK\n\n");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }


}
