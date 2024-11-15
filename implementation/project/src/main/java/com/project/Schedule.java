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
        String query = "SELECT id FROM Offerings WHERE visible = 1 AND available = 1";
        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            boolean hasOfferings = false;
            while (rs.next()) {
                hasOfferings = true;
                int id = rs.getInt("id");
                Offering offering = Offering.getOfferingById(id);
                if (offering != null) {
                    System.out.println(offering);
                }
            }
            if (!hasOfferings) {
                System.out.println("No public offerings available at the moment.");
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving public offerings.");
            e.printStackTrace();
        }
    }

    public static void viewPublicOfferingsWithIDs() {
        Connection conn = DBConnection.getConnection();
        System.out.println("Available Offerings:");
        String query = "SELECT id FROM Offerings WHERE visible = 1 AND available = 1";
        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            boolean hasOfferings = false;
            while (rs.next()) {
                hasOfferings = true;
                int id = rs.getInt("id");
                Offering offering = Offering.getOfferingById(id);
                if (offering != null) {
                    System.out.println("ID: " + offering.getId() + " - " + offering);
                }
            }
            if (!hasOfferings) {
                System.out.println("No available offerings at the moment.");
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving public offerings with IDs.");
            e.printStackTrace();
        }
    }

    public static Offering getOfferingByIndex(int index) {
        Connection conn = DBConnection.getConnection();
        String query = "SELECT id FROM Offerings WHERE visible = 1 AND available = 1 LIMIT 1 OFFSET ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, index);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    return Offering.getOfferingById(id);
                }
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
        String query = "SELECT id FROM Offerings WHERE instructorId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, instructor.getUniqueId());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Offering offering = Offering.getOfferingById(rs.getInt("id"));
                    if (offering != null) {
                        offerings.add(offering);
                    }
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
        String query = "SELECT id FROM Offerings WHERE visible = 0 AND available = 1";
        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
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
        String query = "SELECT id FROM Offerings";
        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            boolean hasOfferings = false;
            while (rs.next()) {
                hasOfferings = true;
                Offering offering = Offering.getOfferingById(rs.getInt("id"));
                if (offering != null) {
                    System.out.println(offering);
                }
            }
            if (!hasOfferings) {
                System.out.println("No offerings available.");
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all offerings.");
            e.printStackTrace();
        }
    }
}
