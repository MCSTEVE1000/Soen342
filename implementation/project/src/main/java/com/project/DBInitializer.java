package com.project;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DBInitializer {
        public static void initialize() {
            Connection conn = DBConnection.getConnection();
    
            if (conn == null) {
                System.err.println("Database connection is not available. Initialization aborted.");
                return;
            }
    
            try (Statement stmt = conn.createStatement()) {
            String createUsersTable = "CREATE TABLE IF NOT EXISTS Users (" +
                    "uniqueId VARCHAR(255) PRIMARY KEY," +
                    "name VARCHAR(255) NOT NULL," +
                    "phoneNumber VARCHAR(255) NOT NULL UNIQUE," +
                    "password VARCHAR(255) NOT NULL," +
                    "userType VARCHAR(50) NOT NULL" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;";
            stmt.execute(createUsersTable);

            String createAdminsTable = "CREATE TABLE IF NOT EXISTS Admins (" +
                    "uniqueId VARCHAR(255) PRIMARY KEY," +
                    "FOREIGN KEY (uniqueId) REFERENCES Users(uniqueId) ON DELETE CASCADE" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;";
            stmt.execute(createAdminsTable);

            String createClientsTable = "CREATE TABLE IF NOT EXISTS Clients (" +
                    "uniqueId VARCHAR(255) PRIMARY KEY," +
                    "isUnderage TINYINT(1)," +
                    "guardianId VARCHAR(255)," +
                    "FOREIGN KEY (uniqueId) REFERENCES Users(uniqueId) ON DELETE CASCADE," +
                    "FOREIGN KEY (guardianId) REFERENCES Clients(uniqueId) ON DELETE SET NULL" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;";
            stmt.execute(createClientsTable);

            String createInstructorsTable = "CREATE TABLE IF NOT EXISTS Instructors (" +
                    "uniqueId VARCHAR(255) PRIMARY KEY," +
                    "specialization VARCHAR(255)," +
                    "availabilities TEXT," +
                    "FOREIGN KEY (uniqueId) REFERENCES Users(uniqueId) ON DELETE CASCADE" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;";
            stmt.execute(createInstructorsTable);

            String createOfferingsTable = "CREATE TABLE IF NOT EXISTS Offerings (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "location TEXT," +
                    "startTime TIME," +
                    "endTime TIME," +
                    "isGroup TINYINT(1)," +
                    "capacity INT," +
                    "date DATE," +
                    "offeringName VARCHAR(255)," +
                    "instructorId VARCHAR(255)," +
                    "available TINYINT(1)," +
                    "visible TINYINT(1)," +
                    "enrolled INT," +
                    "FOREIGN KEY (instructorId) REFERENCES Instructors(uniqueId) ON DELETE SET NULL" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;";
            stmt.execute(createOfferingsTable);

            String createBookingsTable = "CREATE TABLE IF NOT EXISTS Bookings (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "clientId VARCHAR(255)," +
                    "offeringId INT," +
                    "FOREIGN KEY (clientId) REFERENCES Clients(uniqueId) ON DELETE CASCADE," +
                    "FOREIGN KEY (offeringId) REFERENCES Offerings(id) ON DELETE CASCADE" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;";
            stmt.execute(createBookingsTable);

            System.out.println("Database tables initialized.");
        } catch (SQLException e) {
                System.err.println("Error initializing database.");
                e.printStackTrace();
            }
    }
}
