package com.project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

public class Admin extends Users {
    private static final Scanner scanner = new Scanner(System.in);

    public Admin(String name, String phoneNumber, String password) {
        super(name, phoneNumber, password, "Admin");
    }

    public boolean existsInDB() {
        return super.existsInDB();
    }

    public static void adminRegistration() {
        System.out.println("Admin Registration");
        System.out.print("Enter admin key (for security): ");
        String adminKey = scanner.nextLine();

        if (!adminKey.equals("secureAdminKey")) {
            System.out.println("Invalid admin key.");
            return;
        }

        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter phone number: ");
        String phoneNumber = scanner.nextLine();

        // Check if phoneNumber already exists
        if (Users.findUserByPhoneNumber(phoneNumber) != null) {
            System.out.println("Error: Phone number already in use.");
            return;
        }

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        Admin newAdmin = new Admin(name, phoneNumber, password);
        if (newAdmin.saveToDB()) {
            System.out.println("Admin registered successfully.");
        } else {
            System.out.println("Registration failed.");
        }
    }

    @Override
    public boolean saveToDB() {
        Connection conn = DBConnection.getConnection();

        try {
            // Check if phoneNumber already exists in Users table
            if (Users.findUserByPhoneNumber(this.phoneNumber) != null) {
                System.out.println("Error: Phone number already in use.");
                return false;
            }

            String insertUser = "INSERT INTO Users(uniqueId, name, phoneNumber, password, userType) VALUES(?, ?, ?, ?, ?)";
            try (PreparedStatement pstmtUser = conn.prepareStatement(insertUser)) {
                pstmtUser.setString(1, this.uniqueId);
                pstmtUser.setString(2, this.name);
                pstmtUser.setString(3, this.phoneNumber);
                pstmtUser.setString(4, this.password);
                pstmtUser.setString(5, this.userType);
                pstmtUser.executeUpdate();
            }

            String insertAdmin = "INSERT INTO Admins(uniqueId) VALUES(?)";
            try (PreparedStatement pstmtAdmin = conn.prepareStatement(insertAdmin)) {
                pstmtAdmin.setString(1, this.uniqueId);
                pstmtAdmin.executeUpdate();
            }

            return true;
        } catch (SQLException e) {
            System.err.println("Error saving admin to database.");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteFromDB() {
        Connection conn = DBConnection.getConnection();

        try {
            String deleteAdmin = "DELETE FROM Admins WHERE uniqueId = ?";
            try (PreparedStatement pstmtAdmin = conn.prepareStatement(deleteAdmin)) {
                pstmtAdmin.setString(1, this.uniqueId);
                pstmtAdmin.executeUpdate();
            }

            String deleteUser = "DELETE FROM Users WHERE uniqueId = ?";
            try (PreparedStatement pstmtUser = conn.prepareStatement(deleteUser)) {
                pstmtUser.setString(1, this.uniqueId);
                pstmtUser.executeUpdate();
            }

            return true;
        } catch (SQLException e) {
            System.err.println("Error deleting admin from database.");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean login(String identifier, String password) {
        Connection conn = DBConnection.getConnection();

        String query = "SELECT * FROM Users WHERE phoneNumber = ? AND userType = 'Admin'";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, identifier);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String dbPassword = rs.getString("password");
                    if (BCrypt.checkpw(password, dbPassword)) {
                        this.uniqueId = rs.getString("uniqueId");
                        this.name = rs.getString("name");
                        this.phoneNumber = rs.getString("phoneNumber");
                        this.password = dbPassword;
                        Session.getInstance(this);
                        return true;
                    } else {
                        System.out.println("Incorrect password.");
                    }
                } else {
                    System.out.println("Admin not found with the given phone number.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error during admin login.");
            e.printStackTrace();
        }
        return false;
    }

    public static void adminLogin() {
        System.out.println("Admin Login");
        System.out.print("Enter phone number: ");
        String phoneNumber = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        Admin admin = new Admin(null, phoneNumber, null);
        if (admin.login(phoneNumber, password)) {
            System.out.println("Logged in successfully.");
            adminMenu();
        } else {
            System.out.println("Login failed.");
        }
    }

    private static void adminMenu() {
        int choice;
        do {
            System.out.println("\nAdmin Menu:");
            System.out.println("0. Logout");
            System.out.println("1. Create Offering");
            System.out.println("2. View All Offerings");
            System.out.println("3. Delete Instructor or Client Account");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> createOffering();
                case 2 -> Schedule.viewAllOfferings();
                case 3 -> deleteAccount();
                case 0 -> {
                    System.out.println("Logging out...");
                    Session.clearSession();
                }
                default -> System.out.println("Invalid choice.");
            }
        } while (choice != 0);
    }

    private static void createOffering() {
        System.out.println("Creating a new offering...");

        System.out.print("Enter offering name: ");
        String offeringName = scanner.nextLine();
        System.out.print("Enter date (YYYY-MM-DD): ");
        String date = scanner.nextLine();
        System.out.print("Enter start time (HH:MM): ");
        String startTime = scanner.nextLine();
        System.out.print("Enter end time (HH:MM): ");
        String endTime = scanner.nextLine();
        System.out.print("Is this a group offering? (yes/no): ");
        String isGroupResponse = scanner.nextLine();
        boolean isGroup = isGroupResponse.equalsIgnoreCase("yes");
        System.out.print("Enter capacity: ");
        int capacity = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter organization name: ");
        String organization = scanner.nextLine();
        System.out.print("Enter address: ");
        String address = scanner.nextLine();
        System.out.print("Enter city: ");
        String city = scanner.nextLine();
        System.out.print("Enter room: ");
        String room = scanner.nextLine();

        Location location = new Location(address, city, organization, room);

        Offering newOffering = new Offering(location, startTime, endTime, isGroup, capacity, date, offeringName);

        // Check for conflicts before saving
        if (newOffering.hasConflict()) {
            System.out.println("The location is already booked for the specified date and time.");
            return;
        }

        if (newOffering.saveToDB()) {
            System.out.println("Offering created successfully.");
        } else {
            System.out.println("Failed to create offering.");
        }
    }

    private static void deleteAccount() {
        System.out.println("Deleting an account...");

        // Retrieve and display all users
        System.out.println("List of Users:");
        List<Users> usersList = Users.getAllUsers();
        for (Users user : usersList) {
            System.out.println("Name: " + user.getName() + ", Phone Number: " + user.getPhoneNumber() + ", Type: " + user.getUserType());
        }

        System.out.print("Enter the phone number of the account to delete: ");
        String phoneNumber = scanner.nextLine();

        Users user = Users.findUserByPhoneNumber(phoneNumber);
        if (user != null && user.deleteFromDB()) {
            System.out.println("Account deleted successfully.");
        } else {
            System.out.println("Failed to delete account.");
        }
    }

    @Override
    public boolean register() {
        return false;
    }
}
