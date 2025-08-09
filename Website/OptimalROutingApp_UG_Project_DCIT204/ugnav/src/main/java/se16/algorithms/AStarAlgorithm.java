package se16.algorithms;

import se16.model.*;
import se16.data.CampusGraph;
import java.util.*;

/**
 * Implementation of A* (A-Star) algorithm for pathfinding with heuristic optimization.
 * 
 * <p>A* is an informed search algorithm that uses heuristics to find the optimal path
 * between two nodes in a weighted graph. It maintains two cost functions:
 * <ul>
 *   <li>g(n) - actual cost from start node to node n</li>
 *   <li>h(n) - heuristic estimate from node n to goal node</li>
 *   <li>f(n) = g(n) + h(n) - estimated total cost of path through n</li>
 * </ul>
 * 
 * <p>The algorithm is optimal when the heuristic function is admissible (never overestimates
 * the actual cost) and consistent. This implementation uses the Haversine distance formula
 * as the heuristic, which satisfies both conditions for geographic pathfinding.
 * 
 * <p>Time Complexity: O(b^d) where b is branching factor and d is depth
 * <p>Space Complexity: O(b^d) for storing nodes in open and closed sets
 * 
 * @author UG Navigate Team
 * @version 1.0
 * @since 2025
 */
public class AStarAlgorithm {
    
    /** The campus graph containing locations and edges for pathfinding */
    private final CampusGraph graph;

    /**
     * Constructs a new A* algorithm instance with the given campus graph.
     * 
     * @param graph the campus graph containing locations and connecting edges
     * @throws NullPointerException if graph is null
     */
    public AStarAlgorithm(CampusGraph graph) {
        if (graph == null) {
            throw new NullPointerException("Campus graph cannot be null");
        }
        this.graph = graph;
    }

    /**
     * Finds the optimal path between source and destination using A* algorithm.
     * 
     * <p>This method implements the core A* algorithm with the following steps:
     * <ol>
     *   <li>Initialize g-scores (actual costs) and f-scores (estimated total costs)</li>
     *   <li>Add source node to open set with f-score = heuristic(source, destination)</li>
     *   <li>While open set is not empty:
     *     <ul>
     *       <li>Select node with lowest f-score from open set</li>
     *       <li>If node is destination, reconstruct and return path</li>
     *       <li>Move node to closed set</li>
     *       <li>Examine all neighbors and update scores if better path found</li>
     *     </ul>
     *   </li>
     * </ol>
     * 
     * <p>The algorithm optimizes either for shortest distance or fastest time based on
     * the optimizeForTime parameter, which affects both the edge cost calculation and
     * the heuristic function used.
     * 
     * @param source the starting location for pathfinding
     * @param destination the target location to reach
     * @param optimizeForTime if true, optimizes for travel time; if false, optimizes for distance
     * @return the optimal route from source to destination, or null if no path exists
     * @throws IllegalArgumentException if source or destination is null or not in graph
     * 
     * @see #heuristic(Location, Location, boolean)
     * @see #reconstructPath(Location, Location, Map, boolean)
     */
    public Route findPath(Location source, Location destination, boolean optimizeForTime) {
        // Input validation
        if (source == null) {
            throw new IllegalArgumentException("Source location cannot be null");
        }
        if (destination == null) {
            throw new IllegalArgumentException("Destination location cannot be null");
        }
        
        if (source.equals(destination)) {
            // Create a single-location route for same source and destination
            List<Location> waypoints = Arrays.asList(source);
            return new Route(waypoints, new ArrayList<>(), "A* Same Location");
        }
        
        // Initialize data structures for A* algorithm
        Map<Location, Double> gScore = new HashMap<>(); // Cost from start to each node
        Map<Location, Double> fScore = new HashMap<>(); // Estimated total cost through each node
        Map<Location, Location> previous = new HashMap<>(); // Parent tracking for path reconstruction
        PriorityQueue<AStarNode> openSet = new PriorityQueue<>(); // Nodes to be evaluated
        Set<Location> closedSet = new HashSet<>(); // Nodes already evaluated

        // Initialize all locations with infinite cost (unreachable initially)
        for (Location location : graph.getAllLocations()) {
            gScore.put(location, Double.MAX_VALUE);
            fScore.put(location, Double.MAX_VALUE);
        }

        // Set initial costs for source node
        gScore.put(source, 0.0);
        fScore.put(source, heuristic(source, destination, optimizeForTime));
        openSet.offer(new AStarNode(source, fScore.get(source)));

        // Main A* algorithm loop
        while (!openSet.isEmpty()) {
            // Get node with lowest f-score from open set
            AStarNode currentNode = openSet.poll();
            Location current = currentNode.location;

            // Check if we've reached the destination
            if (current.equals(destination)) {
                return reconstructPath(source, destination, previous, optimizeForTime);
            }

            // Move current node from open set to closed set
            closedSet.add(current);

            // Examine all neighbors of current node
            for (Edge edge : graph.getEdges(current)) {
                Location neighbor = edge.getTo();
                
                // Skip nodes already evaluated
                if (closedSet.contains(neighbor)) {
                    continue;
                }

                // Calculate tentative g-score for this path to neighbor
                double tentativeGScore = gScore.get(current) + edge.getCost(optimizeForTime);

                // If this path to neighbor is better than any previous one
                if (tentativeGScore < gScore.get(neighbor)) {
                    // Update parent pointer and scores
                    previous.put(neighbor, current);
                    gScore.put(neighbor, tentativeGScore);
                    fScore.put(neighbor, tentativeGScore + heuristic(neighbor, destination, optimizeForTime));

                    // Add/update neighbor in open set
                    // Remove any existing entry for this neighbor and add updated one
                    openSet.removeIf(node -> node.location.equals(neighbor));
                    openSet.offer(new AStarNode(neighbor, fScore.get(neighbor)));
                }
            }
        }

        // No path found from source to destination
        return null;
    }

