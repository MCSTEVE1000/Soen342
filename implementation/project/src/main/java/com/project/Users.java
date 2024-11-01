package com.project;

import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class Users {
    protected String name;
    protected String uniqueId;
    protected String phoneNumber;
    protected String password;
    protected String userType;

    public Users(String name, String phoneNumber, String password, String userType) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.password = hashPassword(password);
        this.userType = userType;
        this.uniqueId = generateUniqueId();
    }

    private String generateUniqueId() {
        return java.util.UUID.randomUUID().toString();
    }

    private String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    public boolean checkPassword(String plainPassword) {
        return BCrypt.checkpw(plainPassword, this.password);
    }

    public String getName() { return name; }
    public String getUniqueId() { return uniqueId; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = hashPassword(password); }

    public abstract boolean login(String identifier, String password);
    public abstract boolean register();
    public abstract boolean saveToDB();
    public abstract boolean deleteFromDB();

    public static Users findUserByPhoneNumber(String phoneNumber) {
        Connection conn = DBConnection.getConnection();

        try {
            String query = "SELECT * FROM Users WHERE phoneNumber = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, phoneNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String userType = rs.getString("userType");
                String uniqueId = rs.getString("uniqueId");
                String name = rs.getString("name");
                String password = rs.getString("password");

                switch (userType) {
                    case "Admin":
                        Admin admin = new Admin(name, phoneNumber, null);
                        admin.uniqueId = uniqueId;
                        admin.password = password;
                        return admin;
                    case "Client":
                        return Client.findClientByPhoneNumber(phoneNumber);
                    case "Instructor":
                        return Instructor.findInstructorByPhoneNumber(phoneNumber);
                    default:
                        return null;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by phone number.");
            e.printStackTrace();
        }
        return null;
    }

    public boolean existsInDB() {
        Connection conn = DBConnection.getConnection();

        try {
            String query = "SELECT * FROM Users WHERE uniqueId = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, this.uniqueId);
            ResultSet rs = pstmt.executeQuery();

            return rs.next();
        } catch (SQLException e) {
            System.err.println("Error checking user existence in database.");
            e.printStackTrace();
            return false;
        }
    }
}
