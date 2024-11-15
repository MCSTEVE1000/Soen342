package com.project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Schedule {

    public static void viewPublicOfferings() {
        Connection conn = DBConnection.getConnection();
        System.out.println("Public Offerings:");
        try {
            String query = "SELECT id FROM Offerings WHERE visible = 1 AND available = 1";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                Offering offering = Offering.getOfferingById(id);
                if (offering != null) {
                    System.out.println(offering);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving public offerings.");
            e.printStackTrace();
        }
    }

    public static void viewPublicOfferingsWithIDs() {
        Connection conn = DBConnection.getConnection();
        System.out.println("Available Offerings:");
        try {
            String query = "SELECT id FROM Offerings WHERE visible = 1 AND available = 1";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                Offering offering = Offering.getOfferingById(id);
                if (offering != null) {
                    System.out.println("ID: " + offering.id + " - " + offering);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving public offerings with IDs.");
            e.printStackTrace();
        }
    }

    public static Offering getOfferingByIndex(int index) {
        Connection conn = DBConnection.getConnection();
        try {
            String query = "SELECT id FROM Offerings WHERE visible = 1 AND available = 1 LIMIT 1 OFFSET ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, index);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                return Offering.getOfferingById(id);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving offering by index.");
            e.printStackTrace();
        }
        return null;
    }

    public static List<Offering> getOfferingsByInstructor(Instructor instructor) {
        List<Offering> offerings = new java.util.ArrayList<>();
        Connection conn = DBConnection.getConnection();

        try {
            String query = "SELECT id FROM Offerings WHERE instructorId = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, instructor.getUniqueId());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Offering offering = Offering.getOfferingById(rs.getInt("id"));
                if (offering != null) {
                    offerings.add(offering);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving offerings by instructor.");
            e.printStackTrace();
        }

        return offerings;
    }

    public static List<Offering> getAllAvailableOfferings() {
        List<Offering> offerings = new java.util.ArrayList<>();
        Connection conn = DBConnection.getConnection();

        try {
            String query = "SELECT id FROM Offerings WHERE visible = 0 AND available = 1";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                Offering offering = Offering.getOfferingById(id);
                if (offering != null) {
                    offerings.add(offering);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all available offerings.");
            e.printStackTrace();
        }

        return offerings;
    }

    public static void viewAllOfferings() {
        Connection conn = DBConnection.getConnection();
        System.out.println("All Offerings:");
        try {
            String query = "SELECT id FROM Offerings";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Offering offering = Offering.getOfferingById(rs.getInt("id"));
                if (offering != null) {
                    System.out.println(offering);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all offerings.");
            e.printStackTrace();
        }
    }
}
