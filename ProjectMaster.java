import java.sql.*;
import java.util.Scanner;

public class ProjectMaster {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/mobile";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    private static boolean loggedIn = false;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            while (true) {
                System.out.println("Select user type:");
                System.out.println("1) User");
                System.out.println("2) Admin");
                System.out.println("3) Exit");

                System.out.print("Enter your choice: ");
                int userType = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                System.out.println("\n");
                switch (userType) {
                    case 1:
                        userMenu(connection, scanner);
                        break;
                    case 2:
                        adminMenu(connection, scanner);
                        break;
                    case 3:
                        System.out.println("Exiting program...");
                        return;
                    default:
                        System.out.println("Invalid choice. Please enter 1, 2, or 3.");
                }
            }
        } catch (SQLException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private static void userMenu(Connection connection, Scanner scanner) {
        while (true) {
            System.out.println("User Menu:");
            System.out.println("1) Register");
            System.out.println("2) Login");
            System.out.println("3) Buy a product");
            System.out.println("4) Search for a product");
            System.out.println("5) Logout");
            System.out.println("6) Exit");

            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    registerUser(connection, scanner);
                    break;
                case 2:
                    loginUser(connection, scanner);
                    break;
                case 3:
                    if (loggedIn) {
                        buyProduct(connection, scanner);
                    } else {
                        System.out.println("Please login first.");
                    }
                    break;
                case 4:
                    if (loggedIn) {
                        searchProduct(connection, scanner);
                    } else {
                        System.out.println("Please login first.");
                    }
                    break;
                case 5:
                    loggedIn = false;
                    System.out.println("Logged out successfully.");
                    return;
                case 6:
                    System.out.println("Exiting user menu...");
                    return;
                default:
                    System.out.println("Invalid choice. Please enter 1, 2, 3, 4, 5, or 6.");
            }
        }
    }

    private static void adminMenu(Connection connection, Scanner scanner) {
        while (true) {
            System.out.println("Admin Menu:");
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
                    System.out.println("Exiting admin menu...");
                    return;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 5.");
            }
        }
    }

    private static void registerUser(Connection connection, Scanner scanner) {
        // Implementation of user registration
        System.out.println("User Registration");
        // Add code to register a new user
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
    
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
    
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
    
        try {
            String sql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, email);
                preparedStatement.setString(3, password);
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("User registered successfully.");
                } else {
                    System.out.println("User registration failed.");
                }
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while registering user.");
            e.printStackTrace();
        }
    }

    private static void loginUser(Connection connection, Scanner scanner) {
        // Implementation of user login
        System.out.println("User Login");
        // Add code to authenticate the user
        loggedIn = true; // Set loggedIn to true after successful login
        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        loggedIn = true;
                        System.out.println("Login successful.");
                        System.out.println("\n");
                    } else {
                        System.out.println("Invalid username or password.");
                        System.out.println("\n");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while logging in.");
            e.printStackTrace();
        }
    }

    private static void buyProduct(Connection connection, Scanner scanner) {
        if (!loggedIn) {
            System.out.println("Please login first.");
            return;
        }
    
        System.out.println("Enter the ID of the product you want to buy:");
        int productId = scanner.nextInt();
        System.out.println("Enter the name of the product you want to buy:");
        String name = scanner.next();
        System.out.println("Enter the quantity you want to buy:");
        int quantityToBuy = scanner.nextInt();
        scanner.nextLine(); // Consume newline
    
        try {
            // Check if the product exists and has enough quantity
            String productQuery = "SELECT * FROM Products WHERE product_id = ?";
            try (PreparedStatement productStatement = connection.prepareStatement(productQuery)) {
                productStatement.setInt(1, productId);
                ResultSet productResult = productStatement.executeQuery();
    
                if (productResult.next()) {
                    int availableQuantity = productResult.getInt("quantity_in_stock");
                    if (availableQuantity >= quantityToBuy) {
                        // Sufficient quantity available, proceed with purchase
                        double productPrice = productResult.getDouble("price");
                        System.out.println("Product found: " + productResult.getString("name") + ", Price: " + productPrice);
    
                        // Calculate total price and update quantity
                        double totalPrice = productPrice * quantityToBuy;
                        String updateQuery = "UPDATE Products SET quantity_in_stock = ? WHERE product_id = ?";
                        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                            updateStatement.setInt(1, availableQuantity - quantityToBuy);
                            updateStatement.setInt(2, productId);
                            int rowsUpdated = updateStatement.executeUpdate();
                            if (rowsUpdated > 0) {
                                System.out.println("Purchase successful! Total Price: " + totalPrice);
                            } else {
                                System.out.println("Failed to complete purchase.");
                            }
                        }
                    } else {
                        System.out.println("Insufficient quantity available.");
                    }
                } else {
                    System.out.println("Product not found.");
                }
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while processing the purchase.");
            e.printStackTrace();
        }
    }
    

    private static void searchProduct(Connection connection, Scanner scanner) {
        System.out.println("Enter the name of the product you want to search:");
        String productName = scanner.nextLine();
    
        try {
            // Search for the product in the database
            String searchQuery = "SELECT * FROM Products WHERE name LIKE ?";
            try (PreparedStatement searchStatement = connection.prepareStatement(searchQuery)) {
                searchStatement.setString(1, "%" + productName + "%");
                try (ResultSet resultSet = searchStatement.executeQuery()) {
                    boolean found = false;
                    while (resultSet.next()) {
                        found = true;
                        System.out.println("Product ID: " + resultSet.getInt("product_id"));
                        System.out.println("Name: " + resultSet.getString("name"));
                        System.out.println("Price: " + resultSet.getDouble("price"));
                        System.out.println("Quantity in stock: " + resultSet.getInt("quantity_in_stock"));
                        System.out.println();
                    }
                    if (!found) {
                        System.out.println("No products found with the given name.");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while searching for the product.");
            e.printStackTrace();
        }
    }

    private static void insertProduct(Connection connection, Scanner scanner) {
        // Implementation of inserting a new product
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
        catch (SQLException e) {
            System.out.println("An error occurred while inserting the product.");
            e.printStackTrace();
        }
    }
    
    private static void updateProduct(Connection connection, Scanner scanner) {
        // Implementation of updating an existing product
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
        catch (SQLException e) {
            System.out.println("An error occurred while inserting the product.");
            e.printStackTrace();
        }
 
    }
    
    private static void deleteProduct(Connection connection, Scanner scanner) {
        // Implementation of deleting a product
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
        catch (SQLException e) {
            System.out.println("An error occurred while inserting the product.");
            e.printStackTrace();
        }
    }
    
    private static void displayAllProducts(Connection connection) {
        // Implementation of displaying all products
        String selectQuery = "SELECT * FROM Products";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectQuery)) {
            System.out.println("All Products:=");
            System.out.println("\n");
            while (resultSet.next()) {
                System.out.println("ID: " + resultSet.getInt("product_id"));
                System.out.println("\t");
                System.out.println("Name: " + resultSet.getString("name"));
                System.out.println("\t");
                System.out.println("Description: " + resultSet.getString("description"));
                System.out.println("\t");
                System.out.println("Price: " + resultSet.getDouble("price"));
                System.out.println("\t");
                System.out.println("Brand: " + resultSet.getString("brand"));
                System.out.println("\t");
                System.out.println("Specifications: " + resultSet.getString("specifications"));
                System.out.println("\t");
                System.out.println("Quantity in stock: " + resultSet.getInt("quantity_in_stock"));
                System.out.println("\t");
                System.out.println("------------------------");
            }
        }
        catch (SQLException e) {
            System.out.println("An error occurred while inserting the product.");
            e.printStackTrace();
        }
    }

}
