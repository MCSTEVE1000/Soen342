package com.project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Offering {
    int id;
    private Location location;
    private String startTime;
    private String endTime;
    boolean available;
    private boolean isGroup;
    private boolean visible = false;
    private int capacity; // Total capacity
    private int enrolled = 0; // Number of enrolled clients
    Instructor instructor;
    private String instructorId;
    private String date;
    private String offeringName;

    public Offering() {
    }

    public Offering(Location location, String startTime, String endTime, boolean isGroup, int capacity, String date, String offeringName) {
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isGroup = isGroup;
        this.capacity = capacity;
        this.date = date;
        this.offeringName = offeringName;
        this.available = true;
        this.enrolled = 0; // Initialize enrolled to zero
    }

    public Location getLocation() {
        return location;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public boolean isAvailable() {
        return available;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public boolean isVisible() {
        return visible;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getEnrolled() {
        return enrolled;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public String getDate() {
        return date;
    }

    public String getOfferingName() {
        return offeringName;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
        this.instructorId = instructor.getUniqueId();
        this.visible = true;
        updateInDB();
    }

    public boolean hasConflict() {
        Connection conn = DBConnection.getConnection();
        try {
            String query = "SELECT COUNT(*) FROM Offerings WHERE location = ? AND date = ? AND (" +
                    "(startTime < ? AND endTime > ?) OR " +
                    "(startTime >= ? AND startTime < ?))";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, location.toString());
            pstmt.setString(2, date);
            pstmt.setString(3, endTime);
            pstmt.setString(4, startTime);
            pstmt.setString(5, startTime);
            pstmt.setString(6, endTime);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking for conflicting offerings.");
            e.printStackTrace();
        }
        return false;
    }

    public boolean saveToDB() {
        if (hasConflict()) {
            System.out.println("Cannot save offering: conflicting offering exists at the same location and time.");
            return false;
        }

        Connection conn = DBConnection.getConnection();

        try {
            String insertOffering = "INSERT INTO Offerings(location, startTime, endTime, isGroup, capacity, date, offeringName, instructorId, available, visible, enrolled) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(insertOffering, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, location.toString());
            pstmt.setString(2, startTime);
            pstmt.setString(3, endTime);
            pstmt.setInt(4, isGroup ? 1 : 0);
            pstmt.setInt(5, capacity);
            pstmt.setString(6, date);
            pstmt.setString(7, offeringName);
            pstmt.setString(8, instructorId);
            pstmt.setInt(9, available ? 1 : 0);
            pstmt.setInt(10, visible ? 1 : 0);
            pstmt.setInt(11, enrolled); // Save enrolled count
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                this.id = rs.getInt(1);
            }

            return true;
        } catch (SQLException e) {
            System.err.println("Error saving offering to database.");
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateInDB() {
        Connection conn = DBConnection.getConnection();

        try {
            String updateOffering = "UPDATE Offerings SET instructorId = ?, available = ?, visible = ?, capacity = ?, enrolled = ? WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(updateOffering);
            pstmt.setString(1, instructorId);
            pstmt.setInt(2, available ? 1 : 0);
            pstmt.setInt(3, visible ? 1 : 0);
            pstmt.setInt(4, capacity); // Update capacity
            pstmt.setInt(5, enrolled); // Update enrolled count
            pstmt.setInt(6, id);
            pstmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            System.err.println("Error updating offering in database.");
            e.printStackTrace();
            return false;
        }
    }

    public void incrementEnrolled() {
        this.enrolled++;
        if (this.enrolled >= this.capacity) {
            this.available = false;
        }
    }

    public void decrementEnrolled() {
        this.enrolled--;
        if (this.enrolled < this.capacity) {
            this.available = true;
        }
    }

    public static Offering getOfferingById(int id) {
        Connection conn = DBConnection.getConnection();

        try {
            String query = "SELECT * FROM Offerings WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Offering offering = new Offering();
                offering.id = id;
                offering.location = Location.fromString(rs.getString("location"));
                offering.startTime = rs.getString("startTime");
                offering.endTime = rs.getString("endTime");
                offering.isGroup = rs.getInt("isGroup") == 1;
                offering.capacity = rs.getInt("capacity");
                offering.enrolled = rs.getInt("enrolled"); // Retrieve enrolled count
                offering.date = rs.getString("date");
                offering.offeringName = rs.getString("offeringName");
                offering.instructorId = rs.getString("instructorId");
                offering.available = rs.getInt("available") == 1;
                offering.visible = rs.getInt("visible") == 1;

                if (offering.instructorId != null) {
                    offering.instructor = Instructor.getInstructorById(offering.instructorId);
                }

                return offering;
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving offering from database.");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        String groupType = isGroup ? "Group" : "Private";
        String availability = available ? "Available" : "Not Available";
        String instructorName = (instructor != null) ? instructor.getName() : "TBD";
        String cityName = location.getCity().getName();
        int spotsLeft = capacity - enrolled; // Calculate spots left
        return String.format("%s %s Class with %s on %s from %s to %s at %s in %s (%s) - Spots Available: %d",
                groupType, offeringName, instructorName, date, startTime, endTime,
                location.getOrganization(), cityName, availability, spotsLeft);
    }
}
