package com.project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.List;

public class Booking {
    private int id;
    private String clientId;
    private int offeringId;

    public Booking(String clientId, int offeringId) {
        this.clientId = clientId;
        this.offeringId = offeringId;
    }

    public boolean saveToDB() {
        Connection conn = DBConnection.getConnection();

        try {
            String insertBooking = "INSERT INTO Bookings(clientId, offeringId) VALUES(?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(insertBooking, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, clientId);
            pstmt.setInt(2, offeringId);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                this.id = rs.getInt(1);
            }

            Offering offering = Offering.getOfferingById(offeringId);
            if (offering != null) {
                offering.available = false;
                offering.updateInDB();
            }

            return true;
        } catch (SQLException e) {
            System.err.println("Error saving booking to database.");
            e.printStackTrace();
            return false;
        }
    }

    public static void makeBooking(Client client) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Available Offerings:");
        Schedule.viewPublicOfferings();
        System.out.print("Enter the number of the offering you want to book: ");
        int index = scanner.nextInt();
        scanner.nextLine();

        Offering offering = getOfferingByIndex(index);
        if (offering != null && offering.isAvailable()) {
            Booking newBooking = new Booking(client.getUniqueId(), offering.id);
            if (newBooking.saveToDB()) {
                System.out.println("Booking successful.");
            } else {
                System.out.println("Booking failed.");
            }
        } else {
            System.out.println("Offering not available.");
        }
    }

    private static Offering getOfferingByIndex(int index) {
        Connection conn = DBConnection.getConnection();
        try {
            String query = "SELECT id FROM Offerings WHERE visible = 1 LIMIT 1 OFFSET ?";
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

    public static void viewClientBookings(Client client) {
        Connection conn = DBConnection.getConnection();
        System.out.println("Your Bookings:");
        try {
            String query = "SELECT offeringId FROM Bookings WHERE clientId = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, client.getUniqueId());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int offeringId = rs.getInt("offeringId");
                Offering offering = Offering.getOfferingById(offeringId);
                if (offering != null) {
                    System.out.println(offering);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving client bookings.");
            e.printStackTrace();
        }
    }

    public static void cancelBooking(Client client) {
        Scanner scanner = new Scanner(System.in);
        Connection conn = DBConnection.getConnection();
        System.out.println("Your Bookings:");
        try {
            String query = "SELECT id, offeringId FROM Bookings WHERE clientId = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, client.getUniqueId());
            ResultSet rs = pstmt.executeQuery();

            int index = 0;
            List<Integer> bookingIds = new java.util.ArrayList<>();
            List<Offering> offerings = new java.util.ArrayList<>();

            while (rs.next()) {
                int bookingId = rs.getInt("id");
                int offeringId = rs.getInt("offeringId");
                Offering offering = Offering.getOfferingById(offeringId);
                if (offering != null) {
                    System.out.println(index + ". " + offering);
                    bookingIds.add(bookingId);
                    offerings.add(offering);
                    index++;
                }
            }

            if (bookingIds.isEmpty()) {
                System.out.println("No bookings to cancel.");
                return;
            }

            System.out.print("Enter the number of the booking you want to cancel: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice >= 0 && choice < bookingIds.size()) {
                int bookingIdToCancel = bookingIds.get(choice);

                String deleteBooking = "DELETE FROM Bookings WHERE id = ?";
                PreparedStatement pstmtDelete = conn.prepareStatement(deleteBooking);
                pstmtDelete.setInt(1, bookingIdToCancel);
                pstmtDelete.executeUpdate();

                Offering offering = offerings.get(choice);
                offering.available = true;
                offering.updateInDB();

                System.out.println("Booking canceled.");
            } else {
                System.out.println("Invalid choice.");
            }
        } catch (SQLException e) {
            System.err.println("Error canceling booking.");
            e.printStackTrace();
        }
    }
}
