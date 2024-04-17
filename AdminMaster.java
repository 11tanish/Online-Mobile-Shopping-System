import java.sql.*;
import java.util.Scanner;

public class AdminMaster {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/mobile";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            while (true) {
                System.out.println("Select action:");
                System.out.println("1) Insert a product");
                System.out.println("2) Update a product");
                System.out.println("3) Delete a product");
                System.out.println("4) Display all products");
                System.out.println("5) Exit");

                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        insertProduct(connection, scanner);
                        break;
                    case 2:
                        updateProduct(connection, scanner);
                        break;
                    case 3:
                        deleteProduct(connection, scanner);
                        break;
                    case 4:
                        displayAllProducts(connection);
                        break;
                    case 5:
                        System.out.println("Exiting program...");
                        return;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 6.");
                }
            }
        } catch (SQLException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private static void insertProduct(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter product name: ");
        String name = scanner.nextLine();
        System.out.print("Enter product description: ");
        String description = scanner.nextLine();
        System.out.print("Enter product price: ");
        double price = scanner.nextDouble();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter product brand: ");
        String brand = scanner.nextLine();
        System.out.print("Enter product specifications: ");
        String specifications = scanner.nextLine();
        System.out.print("Enter quantity in stock: ");
        int quantity = scanner.nextInt();

        String insertQuery = "INSERT INTO Products (name, description, price, brand, specifications, quantity_in_stock) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            preparedStatement.setDouble(3, price);
            preparedStatement.setString(4, brand);
            preparedStatement.setString(5, specifications);
            preparedStatement.setInt(6, quantity);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Product inserted successfully.");
            } else {
                System.out.println("Failed to insert product.");
            }
        }
    }

    private static void updateProduct(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter product ID to update: ");
        int productId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        String selectQuery = "SELECT * FROM Products WHERE product_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
            preparedStatement.setInt(1, productId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                System.out.println("Current details:");
                System.out.println("Name: " + resultSet.getString("name"));
                System.out.println("Description: " + resultSet.getString("description"));
                System.out.println("Price: " + resultSet.getDouble("price"));
                System.out.println("Brand: " + resultSet.getString("brand"));
                System.out.println("Specifications: " + resultSet.getString("specifications"));
                System.out.println("Quantity in stock: " + resultSet.getInt("quantity_in_stock"));

                System.out.print("Enter new product name: ");
                String name = scanner.nextLine();
                System.out.print("Enter new product description: ");
                String description = scanner.nextLine();
                System.out.print("Enter new product price: ");
                double price = scanner.nextDouble();
                scanner.nextLine(); // Consume newline
                System.out.print("Enter new product brand: ");
                String brand = scanner.nextLine();
                System.out.print("Enter new product specifications: ");
                String specifications = scanner.nextLine();
                System.out.print("Enter new quantity in stock: ");
                int quantity = scanner.nextInt();

                String updateQuery = "UPDATE Products SET name = ?, description = ?, price = ?, brand = ?, specifications = ?, quantity_in_stock = ? WHERE product_id = ?";
                try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                    updateStatement.setString(1, name);
                    updateStatement.setString(2, description);
                    updateStatement.setDouble(3, price);
                    updateStatement.setString(4, brand);
                    updateStatement.setString(5, specifications);
                    updateStatement.setInt(6, quantity);
                    updateStatement.setInt(7, productId);

                    int rowsAffected = updateStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Product updated successfully.");
                    } else {
                        System.out.println("Failed to update product.");
                    }
                }
            } else {
                System.out.println("Product with ID " + productId + " not found.");
            }
        }
    }

    private static void deleteProduct(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter product ID to delete: ");
        int productId = scanner.nextInt();

        String deleteQuery = "DELETE FROM Products WHERE product_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
            preparedStatement.setInt(1, productId);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Product deleted successfully.");
            } else {
                System.out.println("Product with ID " + productId + " not found.");
            }
        }
    }

    private static void displayAllProducts(Connection connection) throws SQLException {
        String selectQuery = "SELECT * FROM Products";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectQuery)) {
            System.out.println("All Products:=");
            System.out.println("\n");
            while (resultSet.next()) {
                System.out.println("ID: " + resultSet.getInt("product_id"));
                System.out.println("Name: " + resultSet.getString("name"));
                System.out.println("Description: " + resultSet.getString("description"));
                System.out.println("Price: " + resultSet.getDouble("price"));
                System.out.println("Brand: " + resultSet.getString("brand"));
                System.out.println("Specifications: " + resultSet.getString("specifications"));
                System.out.println("Quantity in stock: " + resultSet.getInt("quantity_in_stock"));
                System.out.println("------------------------");
            }
        }
    }
}