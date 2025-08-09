package se16.services;

import se16.model.*;
import se16.data.CampusGraph;
import se16.algorithms.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Main routing service that provides comprehensive navigation functionality
 */
public class NavigationService {
    private final CampusGraph graph;
    private final DijkstraAlgorithm dijkstra;
    private final AStarAlgorithm aStar;

    public NavigationService() {
        this.graph = new CampusGraph();
        this.dijkstra = new DijkstraAlgorithm(graph);
        this.aStar = new AStarAlgorithm(graph);
    }

    /**
     * Find the best routes between two locations
     */
    public List<Route> findRoutes(String sourceName, String destinationName, 
                                boolean optimizeForTime, int maxRoutes) {
        Location source = findLocationByName(sourceName);
        Location destination = findLocationByName(destinationName);

        if (source == null || destination == null) {
            return new ArrayList<>();
        }

        return findRoutes(source, destination, optimizeForTime, maxRoutes);
    }

    /**
     * Find the best routes between two locations
     */
    public List<Route> findRoutes(Location source, Location destination, 
                                boolean optimizeForTime, int maxRoutes) {
        List<Route> routes = new ArrayList<>();

        // Get Dijkstra route
        Route dijkstraRoute = dijkstra.findShortestPath(source, destination, optimizeForTime);
        if (dijkstraRoute != null) {
            routes.add(dijkstraRoute);
        }

        // Get A* routes (including alternatives)
        List<Route> aStarRoutes = aStar.findAlternativePaths(source, destination, optimizeForTime, maxRoutes);
        for (Route route : aStarRoutes) {
            if (!isRouteSimilar(route, routes)) {
                routes.add(route);
            }
        }

        // Sort routes based on optimization preference
        if (optimizeForTime) {
            routes.sort(Comparator.comparingDouble(Route::getTotalTime));
        } else {
            routes.sort(Comparator.comparingDouble(Route::getTotalDistance));
        }

        return routes.stream().limit(maxRoutes).collect(Collectors.toList());
    }

    /**
     * Find routes that pass through specific landmarks
     */
    public List<Route> findLandmarkBasedRoutes(String sourceName, String destinationName, 
                                             LocationType landmarkType, int maxRoutes) {
        Location source = findLocationByName(sourceName);
        Location destination = findLocationByName(destinationName);

        if (source == null || destination == null) {
            return new ArrayList<>();
        }

        List<Location> landmarks = graph.getLocationsByType(landmarkType);
        List<Route> landmarkRoutes = new ArrayList<>();

        for (Location landmark : landmarks) {
            // Find route from source to landmark
            Route toLandmark = dijkstra.findShortestPath(source, landmark, false);
            // Find route from landmark to destination
            Route fromLandmark = dijkstra.findShortestPath(landmark, destination, false);

            if (toLandmark != null && fromLandmark != null) {
                // Combine routes
                Route combinedRoute = combineRoutes(toLandmark, fromLandmark, landmarkType.getDisplayName());
                if (combinedRoute != null) {
                    landmarkRoutes.add(combinedRoute);
                }
            }
        }

        // Sort by total distance and limit results
        landmarkRoutes.sort(Comparator.comparingDouble(Route::getTotalDistance));
        return landmarkRoutes.stream().limit(maxRoutes).collect(Collectors.toList());
    }

    /**
     * Search for locations by keyword
     */
    public List<Location> searchLocations(String keyword) {
        return graph.searchLocationsByKeyword(keyword);
    }

    /**
     * Get locations by type
     */
    public List<Location> getLocationsByType(LocationType type) {
        return graph.getLocationsByType(type);
    }

    /**
     * Get the campus graph for direct algorithm access
     */
    public CampusGraph getCampusGraph() {
        return graph;
    }

    /**
     * Get all locations
     */
    public Collection<Location> getAllLocations() {
        return graph.getAllLocations();
    }

    /**
     * Find location by exact name
     */
    public Location findLocationByName(String name) {
        return graph.getLocationByName(name);
    }

