package se16.model;

/**
 * Represents a location on the UG campus with coordinates and metadata
 */
public class Location {
    private final String id;
    private final String name;
    private final double latitude;
    private final double longitude;
    private final LocationType type;
    private final String description;

    public Location(String id, String name, double latitude, double longitude, 
                   LocationType type, String description) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
        this.description = description;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public LocationType getType() { return type; }
    public String getDescription() { return description; }

    /**
     * Calculate distance to another location using Haversine formula
     */
    public double distanceTo(Location other) {
        final double R = 6371000; // Earth's radius in meters
        double lat1Rad = Math.toRadians(this.latitude);
        double lat2Rad = Math.toRadians(other.latitude);
        double deltaLatRad = Math.toRadians(other.latitude - this.latitude);
        double deltaLonRad = Math.toRadians(other.longitude - this.longitude);

        double a = Math.sin(deltaLatRad/2) * Math.sin(deltaLatRad/2) +
                   Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                   Math.sin(deltaLonRad/2) * Math.sin(deltaLonRad/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return R * c; // Distance in meters
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Location location = (Location) obj;
        return id.equals(location.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
