package se16.model;

/**
 * Enum representing different types of locations on UG campus
 */
public enum LocationType {
    ACADEMIC("Academic Building"),
    ADMINISTRATIVE("Administrative Building"),
    RESIDENTIAL("Residential Hall"),
    RECREATIONAL("Recreational Facility"),
    DINING("Dining Facility"),
    LIBRARY("Library"),
    MEDICAL("Medical Facility"),
    TRANSPORT("Transport Hub"),
    BANK("Bank/ATM"),
    LANDMARK("Notable Landmark"),
    PARKING("Parking Area"),
    ENTRANCE("Campus Entrance"),
    OTHER("Other");

    private final String displayName;

    LocationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
