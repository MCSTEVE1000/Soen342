package com.project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import org.mindrot.jbcrypt.BCrypt;

public class Instructor extends Users {
    private static Scanner scanner = new Scanner(System.in);

    private Specialization specialization;
    Availabilities availabilities;

    public Instructor(String name, String phoneNumber, String password, Specialization specialization, Availabilities availabilities) {
        super(name, phoneNumber, password, "Instructor");
        this.specialization = specialization;
        this.availabilities = availabilities;
    }

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

            String insertInstructor = "INSERT INTO Instructors(uniqueId, specialization, availabilities) VALUES(?, ?, ?)";
            PreparedStatement pstmtInstructor = conn.prepareStatement(insertInstructor);
            pstmtInstructor.setString(1, this.uniqueId);
            pstmtInstructor.setString(2, this.specialization.specialization);
            pstmtInstructor.setString(3, this.availabilities.toString());
            pstmtInstructor.executeUpdate();

            return true;
        } catch (SQLException e) {
            System.err.println("Error saving instructor to database.");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteFromDB() {
        Connection conn = DBConnection.getConnection();

        try {
            String deleteInstructor = "DELETE FROM Instructors WHERE uniqueId = ?";
            PreparedStatement pstmtInstructor = conn.prepareStatement(deleteInstructor);
            pstmtInstructor.setString(1, this.uniqueId);
            pstmtInstructor.executeUpdate();

            String deleteUser = "DELETE FROM Users WHERE uniqueId = ?";
            PreparedStatement pstmtUser = conn.prepareStatement(deleteUser);
            pstmtUser.setString(1, this.uniqueId);
            pstmtUser.executeUpdate();

            return true;
        } catch (SQLException e) {
            System.err.println("Error deleting instructor from database.");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean login(String identifier, String password) {
        Connection conn = DBConnection.getConnection();

        try {
            String query = "SELECT * FROM Users WHERE phoneNumber = ? AND userType = 'Instructor'";
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

                    String instructorQuery = "SELECT * FROM Instructors WHERE uniqueId = ?";
                    PreparedStatement instructorPstmt = conn.prepareStatement(instructorQuery);
                    instructorPstmt.setString(1, this.uniqueId);
                    ResultSet instructorRs = instructorPstmt.executeQuery();

                    if (instructorRs.next()) {
                        this.specialization = new Specialization(instructorRs.getString("specialization"));
                        this.availabilities = Availabilities.parseAvailabilities(instructorRs.getString("availabilities"));
                    }

                    Session.getInstance(this);
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error during instructor login.");
            e.printStackTrace();
        }
        return false;
    }

    public static void instructorRegistration() {
        System.out.println("Instructor Registration");
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        System.out.print("Enter your phone number: ");
        String phoneNumber = scanner.nextLine();
        System.out.print("Enter a password: ");
        String password = scanner.nextLine();
        System.out.print("Enter your specialization: ");
        String specializationInput = scanner.nextLine();
        System.out.print("Enter the cities you are available to work in (comma-separated): ");
        String availabilitiesInput = scanner.nextLine();

        Specialization specialization = new Specialization(specializationInput);
        Availabilities availabilities = Availabilities.parseAvailabilities(availabilitiesInput);

        Instructor newInstructor = new Instructor(name, phoneNumber, password, specialization, availabilities);
        if (newInstructor.register()) {
            System.out.println("Instructor registered successfully.");
        } else {
            System.out.println("Registration failed.");
        }
    }

    public static void instructorLogin() {
        System.out.println("Instructor Login");
        System.out.print("Enter your phone number: ");
        String phoneNumber = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        Instructor instructor = new Instructor(null, phoneNumber, null, null, null);
        if (instructor.login(phoneNumber, password)) {
            System.out.println("Logged in successfully.");
            instructorMenu();
        } else {
            System.out.println("Login failed.");
        }
    }

    private static void instructorMenu() {
        int choice;
        do {
            System.out.println("\nInstructor Menu:");
            System.out.println("0. Logout");
            System.out.println("1. View Available Offerings");
            System.out.println("2. Accept an Offering");
            System.out.println("3. View Your Accepted Offerings");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> viewAvailableOfferings();
                case 2 -> acceptOffering();
                case 3 -> viewAcceptedOfferings();
                case 0 -> {
                    System.out.println("Logging out...");
                    Session.clearSession();
                }
                default -> System.out.println("Invalid choice.");
            }
        } while (choice != 0);
    }

    private static void viewAvailableOfferings() {
        System.out.println("Available Offerings:");
        List<Offering> offerings = Schedule.getOfferingsForInstructor((Instructor) Session.getUser());
        for (int i = 0; i < offerings.size(); i++) {
            System.out.println(offerings.get(i));
        }
    }

    private static void acceptOffering() {
        System.out.print("Enter the ID of the offering you want to accept: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        Offering offering = Offering.getOfferingById(id);
        if (offering != null) {
            offering.setInstructor((Instructor) Session.getUser());
            System.out.println("Offering accepted.");
        } else {
            System.out.println("Invalid offering ID.");
        }
    }

    private static void viewAcceptedOfferings() {
        System.out.println("Your Accepted Offerings:");
        List<Offering> offerings = Schedule.getOfferingsByInstructor((Instructor) Session.getUser());
        for (Offering offering : offerings) {
            System.out.println(offering);
        }
    }

    public static Instructor getInstructorById(String instructorId) {
        Connection conn = DBConnection.getConnection();

        try {
            String query = "SELECT * FROM Users WHERE uniqueId = ? AND userType = 'Instructor'";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, instructorId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                String phoneNumber = rs.getString("phoneNumber");
                String password = rs.getString("password");

                String instructorQuery = "SELECT * FROM Instructors WHERE uniqueId = ?";
                PreparedStatement instructorPstmt = conn.prepareStatement(instructorQuery);
                instructorPstmt.setString(1, instructorId);
                ResultSet instructorRs = instructorPstmt.executeQuery();

                if (instructorRs.next()) {
                    String specializationStr = instructorRs.getString("specialization");
                    String availabilitiesStr = instructorRs.getString("availabilities");
                    Specialization specialization = new Specialization(specializationStr);
                    Availabilities availabilities = Availabilities.parseAvailabilities(availabilitiesStr);

                    Instructor instructor = new Instructor(name, phoneNumber, password, specialization, availabilities);
                    instructor.uniqueId = instructorId;
                    return instructor;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving instructor by ID.");
            e.printStackTrace();
        }
        return null;
    }

    public static Instructor findInstructorByPhoneNumber(String phoneNumber) {
        Connection conn = DBConnection.getConnection();

        try {
            String query = "SELECT * FROM Users WHERE phoneNumber = ? AND userType = 'Instructor'";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, phoneNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String uniqueId = rs.getString("uniqueId");
                String name = rs.getString("name");
                String password = rs.getString("password");

                String instructorQuery = "SELECT * FROM Instructors WHERE uniqueId = ?";
                PreparedStatement instructorPstmt = conn.prepareStatement(instructorQuery);
                instructorPstmt.setString(1, uniqueId);
                ResultSet instructorRs = instructorPstmt.executeQuery();

                if (instructorRs.next()) {
                    String specializationStr = instructorRs.getString("specialization");
                    String availabilitiesStr = instructorRs.getString("availabilities");
                    Specialization specialization = new Specialization(specializationStr);
                    Availabilities availabilities = Availabilities.parseAvailabilities(availabilitiesStr);

                    Instructor instructor = new Instructor(name, phoneNumber, password, specialization, availabilities);
                    instructor.uniqueId = uniqueId;
                    return instructor;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding instructor by phone number.");
            e.printStackTrace();
        }
        return null;
    }
}
