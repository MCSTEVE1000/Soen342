package com.project;
import java.util.ArrayList;
import java.util.List;

public class OfferingCatalog {
    private static List<Offering> offeringCatalog = new ArrayList<>();

    public static void addOffering(Offering offering) {
        offeringCatalog.add(offering);
    }

    public static List<Offering> getOfferings() {
        return offeringCatalog;
    }
        public static List<Offering> getOfferings(Instructor instructor) {
        List<Offering> offerings = new ArrayList<>();
        for (Offering offering : OfferingCatalog.getOfferings()) {
            for (City city : instructor.availabilities.cities) {
                if ((offering.getLocation().getCity().getName()).equals(city.getName())) {
                    offerings.add(offering);
                }
            }
        }
        return offerings;
    }

    public static void removeOffering(Offering offering) {
        offeringCatalog.remove(offering);
    }

    public static void printCatalog() {
        for (Offering offering : offeringCatalog) {
            System.out.println(offering);
        }
    }
}   