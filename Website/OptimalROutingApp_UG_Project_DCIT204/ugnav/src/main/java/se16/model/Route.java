package se16.model;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents a route from source to destination with multiple waypoints
 */
public class Route implements Comparable<Route> {
    private final List<Location> waypoints;
    private final List<Edge> edges;
    private final double totalDistance;
    private final double totalTime;
    private final String routeType; // "shortest", "fastest", "landmark-based"

    public Route(List<Location> waypoints, List<Edge> edges, String routeType) {
        this.waypoints = new ArrayList<>(waypoints);
        this.edges = new ArrayList<>(edges);
        this.routeType = routeType;
        this.totalDistance = calculateTotalDistance();
        this.totalTime = calculateTotalTime();
    }

    private double calculateTotalDistance() {
        return edges.stream().mapToDouble(Edge::getDistance).sum();
    }

    private double calculateTotalTime() {
        return edges.stream().mapToDouble(Edge::getEffectiveTime).sum();
    }

    // Getters
    public List<Location> getWaypoints() { return new ArrayList<>(waypoints); }
    public List<Edge> getEdges() { return new ArrayList<>(edges); }
    public double getTotalDistance() { return totalDistance; }
    public double getTotalTime() { return totalTime; }
    public String getRouteType() { return routeType; }

    public Location getSource() {
        return waypoints.isEmpty() ? null : waypoints.get(0);
    }

    public Location getDestination() {
        return waypoints.isEmpty() ? null : waypoints.get(waypoints.size() - 1);
    }

    /**
     * Get landmarks along this route
     */
    public List<Location> getLandmarks() {
        List<Location> landmarks = new ArrayList<>();
        for (Location location : waypoints) {
            if (location.getType() == LocationType.LANDMARK || 
                location.getType() == LocationType.BANK ||
                location.getType() == LocationType.LIBRARY) {
                landmarks.add(location);
            }
        }
        return landmarks;
    }

    /**
     * Check if route passes through a specific landmark type
     */
    public boolean passesThrough(LocationType type) {
        return waypoints.stream().anyMatch(loc -> loc.getType() == type);
    }

    /**
     * Get formatted route description
     */
    public String getDescription() {
        StringBuilder desc = new StringBuilder();
        desc.append(String.format("Route: %s to %s\n", 
                   getSource().getName(), getDestination().getName()));
        desc.append(String.format("Type: %s\n", routeType));
        desc.append(String.format("Distance: %.0f meters\n", totalDistance));
        desc.append(String.format("Estimated Time: %.1f minutes\n", totalTime));
        
        List<Location> landmarks = getLandmarks();
        if (!landmarks.isEmpty()) {
            desc.append("Landmarks: ");
            for (int i = 0; i < landmarks.size(); i++) {
                desc.append(landmarks.get(i).getName());
                if (i < landmarks.size() - 1) desc.append(", ");
            }
            desc.append("\n");
        }
        
        return desc.toString();
    }

    @Override
    public int compareTo(Route other) {
        // Default comparison by total distance
        return Double.compare(this.totalDistance, other.totalDistance);
    }

    @Override
    public String toString() {
        return String.format("%s Route: %.0fm, %.1fmin", 
                           routeType, totalDistance, totalTime);
    }
}
