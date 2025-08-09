package se16.algorithms;

import se16.model.*;
import se16.data.CampusGraph;
import java.util.*;

/**
 * Implementation of Dijkstra's algorithm for finding shortest paths
 */
public class DijkstraAlgorithm {
    private final CampusGraph graph;

    public DijkstraAlgorithm(CampusGraph graph) {
        this.graph = graph;
    }

    /**
     * Find shortest path between two locations
     */
    public Route findShortestPath(Location source, Location destination, boolean optimizeForTime) {
        Map<Location, Double> distances = new HashMap<>();
        Map<Location, Location> previous = new HashMap<>();
        PriorityQueue<LocationDistance> queue = new PriorityQueue<>();
        Set<Location> visited = new HashSet<>();

        // Initialize distances
        for (Location location : graph.getAllLocations()) {
            distances.put(location, Double.MAX_VALUE);
        }
        distances.put(source, 0.0);
        queue.offer(new LocationDistance(source, 0.0));

        while (!queue.isEmpty()) {
            LocationDistance current = queue.poll();
            Location currentLocation = current.location;

            if (visited.contains(currentLocation)) {
                continue;
            }
            visited.add(currentLocation);

            if (currentLocation.equals(destination)) {
                break;
            }

            for (Edge edge : graph.getEdges(currentLocation)) {
                Location neighbor = edge.getTo();
                if (visited.contains(neighbor)) {
                    continue;
                }

                double cost = edge.getCost(optimizeForTime);
                double newDistance = distances.get(currentLocation) + cost;

                if (newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    previous.put(neighbor, currentLocation);
                    queue.offer(new LocationDistance(neighbor, newDistance));
                }
            }
        }

        return reconstructPath(source, destination, previous, optimizeForTime);
    }

    /**
     * Find all shortest paths from source to all other locations
     */
    public Map<Location, Route> findAllShortestPaths(Location source, boolean optimizeForTime) {
        Map<Location, Double> distances = new HashMap<>();
        Map<Location, Location> previous = new HashMap<>();
        PriorityQueue<LocationDistance> queue = new PriorityQueue<>();
        Set<Location> visited = new HashSet<>();
        Map<Location, Route> routes = new HashMap<>();

        // Initialize distances
        for (Location location : graph.getAllLocations()) {
            distances.put(location, Double.MAX_VALUE);
        }
        distances.put(source, 0.0);
        queue.offer(new LocationDistance(source, 0.0));

        while (!queue.isEmpty()) {
            LocationDistance current = queue.poll();
            Location currentLocation = current.location;

            if (visited.contains(currentLocation)) {
                continue;
            }
            visited.add(currentLocation);

            for (Edge edge : graph.getEdges(currentLocation)) {
                Location neighbor = edge.getTo();
                if (visited.contains(neighbor)) {
                    continue;
                }

                double cost = edge.getCost(optimizeForTime);
                double newDistance = distances.get(currentLocation) + cost;

                if (newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    previous.put(neighbor, currentLocation);
                    queue.offer(new LocationDistance(neighbor, newDistance));
                }
            }
        }

        // Reconstruct all paths
        for (Location destination : graph.getAllLocations()) {
            if (!destination.equals(source) && previous.containsKey(destination)) {
                Route route = reconstructPath(source, destination, previous, optimizeForTime);
                if (route != null) {
                    routes.put(destination, route);
                }
            }
        }

        return routes;
    }

    private Route reconstructPath(Location source, Location destination, 
                                Map<Location, Location> previous, boolean optimizeForTime) {
        if (!previous.containsKey(destination)) {
            return null; // No path found
        }

        List<Location> waypoints = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();
        Location current = destination;

        // Build path backwards
        while (current != null) {
            waypoints.add(0, current);
            current = previous.get(current);
        }

        // Build edges list
        for (int i = 0; i < waypoints.size() - 1; i++) {
            Location from = waypoints.get(i);
            Location to = waypoints.get(i + 1);
            
            // Find the edge between these locations
            Edge edge = graph.getEdges(from).stream()
                    .filter(e -> e.getTo().equals(to))
                    .findFirst()
                    .orElse(null);
            
            if (edge != null) {
                edges.add(edge);
            }
        }

        String routeType = optimizeForTime ? "Fastest" : "Shortest";
        return new Route(waypoints, edges, routeType);
    }

    /**
     * Helper class for priority queue
     */
    private static class LocationDistance implements Comparable<LocationDistance> {
        final Location location;
        final double distance;

        LocationDistance(Location location, double distance) {
            this.location = location;
            this.distance = distance;
        }

        @Override
        public int compareTo(LocationDistance other) {
            return Double.compare(this.distance, other.distance);
        }
    }
}