    /**
     * Calculates the heuristic estimate from one location to another.
     * 
     * <p>This method implements an admissible and consistent heuristic function using
     * the Haversine distance formula to calculate the straight-line distance between
     * two geographic points. The heuristic is:
     * <ul>
     *   <li><b>Admissible</b>: Never overestimates the actual cost to reach the goal</li>
     *   <li><b>Consistent</b>: Satisfies the triangle inequality h(n) ≤ c(n,n') + h(n')</li>
     * </ul>
     * 
     * <p>For time optimization, the distance is converted to estimated walking time
     * using an average walking speed of 5 km/h (83.33 meters per minute).
     * 
     * @param from the starting location
     * @param to the destination location
     * @param optimizeForTime if true, returns time estimate; if false, returns distance
     * @return the heuristic estimate (meters or minutes) from 'from' to 'to'
     * 
     * @see Location#distanceTo(Location)
     */
    private double heuristic(Location from, Location to, boolean optimizeForTime) {
        double distance = from.distanceTo(to);
        if (optimizeForTime) {
            // Convert distance to estimated time (average walking speed: 5 km/h = 83.33 m/min)
            return distance / 83.33; // meters per minute
        } else {
            return distance;
        }
    }

    /**
     * Finds multiple alternative paths using A* algorithm with node exclusion strategy.
     * 
     * <p>This method generates diverse route options by:
     * <ol>
     *   <li>Finding the optimal primary path using standard A*</li>
     *   <li>For each intermediate node in the primary path:
     *     <ul>
     *       <li>Temporarily exclude the node from the graph</li>
     *       <li>Find an alternative path avoiding the excluded node</li>
     *       <li>Add the alternative path if it's sufficiently different</li>
     *     </ul>
     *   </li>
     * </ol>
     * 
     * <p>Routes are considered similar if they share more than 70% of their waypoints.
     * This ensures the returned alternatives provide meaningful choice to users.
     * 
     * @param source the starting location for pathfinding
     * @param destination the target location to reach
     * @param optimizeForTime if true, optimizes for travel time; if false, optimizes for distance
     * @param maxPaths maximum number of alternative paths to return (including primary)
     * @return list of alternative routes, with the optimal route first
     * @throws IllegalArgumentException if maxPaths is less than 1
     * 
     * @see #findPathWithExclusions(Location, Location, boolean, Set)
     * @see #isRouteSimilar(Route, List)
     */
    public List<Route> findAlternativePaths(Location source, Location destination, 
                                          boolean optimizeForTime, int maxPaths) {
        if (maxPaths < 1) {
            throw new IllegalArgumentException("maxPaths must be at least 1");
        }
        
        List<Route> routes = new ArrayList<>();
        Set<Location> excludedNodes = new HashSet<>();

        // Find primary optimal path
        Route primaryRoute = findPath(source, destination, optimizeForTime);
        if (primaryRoute != null) {
            routes.add(primaryRoute);
        }

        // Generate alternative paths by excluding intermediate nodes from primary path
        if (primaryRoute != null && maxPaths > 1) {
            List<Location> primaryWaypoints = primaryRoute.getWaypoints();
            
            // Try excluding each intermediate waypoint (not source or destination)
            for (int i = 1; i < primaryWaypoints.size() - 1 && routes.size() < maxPaths; i++) {
                Location nodeToExclude = primaryWaypoints.get(i);
                excludedNodes.add(nodeToExclude);
                
                Route alternativeRoute = findPathWithExclusions(source, destination, 
                                                              optimizeForTime, excludedNodes);
                if (alternativeRoute != null && !isRouteSimilar(alternativeRoute, routes)) {
                    routes.add(alternativeRoute);
                }
                
                // Remove the excluded node to try next alternative
                excludedNodes.remove(nodeToExclude);
            }
        }

        return routes;
    }

