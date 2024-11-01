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
            String query = "SELECT id FROM Offerings WHERE visible = 1";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            int index = 0;
            while (rs.next()) {
                int id = rs.getInt("id");
                Offering offering = Offering.getOfferingById(id);
                if (offering != null) {
                    System.out.println(index + ". " + offering);
                    index++;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving public offerings.");
            e.printStackTrace();
        }
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

    public static List<Offering> getOfferingsForInstructor(Instructor instructor) {
        List<Offering> offerings = new java.util.ArrayList<>();
        Connection conn = DBConnection.getConnection();

        try {
            String query = "SELECT id FROM Offerings WHERE visible = 0";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                Offering offering = Offering.getOfferingById(id);
                if (offering != null) {
                    for (City city : instructor.availabilities.cities) {
                        if (offering.getLocation().getCity().getName().equalsIgnoreCase(city.getName())) {
                            offerings.add(offering);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving offerings for instructor.");
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
