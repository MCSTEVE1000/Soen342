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
    private static final Scanner scanner = new Scanner(System.in);

    public Booking(String clientId, int offeringId) {
        this.clientId = clientId;
        this.offeringId = offeringId;
    }

    public boolean saveToDB() {
        Connection conn = DBConnection.getConnection();

        try {
            conn.setAutoCommit(false);

            String insertBooking = "INSERT INTO Bookings(clientId, offeringId) VALUES(?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertBooking, PreparedStatement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, clientId);
                pstmt.setInt(2, offeringId);
                pstmt.executeUpdate();

                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        this.id = rs.getInt(1);
                    }
                }
            }

            Offering offering = Offering.getOfferingById(offeringId);
            if (offering != null) {
                offering.incrementEnrolled();
                offering.updateInDB();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Error saving booking to database.");
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Error rolling back transaction.");
                rollbackEx.printStackTrace();
            }
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                System.err.println("Error resetting auto-commit.");
                ex.printStackTrace();
            }
        }
    }

    public static boolean hasExistingBooking(String clientId, int offeringId) {
        Connection conn = DBConnection.getConnection();
        String query = "SELECT COUNT(*) FROM Bookings WHERE clientId = ? AND offeringId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, clientId);
            pstmt.setInt(2, offeringId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0; 
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking existing booking.");
            e.printStackTrace();
        }
        return false;
    }

    public static void makeBooking(Client client) {
        System.out.println("Available Offerings:");
        Schedule.viewPublicOfferingsWithIDs();
        System.out.print("Enter the ID of the offering you want to book: ");
        int offeringId = scanner.nextInt();
        scanner.nextLine();

        Offering offering = Offering.getOfferingById(offeringId);
        if (offering != null && offering.isAvailable()) {
            if (Booking.hasExistingBooking(client.getUniqueId(), offeringId)) {
                System.out.println("You have already booked this offering.");
                return; 
            }

            Booking newBooking = new Booking(client.getUniqueId(), offering.getId());
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
        String query = "SELECT * FROM Bookings WHERE clientId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, client.getUniqueId());
            try (ResultSet rs = pstmt.executeQuery()) {

                System.out.println("Your Bookings:");
                boolean hasBookings = false;
                while (rs.next()) {
                    hasBookings = true;
                    int bookingId = rs.getInt("id");
                    int offeringId = rs.getInt("offeringId");
                    Offering offering = Offering.getOfferingById(offeringId);
                    if (offering != null) {
                        System.out.println("Booking ID: " + bookingId + " - " + offering);
                    }
                }
                if (!hasBookings) {
                    System.out.println("You have no bookings.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving client bookings.");
            e.printStackTrace();
        }
    }

    public static void cancelBooking(Client client) {
        Connection conn = DBConnection.getConnection();
        String query = "SELECT * FROM Bookings WHERE clientId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, client.getUniqueId());
            try (ResultSet rs = pstmt.executeQuery()) {

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
                    
                    String getOfferingIdQuery = "SELECT offeringId FROM Bookings WHERE id = ?";
                    int offeringId = -1;
                    try (PreparedStatement pstmtGetOfferingId = conn.prepareStatement(getOfferingIdQuery)) {
                        pstmtGetOfferingId.setInt(1, bookingIdToCancel);
                        try (ResultSet rsOfferingId = pstmtGetOfferingId.executeQuery()) {
                            if (rsOfferingId.next()) {
                                offeringId = rsOfferingId.getInt("offeringId");
                            }
                        }
                    }

                    
                    try {
                        conn.setAutoCommit(false);

                        
                        String deleteBooking = "DELETE FROM Bookings WHERE id = ?";
                        try (PreparedStatement pstmtDelete = conn.prepareStatement(deleteBooking)) {
                            pstmtDelete.setInt(1, bookingIdToCancel);
                            pstmtDelete.executeUpdate();
                        }

                       
                        if (offeringId != -1) {
                            Offering offering = Offering.getOfferingById(offeringId);
                            if (offering != null) {
                                offering.decrementEnrolled();
                                offering.updateInDB();
                            }
                        }

                        conn.commit();
                        System.out.println("Booking canceled.");
                    } catch (SQLException e) {
                        conn.rollback();
                        System.err.println("Error canceling booking.");
                        e.printStackTrace();
                    } finally {
                        conn.setAutoCommit(true);
                    }
                } else {
                    System.out.println("Invalid Booking ID.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error canceling booking.");
            e.printStackTrace();
        }
    }


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

            Booking newBooking = new Booking(ward.getUniqueId(), offering.getId());
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

    
    private static List<Client> getWards(Client guardian) {
        Connection conn = DBConnection.getConnection();
        List<Client> wards = new ArrayList<>();
        String query = "SELECT * FROM Clients WHERE guardianId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, guardian.getUniqueId());
            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    String wardId = rs.getString("uniqueId");
                    Client ward = Client.findClientById(wardId);
                    if (ward != null) {
                        wards.add(ward);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving wards.");
            e.printStackTrace();
        }
        return wards;
    }
}
