import java.sql.*;
public  class Databasedemo
{
    public  static void main(String args[])
    {
        Connection cn=null;
        Statement stmt;
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            cn=DriverManager.getConnection("jdbc:mysql://localhost:3306","root","");
            stmt=cn.createStatement();
            stmt.executeUpdate("Create database Mobile");
            cn.close();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }
}