package com.project;

import java.util.Scanner;

public class Main {
    public static void menu() {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\nMain Menu:");
            System.out.println("0. Exit");
            System.out.println("1. Admin Login");
            System.out.println("2. Instructor Login");
            System.out.println("3. Instructor Registration");
            System.out.println("4. Client Login");
            System.out.println("5. Client Registration");
            System.out.println("6. View Offerings Schedule");

            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> Admin.adminLogin();
                case 2 -> Instructor.instructorLogin();
                case 3 -> Instructor.instructorRegistration();
                case 4 -> Client.clientLogin();
                case 5 -> Client.clientRegistration();
                case 6 -> Schedule.viewPublicOfferings();
                case 0 -> System.out.println("Exiting...");
                default -> System.out.println("Invalid choice.");
            }
        } while (choice != 0);

        scanner.close();
    }

    public static void main(String[] args) {
        DBInitializer.initialize();

       /*  Admin defaultAdmin = new Admin("Default Admin", "123", "123");
        if (!defaultAdmin.existsInDB()) {
            defaultAdmin.saveToDB();
        }*/

        menu();
    }
}
