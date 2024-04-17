import java.sql.*;
import java.util.*;
public  class CreateTable
{
    public  static void main(String args[])
    {
        Connection cn=null;
        Statement stmt;
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Mobile","root","");
            stmt=cn.createStatement();
                    stmt.executeUpdate("CREATE TABLE Users (" +
                   "user_id INT AUTO_INCREMENT PRIMARY KEY," +
                   "username VARCHAR(20)," +
                   "email VARCHAR(50)," +
                   "password VARCHAR(100)," +
                   "shipping_address VARCHAR(255)," +
                   "payment_info VARCHAR(255)" +
                   ")");

                   stmt.executeUpdate("CREATE TABLE Products (" +
                   "product_id INT AUTO_INCREMENT PRIMARY KEY," +
                   "name VARCHAR(100)," +
                   "description TEXT," +
                   "price DECIMAL(10, 2)," +
                   "brand VARCHAR(50)," +
                   "specifications TEXT," +
                   "quantity_in_stock INT" +
                   ")");
            cn.close();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }
}