    /**
     * Finds a path using A* algorithm while avoiding specified locations.
     * 
     * <p>This is a modified version of the standard A* algorithm that treats certain
     * nodes as obstacles. It's used to generate alternative routes by temporarily
     * removing intermediate waypoints from the primary path.
     * 
     * <p>The algorithm follows the same logic as {@link #findPath(Location, Location, boolean)}
     * but skips any nodes in the excluded set during neighbor exploration.
     * 
     * @param source the starting location for pathfinding
     * @param destination the target location to reach
     * @param optimizeForTime if true, optimizes for travel time; if false, optimizes for distance
     * @param excludedNodes set of locations to avoid during pathfinding
     * @return alternative route avoiding excluded nodes, or null if no path exists
     * 
     * @see #findPath(Location, Location, boolean)
     */
    private Route findPathWithExclusions(Location source, Location destination, 
                                       boolean optimizeForTime, Set<Location> excludedNodes) {
        // Initialize data structures (same as standard A*)
        Map<Location, Double> gScore = new HashMap<>();
        Map<Location, Double> fScore = new HashMap<>();
        Map<Location, Location> previous = new HashMap<>();
        PriorityQueue<AStarNode> openSet = new PriorityQueue<>();
        Set<Location> closedSet = new HashSet<>();

        // Initialize scores for all non-excluded locations
        for (Location location : graph.getAllLocations()) {
            if (!excludedNodes.contains(location)) {
                gScore.put(location, Double.MAX_VALUE);
                fScore.put(location, Double.MAX_VALUE);
            }
        }

        // Set initial costs for source node
        gScore.put(source, 0.0);
        fScore.put(source, heuristic(source, destination, optimizeForTime));
        openSet.offer(new AStarNode(source, fScore.get(source)));

        // Main algorithm loop with exclusion logic
        while (!openSet.isEmpty()) {
            AStarNode currentNode = openSet.poll();
            Location current = currentNode.location;

            if (current.equals(destination)) {
                return reconstructPath(source, destination, previous, optimizeForTime);
            }

            closedSet.add(current);

            for (Edge edge : graph.getEdges(current)) {
                Location neighbor = edge.getTo();
                
                // Skip if neighbor is in closed set or excluded
                if (closedSet.contains(neighbor) || excludedNodes.contains(neighbor)) {
                    continue;
                }

                double tentativeGScore = gScore.get(current) + edge.getCost(optimizeForTime);

                if (tentativeGScore < gScore.getOrDefault(neighbor, Double.MAX_VALUE)) {
                    previous.put(neighbor, current);
                    gScore.put(neighbor, tentativeGScore);
                    fScore.put(neighbor, tentativeGScore + heuristic(neighbor, destination, optimizeForTime));

                    openSet.removeIf(node -> node.location.equals(neighbor));
                    openSet.offer(new AStarNode(neighbor, fScore.get(neighbor)));
                }
            }
        }

        return null; // No path found avoiding excluded nodes
    }

