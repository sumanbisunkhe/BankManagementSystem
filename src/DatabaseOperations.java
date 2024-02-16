import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseOperations {
    public static Connection getDatabaseConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/BankManagementSystem", "root", "#b$um@n1thrives");
    }

    public static void createDatabase() throws SQLException, ClassNotFoundException {
        Connection connection = getDatabaseConnection();
        String dataBaseCreationQuery = "CREATE DATABASE IF NOT EXISTS BankManagementSystem";
        PreparedStatement statement = connection.prepareStatement(dataBaseCreationQuery);
        statement.execute();
//        System.out.println("DATABASE CREATED SUCCESSFULLY");
        connection.close();
    }


    public static void createTableCustomerInformation() throws SQLException, ClassNotFoundException {
        Connection connection = getDatabaseConnection();
        String createTableQuery = "CREATE TABLE IF NOT EXISTS customerinformation ("
                + "account_number VARCHAR(255) NOT NULL PRIMARY KEY, "
                + "account_name VARCHAR(255) NOT NULL, "
                + "age INT NOT NULL, "
                + "address VARCHAR(255) NOT NULL, "
                + "current_balance DOUBLE NOT NULL, "
                + "pin INT NOT NULL, "
                + "phone_number BIGINT NOT NULL, "
                + "password VARCHAR(255) NOT NULL)";
        PreparedStatement statement = connection.prepareStatement(createTableQuery);
        statement.execute();
//        System.out.println("CustomerInformation TABLE CREATED SUCCESSFULLY");
    }

    public static void createTableTransactionHistory() throws SQLException, ClassNotFoundException {
        // Establish the database connection
        Connection connection = getDatabaseConnection();

        // Create a Statement object
        Statement statement = connection.createStatement();

        // Execute the SQL query to create the table with an index on transaction_time
        String createTableQuery = "CREATE TABLE IF NOT EXISTS transactionhistory ("
                + "transaction_id VARCHAR(255) NOT NULL PRIMARY KEY,"
                + "account_number VARCHAR(255) NOT NULL,"
                + "amount DOUBLE NOT NULL,"
                + "transaction_type VARCHAR(255) NOT NULL,"
                + "transaction_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,"
                + "INDEX idx_transaction_time (transaction_time DESC))";

        statement.executeUpdate(createTableQuery);

//        System.out.println("Table 'transaction history' created successfully.");

        // Close the resources
        statement.close();
        connection.close();
    }



    // Insert account details into the database
    public static void insertIntoTableCustomerInfo(KycInformation kycInformation) throws SQLException, ClassNotFoundException {

        Connection connection = getDatabaseConnection();
        String query = "INSERT INTO customerinformation (account_number, account_name, age, address, current_balance, pin,phone_number, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, kycInformation.getAccountNumber());
        statement.setString(2, kycInformation.getAccountName());
        statement.setInt(3, kycInformation.getAge());
        statement.setString(4, kycInformation.getAddress());
        statement.setDouble(5, kycInformation.getCurrentBalance());
        statement.setInt(6, kycInformation.getPin());
        statement.setDouble(7, kycInformation.getPhoneNumber());
        statement.setString(8, kycInformation.getPassword());
        statement.executeUpdate();
//        System.out.println("Insertion into Customer Information table successful.");
    }

    public static void insertIntoTableTransactionHistory(KycInformation kycInformation, double amount, String transactionType) throws SQLException, ClassNotFoundException {
        Connection connection = getDatabaseConnection();
        String query = "INSERT INTO transactionhistory (transaction_id, account_number, amount, transaction_type, transaction_time) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, kycInformation.getTransactionId());
        statement.setString(2, kycInformation.getAccountNumber());
        statement.setDouble(3, amount);
        statement.setString(4, transactionType);

        // Convert current timestamp to Timestamp
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        statement.setTimestamp(5, timestamp);

        statement.executeUpdate();

        // Close the resources
        statement.close();
        connection.close();
    }




    public static void updateCurrentBalanceWhenDepositIntoOwn(double amount, KycInformation kycInformation) throws ClassNotFoundException, SQLException {
        double currentBalance = getCurrentBalanceFromDatabase(kycInformation);
        double updatedBalance = currentBalance + amount;

        Connection connection = getDatabaseConnection();
        String query = "UPDATE customerinformation SET current_balance = ? WHERE account_number = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setDouble(1, updatedBalance);
        statement.setString(2, kycInformation.getAccountNumber());  // Use the parameter here
        statement.executeUpdate();
//        System.out.println("updateCurrentBalanceWhenDeposit successful.");


    }

    public static void updateTheirCurrentBalanceWhenDepositIntoDifferent(double amount2, String accountNo) throws ClassNotFoundException, SQLException {
        double currentBalance = getCurrentBalanceFromDatabaseByAccountNumber(accountNo);
        double updatedBalance = currentBalance + amount2;

        Connection connection = getDatabaseConnection();
        String query = "UPDATE customerinformation SET current_balance = ? WHERE account_number = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setDouble(1, updatedBalance);
        statement.setString(2, accountNo);  // Use the parameter here
        statement.executeUpdate();
//        System.out.println("updateTheirCurrentBalanceWhenDepositIntoDifferent account successful.");
    }

    public static void updateCurrentBalanceWhenWithdrawal(double withdrawalAmount, KycInformation kycInformation) throws SQLException, ClassNotFoundException {

        double currentBalance = getCurrentBalanceFromDatabase(kycInformation);
        double updatedBalance = currentBalance - withdrawalAmount;

        Connection connection = getDatabaseConnection();
        String query = "UPDATE customerinformation SET current_balance = ? WHERE account_number = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setDouble(1, updatedBalance);
        statement.setString(2, kycInformation.getAccountNumber());  // Use the parameter here
        statement.executeUpdate();
//        System.out.println("updateCurrentBalanceWhenWithdrawal successful.");
    }
    public static void updateOwnCurrentBalanceWhenDepositedIntoDifferent(double amount, KycInformation kycInformation) throws SQLException, ClassNotFoundException {

        double currentBalance = getCurrentBalanceFromDatabase(kycInformation);
        double updatedBalance = currentBalance - amount;

        Connection connection = getDatabaseConnection();
        String query = "UPDATE customerinformation SET current_balance = ? WHERE account_number = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setDouble(1, updatedBalance);
        statement.setString(2, kycInformation.getAccountNumber());  // Use the parameter here
        statement.executeUpdate();
//        System.out.println("updateCurrentBalanceWhenWithdrawal successful.");
    }

    // Get the current balance from the database
    public static double getCurrentBalanceFromDatabase(KycInformation kycInformation) throws ClassNotFoundException, SQLException {
        Connection connection = getDatabaseConnection();
        String query = "SELECT current_balance FROM customerinformation WHERE account_number = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, kycInformation.getAccountNumber());
        ResultSet resultSet = statement.executeQuery();
//        System.out.println("Executing query: " + query);
//        System.out.println("Account number in query: " + kycInformation.getAccountNumber());


        // Check if there are any rows in the result set
        if (resultSet.next()) {
            // If there are rows, retrieve the "current_balance" column value
            return resultSet.getDouble("current_balance");
        } else {
            // Handle the case where no rows are returned (e.g., account not found)
            // You can customize the message or return a default value if appropriate
            throw new SQLException("Account not found or empty result set.");
        }
    }
    public static List<Double> getAllPins() throws ClassNotFoundException, SQLException {
        Connection connection = getDatabaseConnection();
        String query = "SELECT pin FROM customerinformation";
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();

        List<Double> pins = new ArrayList<>();

        // Iterate through the result set and add each pin to the list
        while (resultSet.next()) {
            double pin = resultSet.getDouble("pin");
            pins.add(pin);
        }

        // Close resources
        resultSet.close();
        statement.close();
        connection.close();

        // Return the list of pins
        return pins;
    }

    public static double getPinFromDatabase(KycInformation kycInformation) throws ClassNotFoundException, SQLException {
        Connection connection = getDatabaseConnection();
        String query = "SELECT pin FROM customerinformation WHERE account_number = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, kycInformation.getAccountNumber());
        ResultSet resultSet = statement.executeQuery();
//        System.out.println("Executing query: " + query);
//        System.out.println("Account number in query: " + kycInformation.getAccountNumber());


        // Check if there are any rows in the result set
        if (resultSet.next()) {
            // If there are rows, retrieve the "current_balance" column value
            return resultSet.getDouble("pin");
        } else {
            // Handle the case where no rows are returned (e.g., account not found)
            // You can customize the message or return a default value if appropriate
            throw new SQLException("Incorrect pin! ATM withdrawal denied.");
        }
    }

    public static List<String> getPhoneNumbersFromDatabase() throws SQLException, ClassNotFoundException {
        List<String> phoneNumbers = new ArrayList<>();

        // Establish the database connection
        Connection connection = getDatabaseConnection();

        // Create a Statement object
        Statement statement = connection.createStatement();

        // Execute the SQL query to retrieve phone numbers
        String query = "SELECT phone_number FROM customerinformation";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();

        // Process the result set
        while (resultSet.next()) {
            String phoneNumber = resultSet.getString("phone_number");
            phoneNumbers.add(phoneNumber);
        }

        // Close the resources
        resultSet.close();
        preparedStatement.close();
        statement.close();
        connection.close();

        return phoneNumbers;
    }

    public static double getCurrentBalanceFromDatabaseByAccountNumber(String accountNo) throws ClassNotFoundException, SQLException {
        Connection connection = getDatabaseConnection();
        String query = "SELECT current_balance FROM customerinformation WHERE account_number = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, accountNo);
        ResultSet resultSet = statement.executeQuery();
//        System.out.println("Executing query: " + query);
//        System.out.println("Account number in query: " + accountNo);


        // Check if there are any rows in the result set
        if (resultSet.next()) {
            // If there are rows, retrieve the "current_balance" column value
            return resultSet.getDouble("current_balance");
        } else {
            // Handle the case where no rows are returned (e.g., account not found)
            // You can customize the message or return a default value if appropriate
            throw new SQLException("Account not found or empty result set.");
        }
    }


    public static String getAccountNameFromDatabase(long phoneNumber) throws ClassNotFoundException, SQLException {
        Connection connection = getDatabaseConnection();
        String query = "SELECT account_name FROM customerinformation WHERE phone_number = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, phoneNumber);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("account_name");
                } else {
                    // Handle the case where no rows are returned (e.g., account not found)
                    return "Account Not Found";  // You can customize the message or return null if appropriate
                }
            }
        }
    }


    public static void setCurrentLoggedInUserInformation(KycInformation kycInformation) throws ClassNotFoundException, SQLException {
        Connection connection = getDatabaseConnection();
        {
            String query = "SELECT * FROM CustomerInformation WHERE phone_number = ? AND password = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setLong(1, kycInformation.getPhoneNumber());
                statement.setString(2, kycInformation.getPassword());

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        kycInformation.setAccountName(resultSet.getString("account_name"));
                        kycInformation.setAccountNumber(resultSet.getString("account_number"));

                        //Remaining fields set here
                    }
                }
            }
        }
    }

}






