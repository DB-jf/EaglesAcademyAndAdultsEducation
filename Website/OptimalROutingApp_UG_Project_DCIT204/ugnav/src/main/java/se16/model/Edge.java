package se16.model;

/**
 * Represents an edge/path between two locations on campus
 */
public class Edge {
    private final Location from;
    private final Location to;
    private final double distance;
    private final double walkingTime; // in minutes
    private final double trafficFactor; // 1.0 = normal, >1.0 = congested
    private final String pathType; // "walkway", "road", "stairs", etc.

    public Edge(Location from, Location to, double distance, double walkingTime, 
                double trafficFactor, String pathType) {
        this.from = from;
        this.to = to;
        this.distance = distance;
        this.walkingTime = walkingTime;
        this.trafficFactor = trafficFactor;
        this.pathType = pathType;
    }

    public Edge(Location from, Location to) {
        this.from = from;
        this.to = to;
        this.distance = from.distanceTo(to);
        this.walkingTime = calculateWalkingTime();
        this.trafficFactor = 1.0;
        this.pathType = "walkway";
    }

    private double calculateWalkingTime() {
        // Average walking speed: 5 km/h = 83.33 m/min
        return distance / 83.33;
    }

    // Getters
    public Location getFrom() { return from; }
    public Location getTo() { return to; }
    public double getDistance() { return distance; }
    public double getWalkingTime() { return walkingTime; }
    public double getTrafficFactor() { return trafficFactor; }
    public String getPathType() { return pathType; }

    /**
     * Get the effective travel time considering traffic
     */
    public double getEffectiveTime() {
        return walkingTime * trafficFactor;
    }

    /**
     * Get the cost for pathfinding algorithms (can be distance or time based)
     */
    public double getCost(boolean timeOptimized) {
        return timeOptimized ? getEffectiveTime() : distance;
    }

    @Override
    public String toString() {
        return String.format("%s -> %s (%.0fm, %.1fmin)", 
                           from.getName(), to.getName(), distance, getEffectiveTime());
    }
}