    /**
     * Get route recommendations based on time of day and user preferences
     */
    public List<Route> getRecommendedRoutes(Location source, Location destination, 
                                          String timeOfDay, String userPreference) {
        boolean optimizeForTime = "fastest".equalsIgnoreCase(userPreference);
        
        // Adjust for time of day (morning rush, lunch time, evening)
        List<Route> routes = findRoutes(source, destination, optimizeForTime, 3);
        
        // Apply time-based adjustments
        for (Route route : routes) {
            adjustRouteForTimeOfDay(route, timeOfDay);
        }
        
        return routes;
    }

    /**
     * Get route statistics and analysis
     */
    public RouteAnalysis analyzeRoute(Route route) {
        List<Location> landmarks = route.getLandmarks();
        Map<LocationType, Integer> typeCount = new HashMap<>();
        
        for (Location waypoint : route.getWaypoints()) {
            LocationType type = waypoint.getType();
            typeCount.put(type, typeCount.getOrDefault(type, 0) + 1);
        }
        
        return new RouteAnalysis(route, landmarks, typeCount);
    }

    // Helper methods
    private boolean isRouteSimilar(Route newRoute, List<Route> existingRoutes) {
        for (Route existing : existingRoutes) {
            List<Location> newWaypoints = newRoute.getWaypoints();
            List<Location> existingWaypoints = existing.getWaypoints();
            
            int commonWaypoints = 0;
            for (Location waypoint : newWaypoints) {
                if (existingWaypoints.contains(waypoint)) {
                    commonWaypoints++;
                }
            }
            
            double similarity = (double) commonWaypoints / Math.max(newWaypoints.size(), existingWaypoints.size());
            if (similarity > 0.6) {
                return true;
            }
        }
        return false;
    }

    private Route combineRoutes(Route route1, Route route2, String routeType) {
        List<Location> combinedWaypoints = new ArrayList<>(route1.getWaypoints());
        List<Edge> combinedEdges = new ArrayList<>(route1.getEdges());
        
        // Remove duplicate waypoint (the landmark)
        List<Location> route2Waypoints = route2.getWaypoints();
        if (!route2Waypoints.isEmpty()) {
            combinedWaypoints.addAll(route2Waypoints.subList(1, route2Waypoints.size()));
        }
        combinedEdges.addAll(route2.getEdges());
        
        return new Route(combinedWaypoints, combinedEdges, "Via " + routeType);
    }

    private void adjustRouteForTimeOfDay(Route route, String timeOfDay) {
        // This would adjust traffic factors based on time of day
        // Implementation would modify edge costs based on historical data
        // For now, this is a placeholder for future enhancement
    }

    /**
     * Route analysis result class
     */
    public static class RouteAnalysis {
        private final Route route;
        private final List<Location> landmarks;
        private final Map<LocationType, Integer> locationTypeCount;

        public RouteAnalysis(Route route, List<Location> landmarks, Map<LocationType, Integer> locationTypeCount) {
            this.route = route;
            this.landmarks = landmarks;
            this.locationTypeCount = locationTypeCount;
        }

        public Route getRoute() { return route; }
        public List<Location> getLandmarks() { return landmarks; }
        public Map<LocationType, Integer> getLocationTypeCount() { return locationTypeCount; }
        
        public String getSummary() {
            StringBuilder summary = new StringBuilder();
            summary.append("Route Analysis:\n");
            summary.append(String.format("Total Distance: %.0f meters\n", route.getTotalDistance()));
            summary.append(String.format("Estimated Time: %.1f minutes\n", route.getTotalTime()));
            summary.append(String.format("Waypoints: %d\n", route.getWaypoints().size()));
            summary.append(String.format("Landmarks: %d\n", landmarks.size()));
            
            if (!landmarks.isEmpty()) {
                summary.append("Notable Landmarks: ");
                summary.append(landmarks.stream()
                        .map(Location::getName)
                        .collect(Collectors.joining(", ")));
            }
            
            return summary.toString();
        }
    }
}