    /**
     * Determines if a new route is similar to any existing routes.
     * 
     * <p>Routes are considered similar if they share more than 70% of their waypoints.
     * This similarity check helps ensure that alternative routes provide meaningful
     * diversity rather than minor variations of the same path.
     * 
     * <p>Similarity is calculated as:
     * <code>similarity = common_waypoints / max(route1_length, route2_length)</code>
     * 
     * @param newRoute the route to check for similarity
     * @param existingRoutes list of existing routes to compare against
     * @return true if the new route is similar to any existing route (>70% similarity)
     */
    private boolean isRouteSimilar(Route newRoute, List<Route> existingRoutes) {
        final double SIMILARITY_THRESHOLD = 0.7;
        
        for (Route existing : existingRoutes) {
            List<Location> newWaypoints = newRoute.getWaypoints();
            List<Location> existingWaypoints = existing.getWaypoints();
            
            // Count common waypoints between the two routes
            int commonWaypoints = 0;
            for (Location waypoint : newWaypoints) {
                if (existingWaypoints.contains(waypoint)) {
                    commonWaypoints++;
                }
            }
            
            // Calculate similarity as percentage of common waypoints
            double similarity = (double) commonWaypoints / Math.max(newWaypoints.size(), existingWaypoints.size());
            if (similarity > SIMILARITY_THRESHOLD) {
                return true; // Routes are too similar
            }
        }
        return false; // Route is sufficiently different
    }

    /**
     * Reconstructs the optimal path from source to destination using parent pointers.
     * 
     * <p>This method traces back from the destination to the source using the parent
     * pointers stored during the A* search. It then constructs a Route object containing:
     * <ul>
     *   <li>Ordered list of waypoints from source to destination</li>
     *   <li>List of edges connecting consecutive waypoints</li>
     *   <li>Route type indicating optimization method used</li>
     * </ul>
     * 
     * <p>The method also validates that each edge exists in the graph and handles
     * cases where edges might be missing due to graph inconsistencies.
     * 
     * @param source the starting location of the path
     * @param destination the ending location of the path
     * @param previous map of parent pointers from A* search
     * @param optimizeForTime indicates whether path was optimized for time or distance
     * @return complete Route object with waypoints and edges, or null if reconstruction fails
     * 
     * @see Route
     */
    private Route reconstructPath(Location source, Location destination, 
                                Map<Location, Location> previous, boolean optimizeForTime) {
        if (!previous.containsKey(destination)) {
            return null; // No path exists to destination
        }

        // Build waypoints list by tracing parent pointers backwards
        List<Location> waypoints = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();
        Location current = destination;

        // Trace back from destination to source
        while (current != null) {
            waypoints.add(0, current); // Add to front to maintain source->destination order
            current = previous.get(current);
        }

        // Build edges list by finding connections between consecutive waypoints
        for (int i = 0; i < waypoints.size() - 1; i++) {
            Location from = waypoints.get(i);
            Location to = waypoints.get(i + 1);
            
            // Find the edge connecting these two waypoints
            Edge edge = graph.getEdges(from).stream()
                    .filter(e -> e.getTo().equals(to))
                    .findFirst()
                    .orElse(null);
            
            if (edge != null) {
                edges.add(edge);
            } else {
                // Log warning about missing edge (shouldn't happen in consistent graph)
                System.err.println("Warning: Missing edge from " + from.getName() + " to " + to.getName());
            }
        }

        // Create route with appropriate type description
        String routeType = optimizeForTime ? "A* Fastest" : "A* Shortest";
        return new Route(waypoints, edges, routeType);
    }

    /**
     * Helper class representing a node in the A* algorithm's priority queue.
     * 
     * <p>Each node contains a location and its f-score (estimated total cost).
     * The priority queue uses the f-score to determine which node to examine next,
     * always selecting the node with the lowest estimated total cost.
     * 
     * <p>This class implements {@link Comparable} to enable automatic ordering
     * in the priority queue based on f-scores.
     * 
     * @see PriorityQueue
     * @see Comparable
     */
    private static class AStarNode implements Comparable<AStarNode> {
        /** The location this node represents */
        final Location location;
        
        /** The f-score (g-score + heuristic) for this node */
        final double fScore;

        /**
         * Creates a new A* node.
         * 
         * @param location the location this node represents
         * @param fScore the estimated total cost through this node
         */
        AStarNode(Location location, double fScore) {
            this.location = location;
            this.fScore = fScore;
        }

        /**
         * Compares this node with another based on f-scores.
         * 
         * <p>Nodes with lower f-scores are considered "smaller" and will be
         * processed first by the priority queue.
         * 
         * @param other the other A* node to compare with
         * @return negative if this node has lower f-score, positive if higher, zero if equal
         */
        @Override
        public int compareTo(AStarNode other) {
            return Double.compare(this.fScore, other.fScore);
        }
        
        /**
         * Returns a string representation of this node for debugging.
         * 
         * @return string containing location name and f-score
         */
        @Override
        public String toString() {
            return String.format("AStarNode{location=%s, fScore=%.2f}", 
                               location.getName(), fScore);
        }
    }
}
