import java.sql.*;
import java.util.Scanner;

public class UserM {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/mobile";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            while (true) {
                System.out.println("Select action:");
                System.out.println("1) Register new user");
                System.out.println("2) Login");
                System.out.println("3) Exit");

                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        registerUser(connection, scanner);
                        break;
                    case 2:
                        int userId = loginUser(connection, scanner);
                        if (userId != -1) {
                            userMenu(connection, scanner, userId);
                        }
                        break;
                    case 3:
                        System.out.println("Exiting program...");
                        return;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 3.");
                }
            }
        } catch (SQLException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private static void registerUser(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Registration Form:");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter shipping address: ");
        String shippingAddress = scanner.nextLine();
        System.out.print("Enter payment info: ");
        String paymentInfo = scanner.nextLine();

        String sql = "INSERT INTO Users (username, email, password, shipping_address, payment_info) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);
            preparedStatement.setString(4, shippingAddress);
            preparedStatement.setString(5, paymentInfo);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);
                        System.out.println("User registered successfully with user ID: " + userId);
                        userMenu(connection, scanner, userId);
                    }
                }
            } else {
                System.out.println("Failed to register user.");
            }
        }
    }

    private static int loginUser(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Login Form:");
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        String sql = "SELECT * FROM Users WHERE email = ? AND password = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int userId = resultSet.getInt("user_id");
                System.out.println("Login successful. Welcome, " + resultSet.getString("username") + "! Your user ID is: " + userId);
                return userId;
            } else {
                System.out.println("Invalid email or password. Please try again.");
            }
        }
        return -1;
    }

    private static void userMenu(Connection connection, Scanner scanner, int userId) throws SQLException {
        while (true) {
            System.out.println("User Menu:");
            System.out.println("1) Buy a product");
            System.out.println("2) Search for a product");
            System.out.println("3) Exit");

            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    buyProduct(connection, scanner, userId);
                    break;
                case 2:
                    searchProduct(connection, scanner);
                    break;
                case 3:
                    System.out.println("Exiting user menu...");
                    return;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 3.");
            }
        }
    }

    private static void buyProduct(Connection connection, Scanner scanner, int userId) throws SQLException {
        System.out.println("Enter the ID of the product you want to buy:");
        int productId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        // Check if the product exists
        String productQuery = "SELECT * FROM Products WHERE product_id = ?";
        try (PreparedStatement productStatement = connection.prepareStatement(productQuery)) {
            productStatement.setInt(1, productId);
            ResultSet productResult = productStatement.executeQuery();

            if (productResult.next()) {
                // Product found, proceed with purchase
                double productPrice = productResult.getDouble("price");
                System.out.println("Product found: " + productResult.getString("name") + ", Price: " + productPrice);

                // Insert purchase record into Orders table
                String purchaseQuery = "INSERT INTO Orders (user_id, product_id) VALUES (?, ?)";
                try (PreparedStatement purchaseStatement = connection.prepareStatement(purchaseQuery)) {
                    purchaseStatement.setInt(1, userId);
                    purchaseStatement.setInt(2, productId);
                    int rowsInserted = purchaseStatement.executeUpdate();

                    if (rowsInserted > 0) {
                        System.out.println("Purchase successful!");
                    } else {
                        System.out.println("Failed to complete purchase.");
                    }
                }
            } else {
                System.out.println("Product not found.");
            }
        }
    }

    private static void searchProduct(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Enter the name or keyword of the product you want to search:");
        String searchKeyword = scanner.nextLine();

        // Search for products by name or keyword
        String searchQuery = "SELECT * FROM Products WHERE name LIKE ?";
        try (PreparedStatement searchStatement = connection.prepareStatement(searchQuery)) {
            searchStatement.setString(1, "%" + searchKeyword + "%");
            ResultSet searchResult = searchStatement.executeQuery();

            if (searchResult.next()) {
                System.out.println("Search results:");
                do {
                    System.out.println("Product ID: " + searchResult.getInt("product_id") +
                            ", Name: " + searchResult.getString("name") +
                            ", Price: " + searchResult.getDouble("price"));
                } while (searchResult.next());
            } else {
                System.out.println("No products found matching the search keyword.");
            }
        }
    }
}
