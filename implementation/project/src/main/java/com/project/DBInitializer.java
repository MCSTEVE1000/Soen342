package com.project;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DBInitializer {
    public static void initialize() {
        Connection conn = DBConnection.getConnection();

        try (Statement stmt = conn.createStatement()) {
            String createUsersTable = "CREATE TABLE IF NOT EXISTS Users (" +
                    "uniqueId TEXT PRIMARY KEY," +
                    "name TEXT NOT NULL," +
                    "phoneNumber TEXT NOT NULL UNIQUE," +
                    "password TEXT NOT NULL," +
                    "userType TEXT NOT NULL" +
                    ");";
            stmt.execute(createUsersTable);

            String createAdminsTable = "CREATE TABLE IF NOT EXISTS Admins (" +
                    "uniqueId TEXT PRIMARY KEY" +
                    ");";
            stmt.execute(createAdminsTable);

            String createClientsTable = "CREATE TABLE IF NOT EXISTS Clients (" +
                    "uniqueId TEXT PRIMARY KEY," +
                    "isUnderage INTEGER," +
                    "guardianId TEXT" +
                    ");";
            stmt.execute(createClientsTable);

            String createInstructorsTable = "CREATE TABLE IF NOT EXISTS Instructors (" +
                    "uniqueId TEXT PRIMARY KEY," +
                    "specialization TEXT," +
                    "availabilities TEXT" +
                    ");";
            stmt.execute(createInstructorsTable);

            String createOfferingsTable = "CREATE TABLE IF NOT EXISTS Offerings (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "location TEXT," +
                    "startTime TEXT," +
                    "endTime TEXT," +
                    "isGroup INTEGER," +
                    "capacity INTEGER," +
                    "date TEXT," +
                    "offeringName TEXT," +
                    "instructorId TEXT," +
                    "available INTEGER," +
                    "visible INTEGER," +
                    "enrolled INTEGER" + // Added enrolled field
                    ");";
            stmt.execute(createOfferingsTable);

            String createBookingsTable = "CREATE TABLE IF NOT EXISTS Bookings (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "clientId TEXT," +
                    "offeringId INTEGER" +
                    ");";
            stmt.execute(createBookingsTable);

            System.out.println("Database tables initialized.");
        } catch (SQLException e) {
            System.err.println("Error initializing database.");
            e.printStackTrace();
        }
    }
}
