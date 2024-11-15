package com.project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static Connection conn = null;

    public static void connect() {
        try {
            
            Class.forName("com.mysql.cj.jdbc.Driver");

           
            String url = "jdbc:mysql://localhost:3306/project_db?useSSL=false&serverTimezone=UTC";
            String user = "project_user";
            String password = "1234";

            
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to MySQL database.");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
            conn = null;
        } catch (SQLException e) {
            System.err.println("Failed to connect to MySQL database.");
            e.printStackTrace();
            conn = null;
        }
    }

    public static Connection getConnection() {
        if (conn == null) {
            connect();
        }
        return conn;
    }
}
