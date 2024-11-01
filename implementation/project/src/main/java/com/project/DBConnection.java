package com.project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static Connection connection = null;

    public static synchronized Connection getConnection() {
        if (connection == null) {
            try {
                String url = "jdbc:sqlite:app_database.db";
                connection = DriverManager.getConnection(url);
                System.out.println("Connected to SQLite database.");
            } catch (SQLException e) {
                System.err.println("Failed to connect to SQLite database.");
                e.printStackTrace();
            }
        }
        return connection;
    }
}
