package com.project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import org.mindrot.jbcrypt.BCrypt;

public class Client extends Users {
    private static Scanner scanner = new Scanner(System.in);

    private boolean isUnderage;
    private String guardianId;

    public Client(String name, String phoneNumber, String password, boolean isUnderage, String guardianId) {
        super(name, phoneNumber, password, "Client");
        this.isUnderage = isUnderage;
        this.guardianId = guardianId;
    }

    public boolean isUnderage() { return isUnderage; }
    public String getGuardianId() { return guardianId; }

    @Override
    public boolean register() {
        return saveToDB();
    }

    @Override
    public boolean saveToDB() {
        Connection conn = DBConnection.getConnection();

        try {
            String insertUser = "INSERT INTO Users(uniqueId, name, phoneNumber, password, userType) VALUES(?, ?, ?, ?, ?)";
            PreparedStatement pstmtUser = conn.prepareStatement(insertUser);
            pstmtUser.setString(1, this.uniqueId);
            pstmtUser.setString(2, this.name);
            pstmtUser.setString(3, this.phoneNumber);
            pstmtUser.setString(4, this.password);
            pstmtUser.setString(5, this.userType);
            pstmtUser.executeUpdate();

            String insertClient = "INSERT INTO Clients(uniqueId, isUnderage, guardianId) VALUES(?, ?, ?)";
            PreparedStatement pstmtClient = conn.prepareStatement(insertClient);
            pstmtClient.setString(1, this.uniqueId);
            pstmtClient.setInt(2, this.isUnderage ? 1 : 0);
            pstmtClient.setString(3, this.guardianId);
            pstmtClient.executeUpdate();

            return true;
        } catch (SQLException e) {
            System.err.println("Error saving client to database.");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteFromDB() {
        Connection conn = DBConnection.getConnection();

        try {
            String deleteClient = "DELETE FROM Clients WHERE uniqueId = ?";
            PreparedStatement pstmtClient = conn.prepareStatement(deleteClient);
            pstmtClient.setString(1, this.uniqueId);
            pstmtClient.executeUpdate();

            String deleteUser = "DELETE FROM Users WHERE uniqueId = ?";
            PreparedStatement pstmtUser = conn.prepareStatement(deleteUser);
            pstmtUser.setString(1, this.uniqueId);
            pstmtUser.executeUpdate();

            return true;
        } catch (SQLException e) {
            System.err.println("Error deleting client from database.");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean login(String identifier, String password) {
        Connection conn = DBConnection.getConnection();

        try {
            String query = "SELECT * FROM Users WHERE phoneNumber = ? AND userType = 'Client'";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, identifier);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String dbPassword = rs.getString("password");
                if (BCrypt.checkpw(password, dbPassword)) {
                    this.uniqueId = rs.getString("uniqueId");
                    this.name = rs.getString("name");
                    this.phoneNumber = rs.getString("phoneNumber");
                    this.password = dbPassword;

                    String clientQuery = "SELECT * FROM Clients WHERE uniqueId = ?";
                    PreparedStatement clientPstmt = conn.prepareStatement(clientQuery);
                    clientPstmt.setString(1, this.uniqueId);
                    ResultSet clientRs = clientPstmt.executeQuery();

                    if (clientRs.next()) {
                        this.isUnderage = clientRs.getInt("isUnderage") == 1;
                        this.guardianId = clientRs.getString("guardianId");
                    }

                    Session.getInstance(this);
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error during client login.");
            e.printStackTrace();
        }
        return false;
    }

    public static void clientRegistration() {
        System.out.println("Client Registration");
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        System.out.print("Enter your phone number: ");
        String phoneNumber = scanner.nextLine();
        System.out.print("Enter a password: ");
        String password = scanner.nextLine();
        System.out.print("Are you underage? (yes/no): ");
        String underageResponse = scanner.nextLine();
        boolean isUnderage = underageResponse.equalsIgnoreCase("yes");
        String guardianId = null;

        if (isUnderage) {
            System.out.println("Guardian Information Required.");
            System.out.print("Enter guardian's phone number: ");
            String guardianPhone = scanner.nextLine();

            Client guardian = findClientByPhoneNumber(guardianPhone);
            if (guardian == null) {
                System.out.print("Enter guardian's name: ");
                String guardianName = scanner.nextLine();
                System.out.print("Enter guardian's password: ");
                String guardianPassword = scanner.nextLine();

                guardian = new Client(guardianName, guardianPhone, guardianPassword, false, null);
                if (guardian.register()) {
                    guardianId = guardian.getUniqueId();
                } else {
                    System.out.println("Failed to register guardian.");
                    return;
                }
            } else {
                guardianId = guardian.getUniqueId();
            }
        }

        Client newClient = new Client(name, phoneNumber, password, isUnderage, guardianId);
        if (newClient.register()) {
            System.out.println("Client registered successfully!");
        } else {
            System.out.println("Registration failed.");
        }
    }

    public static void clientLogin() {
        System.out.println("Client Login");
        System.out.print("Enter your phone number: ");
        String phoneNumber = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        Client client = new Client(null, phoneNumber, null, false, null);
        if (client.login(phoneNumber, password)) {
            System.out.println("Logged in successfully!");
            clientMenu(client);
        } else {
            System.out.println("Login failed.");
        }
    }

    static Client findClientByPhoneNumber(String phoneNumber) {
        Connection conn = DBConnection.getConnection();

        try {
            String query = "SELECT * FROM Users WHERE phoneNumber = ? AND userType = 'Client'";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, phoneNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String uniqueId = rs.getString("uniqueId");
                String name = rs.getString("name");
                String password = rs.getString("password");

                String clientQuery = "SELECT * FROM Clients WHERE uniqueId = ?";
                PreparedStatement clientPstmt = conn.prepareStatement(clientQuery);
                clientPstmt.setString(1, uniqueId);
                ResultSet clientRs = clientPstmt.executeQuery();

                if (clientRs.next()) {
                    boolean isUnderage = clientRs.getInt("isUnderage") == 1;
                    String guardianId = clientRs.getString("guardianId");

                    Client client = new Client(name, phoneNumber, password, isUnderage, guardianId);
                    client.uniqueId = uniqueId;
                    return client;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding client by phone number.");
            e.printStackTrace();
        }
        return null;
    }

    private static void clientMenu(Client client) {
        int choice;
        do {
            System.out.println("\nClient Menu:");
            System.out.println("0. Logout");
            System.out.println("1. View Available Offerings");
            System.out.println("2. Make a Booking");
            System.out.println("3. View Your Bookings");
            System.out.println("4. Cancel a Booking");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> Schedule.viewPublicOfferings();
                case 2 -> Booking.makeBooking(client);
                case 3 -> Booking.viewClientBookings(client);
                case 4 -> Booking.cancelBooking(client);
                case 0 -> {
                    System.out.println("Logging out...");
                    Session.clearSession();
                }
                default -> System.out.println("Invalid choice.");
            }
        } while (choice != 0);
    }
}
