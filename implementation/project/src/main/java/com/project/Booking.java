package com.project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class Booking {
    private int id;
    private String clientId;
    private int offeringId;
    private static Scanner scanner = new Scanner(System.in);

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

            // Update the offering's enrolled count and availability
            Offering offering = Offering.getOfferingById(offeringId);
            if (offering != null) {
                offering.incrementEnrolled();
                offering.updateInDB();
            }

            return true;
        } catch (SQLException e) {
            System.err.println("Error saving booking to database.");
            e.printStackTrace();
            return false;
        }
    }

    // New method to check if the client already has a booking for the offering
    public static boolean hasExistingBooking(String clientId, int offeringId) {
        Connection conn = DBConnection.getConnection();
        try {
            String query = "SELECT COUNT(*) FROM Bookings WHERE clientId = ? AND offeringId = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, clientId);
            pstmt.setInt(2, offeringId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0; // Returns true if at least one booking exists
            }
        } catch (SQLException e) {
            System.err.println("Error checking existing booking.");
            e.printStackTrace();
        }
        return false;
    }

    public static void makeBooking(Client client) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Available Offerings:");
        Schedule.viewPublicOfferingsWithIDs();
        System.out.print("Enter the ID of the offering you want to book: ");
        int offeringId = scanner.nextInt();
        scanner.nextLine();

        Offering offering = Offering.getOfferingById(offeringId);
        if (offering != null && offering.isAvailable()) {
            // Check if the client already has a booking for this offering
            if (Booking.hasExistingBooking(client.getUniqueId(), offeringId)) {
                System.out.println("You have already booked this offering.");
                return; // Do not proceed with booking
            }

            Booking newBooking = new Booking(client.getUniqueId(), offering.id);
            if (newBooking.saveToDB()) {
                System.out.println("Booking successful.");
            } else {
                System.out.println("Booking failed.");
            }
        } else {
            System.out.println("Offering not available or does not exist.");
        }
    }

    public static void viewClientBookings(Client client) {
        Connection conn = DBConnection.getConnection();
        try {
            String query = "SELECT * FROM Bookings WHERE clientId = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, client.getUniqueId());
            ResultSet rs = pstmt.executeQuery();

            System.out.println("Your Bookings:");
            while (rs.next()) {
                int bookingId = rs.getInt("id");
                int offeringId = rs.getInt("offeringId");
                Offering offering = Offering.getOfferingById(offeringId);
                if (offering != null) {
                    System.out.println("Booking ID: " + bookingId + " - " + offering);
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
        try {
            String query = "SELECT * FROM Bookings WHERE clientId = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, client.getUniqueId());
            ResultSet rs = pstmt.executeQuery();

            List<Integer> bookingIds = new ArrayList<>();
            System.out.println("Your Bookings:");
            while (rs.next()) {
                int bookingId = rs.getInt("id");
                int offeringId = rs.getInt("offeringId");
                Offering offering = Offering.getOfferingById(offeringId);
                if (offering != null) {
                    System.out.println("Booking ID: " + bookingId + " - " + offering);
                    bookingIds.add(bookingId);
                }
            }

            if (bookingIds.isEmpty()) {
                System.out.println("No bookings to cancel.");
                return;
            }

            System.out.print("Enter the Booking ID you want to cancel: ");
            int bookingIdToCancel = scanner.nextInt();
            scanner.nextLine();

            if (bookingIds.contains(bookingIdToCancel)) {
                // Retrieve offeringId before deleting
                String getOfferingIdQuery = "SELECT offeringId FROM Bookings WHERE id = ?";
                PreparedStatement pstmtGetOfferingId = conn.prepareStatement(getOfferingIdQuery);
                pstmtGetOfferingId.setInt(1, bookingIdToCancel);
                ResultSet rsOfferingId = pstmtGetOfferingId.executeQuery();
                int offeringId = -1;
                if (rsOfferingId.next()) {
                    offeringId = rsOfferingId.getInt("offeringId");
                }

                // Now delete the booking
                String deleteBooking = "DELETE FROM Bookings WHERE id = ?";
                PreparedStatement pstmtDelete = conn.prepareStatement(deleteBooking);
                pstmtDelete.setInt(1, bookingIdToCancel);
                pstmtDelete.executeUpdate();

                // Update the offering's enrolled count and availability
                if (offeringId != -1) {
                    Offering offering = Offering.getOfferingById(offeringId);
                    if (offering != null) {
                        offering.decrementEnrolled();
                        offering.updateInDB();
                    }
                }

                System.out.println("Booking canceled.");
            } else {
                System.out.println("Invalid Booking ID.");
            }
        } catch (SQLException e) {
            System.err.println("Error canceling booking.");
            e.printStackTrace();
        }
    }

    // Methods for guardians to manage bookings for their wards

    public static void makeBookingForWard(Client guardian) {
        List<Client> wards = getWards(guardian);
        if (wards.isEmpty()) {
            System.out.println("You have no wards to make bookings for.");
            return;
        }

        System.out.println("Select a ward to make a booking for:");
        for (int i = 0; i < wards.size(); i++) {
            Client ward = wards.get(i);
            System.out.println((i + 1) + ". " + ward.getName() + " (Phone: " + ward.getPhoneNumber() + ")");
        }
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice < 1 || choice > wards.size()) {
            System.out.println("Invalid choice.");
            return;
        }

        Client ward = wards.get(choice - 1);

        // Proceed with booking for the selected ward
        System.out.println("Available Offerings:");
        Schedule.viewPublicOfferingsWithIDs();
        System.out.print("Enter the ID of the offering you want to book: ");
        int offeringId = scanner.nextInt();
        scanner.nextLine();

        Offering offering = Offering.getOfferingById(offeringId);
        if (offering != null && offering.isAvailable()) {
            if (Booking.hasExistingBooking(ward.getUniqueId(), offeringId)) {
                System.out.println("This ward has already booked this offering.");
                return;
            }

            Booking newBooking = new Booking(ward.getUniqueId(), offering.id);
            if (newBooking.saveToDB()) {
                System.out.println("Booking successful for " + ward.getName() + ".");
            } else {
                System.out.println("Booking failed.");
            }
        } else {
            System.out.println("Offering not available or does not exist.");
        }
    }

    public static void viewWardBookings(Client guardian) {
        List<Client> wards = getWards(guardian);
        if (wards.isEmpty()) {
            System.out.println("You have no wards to view bookings for.");
            return;
        }

        System.out.println("Select a ward to view bookings for:");
        for (int i = 0; i < wards.size(); i++) {
            Client ward = wards.get(i);
            System.out.println((i + 1) + ". " + ward.getName() + " (Phone: " + ward.getPhoneNumber() + ")");
        }
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice < 1 || choice > wards.size()) {
            System.out.println("Invalid choice.");
            return;
        }

        Client ward = wards.get(choice - 1);
        viewClientBookings(ward);
    }

    public static void cancelWardBooking(Client guardian) {
        List<Client> wards = getWards(guardian);
        if (wards.isEmpty()) {
            System.out.println("You have no wards to cancel bookings for.");
            return;
        }

        System.out.println("Select a ward to cancel a booking for:");
        for (int i = 0; i < wards.size(); i++) {
            Client ward = wards.get(i);
            System.out.println((i + 1) + ". " + ward.getName() + " (Phone: " + ward.getPhoneNumber() + ")");
        }
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice < 1 || choice > wards.size()) {
            System.out.println("Invalid choice.");
            return;
        }

        Client ward = wards.get(choice - 1);
        cancelBooking(ward);
    }

    // Helper method to get wards of a guardian
    private static List<Client> getWards(Client guardian) {
        Connection conn = DBConnection.getConnection();
        List<Client> wards = new ArrayList<>();
        try {
            String query = "SELECT * FROM Clients WHERE guardianId = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, guardian.getUniqueId());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String wardId = rs.getString("uniqueId");
                Client ward = Client.findClientById(wardId);
                if (ward != null) {
                    wards.add(ward);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving wards.");
            e.printStackTrace();
        }
        return wards;
    }
}
