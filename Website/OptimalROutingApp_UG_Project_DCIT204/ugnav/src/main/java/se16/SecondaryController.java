package se16;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import se16.model.*;
import se16.services.NavigationService;
import se16.algorithms.*;
import java.util.*;

public class SecondaryController {

    @FXML private ListView<Route> algorithmResultsList;
    @FXML private TextArea algorithmDetailsArea;
    @FXML private ComboBox<String> algorithmComboBox;
    @FXML private ComboBox<Location> demoSourceCombo;
    @FXML private ComboBox<Location> demoDestCombo;
    @FXML private Button runAlgorithmButton;
    @FXML private Label performanceLabel;

    private NavigationService navigationService;

    @FXML
    private void initialize() {
        navigationService = new NavigationService();
        setupDemoUI();
    }

    private void setupDemoUI() {
        // Setup algorithm selection
        algorithmComboBox.setItems(FXCollections.observableArrayList(
            "Dijkstra's Algorithm - Shortest Path",
            "A* Algorithm - Heuristic Search", 
            "Route Sorting - Quick Sort",
            "Route Sorting - Merge Sort",
            "Vogel's Approximation Method",
            "Critical Path Analysis"
        ));
        algorithmComboBox.setValue("Dijkstra's Algorithm - Shortest Path");

        // Load demo locations
        List<Location> locations = new ArrayList<>(navigationService.getAllLocations());
        locations.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        
        demoSourceCombo.setItems(FXCollections.observableArrayList(locations));
        demoDestCombo.setItems(FXCollections.observableArrayList(locations));
        
        // Set default demo locations
        Location mainGate = navigationService.findLocationByName("Main Gate");
        Location library = navigationService.findLocationByName("Balme Library");
        
        if (mainGate != null) demoSourceCombo.setValue(mainGate);
        if (library != null) demoDestCombo.setValue(library);

        // Setup list view
        algorithmResultsList.setCellFactory(listView -> new RouteListCell());
        algorithmResultsList.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> displayAlgorithmDetails(newValue));
    }

    @FXML
    private void runAlgorithmDemo() {
        String selectedAlgorithm = algorithmComboBox.getValue();
        Location source = demoSourceCombo.getValue();
        Location destination = demoDestCombo.getValue();

        if (selectedAlgorithm == null || source == null || destination == null) {
            showAlert("Please select algorithm, source, and destination.");
            return;
        }

        long startTime = System.nanoTime();
        List<Route> results = new ArrayList<>();
        StringBuilder details = new StringBuilder();

        try {
            switch (selectedAlgorithm) {
                case "Dijkstra's Algorithm - Shortest Path":
                    results = runDijkstraDemo(source, destination, details);
                    break;
                case "A* Algorithm - Heuristic Search":
                    results = runAStarDemo(source, destination, details);
                    break;
                case "Route Sorting - Quick Sort":
                    results = runSortingDemo(source, destination, RouteSorter.SortAlgorithm.QUICK_SORT, details);
                    break;
                case "Route Sorting - Merge Sort":
                    results = runSortingDemo(source, destination, RouteSorter.SortAlgorithm.MERGE_SORT, details);
                    break;
                case "Vogel's Approximation Method":
                    runVogelDemo(details);
                    break;
                case "Critical Path Analysis":
                    runCriticalPathDemo(source, destination, details);
                    break;
            }

            long endTime = System.nanoTime();
            double executionTime = (endTime - startTime) / 1_000_000.0; // Convert to milliseconds

            algorithmResultsList.setItems(FXCollections.observableArrayList(results));
            performanceLabel.setText(String.format("Execution Time: %.2f ms", executionTime));

            if (!results.isEmpty()) {
                algorithmResultsList.getSelectionModel().selectFirst();
            }

        } catch (Exception e) {
            showAlert("Error running algorithm: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<Route> runDijkstraDemo(Location source, Location destination, StringBuilder details) {
        details.append("=== DIJKSTRA'S ALGORITHM DEMONSTRATION ===\n\n");
        details.append("Algorithm: Single-source shortest path algorithm\n");
        details.append("Time Complexity: O((V + E) log V)\n");
        details.append("Space Complexity: O(V)\n\n");

        details.append("Step-by-step process:\n");
        details.append("1. Initialize distances to all nodes as infinity\n");
        details.append("2. Set distance to source as 0\n");
        details.append("3. Use priority queue to process nodes in order of distance\n");
        details.append("4. Update distances to neighboring nodes\n");
        details.append("5. Repeat until destination is reached\n\n");

        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(navigationService.getCampusGraph());
        
        // Find shortest distance route
        Route shortestRoute = dijkstra.findShortestPath(source, destination, false);
        List<Route> results = new ArrayList<>();
        
        if (shortestRoute != null) {
            results.add(shortestRoute);
            details.append("RESULTS:\n");
            details.append(String.format("Shortest Distance: %.0f meters\n", shortestRoute.getTotalDistance()));
            details.append(String.format("Estimated Time: %.1f minutes\n", shortestRoute.getTotalTime()));
            details.append(String.format("Number of waypoints: %d\n", shortestRoute.getWaypoints().size()));
        }

        // Find fastest time route
        Route fastestRoute = dijkstra.findShortestPath(source, destination, true);
        if (fastestRoute != null && !results.contains(fastestRoute)) {
            results.add(fastestRoute);
            details.append(String.format("Fastest Route Time: %.1f minutes\n", fastestRoute.getTotalTime()));
        }

        return results;
    }

    private List<Route> runAStarDemo(Location source, Location destination, StringBuilder details) {
        details.append("=== A* ALGORITHM DEMONSTRATION ===\n\n");
        details.append("Algorithm: Best-first search with heuristic function\n");
        details.append("Time Complexity: O(b^d) where b is branching factor, d is depth\n");
        details.append("Space Complexity: O(b^d)\n\n");

        details.append("Key features:\n");
        details.append("1. Uses f(n) = g(n) + h(n) evaluation function\n");
        details.append("2. g(n) = actual cost from start to node n\n");
        details.append("3. h(n) = heuristic estimate from n to goal\n");
        details.append("4. Guarantees optimal solution if heuristic is admissible\n\n");

        AStarAlgorithm aStar = new AStarAlgorithm(navigationService.getCampusGraph());
        
        // Find multiple alternative paths
        List<Route> results = aStar.findAlternativePaths(source, destination, false, 3);
        
        details.append("RESULTS:\n");
        details.append(String.format("Found %d alternative routes\n", results.size()));
        
        for (int i = 0; i < results.size(); i++) {
            Route route = results.get(i);
            details.append(String.format("Route %d: %.0fm, %.1fmin\n", 
                i + 1, route.getTotalDistance(), route.getTotalTime()));
        }

        return results;
    }

    private List<Route> runSortingDemo(Location source, Location destination, 
                                     RouteSorter.SortAlgorithm algorithm, StringBuilder details) {
        details.append(String.format("=== %s DEMONSTRATION ===\n\n", algorithm.name().replace("_", " ")));
        
        // Generate multiple routes for sorting
        List<Route> allRoutes = navigationService.findRoutes(source, destination, false, 5);
        
        // Add some landmark-based routes
        for (LocationType type : Arrays.asList(LocationType.BANK, LocationType.LIBRARY, LocationType.DINING)) {
            List<Route> landmarkRoutes = navigationService.findLandmarkBasedRoutes(
                source.getName(), destination.getName(), type, 2);
            allRoutes.addAll(landmarkRoutes);
        }

        if (allRoutes.isEmpty()) {
            details.append("No routes found for sorting demonstration.\n");
            return new ArrayList<>();
        }

        details.append(String.format("Sorting %d routes by different criteria\n\n", allRoutes.size()));

        // Sort by distance
        List<Route> sortedByDistance = RouteSorter.sortByCriteria(allRoutes, 
            RouteSorter.SortCriteria.DISTANCE, algorithm);
        
        details.append("Sorted by Distance:\n");
        for (int i = 0; i < Math.min(3, sortedByDistance.size()); i++) {
            Route route = sortedByDistance.get(i);
            details.append(String.format("%d. %s - %.0fm\n", i + 1, route.getRouteType(), route.getTotalDistance()));
        }

        details.append("\nSorted by Time:\n");
        List<Route> sortedByTime = RouteSorter.sortByCriteria(allRoutes, 
            RouteSorter.SortCriteria.TIME, algorithm);
        
        for (int i = 0; i < Math.min(3, sortedByTime.size()); i++) {
            Route route = sortedByTime.get(i);
            details.append(String.format("%d. %s - %.1fmin\n", i + 1, route.getRouteType(), route.getTotalTime()));
        }

        return sortedByDistance;
    }

    private void runVogelDemo(StringBuilder details) {
        details.append("=== VOGEL'S APPROXIMATION METHOD DEMONSTRATION ===\n\n");
        details.append("Algorithm: Transportation problem optimization\n");
        details.append("Application: Optimal resource allocation on campus\n\n");

        details.append("Scenario: Optimize shuttle service allocation\n");
        details.append("Sources: Transport hubs with shuttle capacity\n");
        details.append("Destinations: High-demand locations\n\n");

        // Create a sample transportation problem
        List<Location> sources = Arrays.asList(
            navigationService.findLocationByName("Shuttle Station"),
            navigationService.findLocationByName("Main Gate"),
            navigationService.findLocationByName("Car Park A")
        );

        List<Location> destinations = Arrays.asList(
            navigationService.findLocationByName("Balme Library"),
            navigationService.findLocationByName("Night Market"), 
            navigationService.findLocationByName("Medical School")
        );

        // Remove nulls
        sources = sources.stream().filter(Objects::nonNull).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        destinations = destinations.stream().filter(Objects::nonNull).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        if (sources.size() >= 2 && destinations.size() >= 2) {
            double[] supply = {100, 80, 60}; // Shuttle capacity
            double[] demand = {70, 90, 80}; // Passenger demand

            VogelApproximationMethod.TransportationProblem problem = 
                VogelApproximationMethod.createCampusTransportationProblem(
                    sources, destinations, supply, demand);

            List<VogelApproximationMethod.Allocation> allocations = 
                VogelApproximationMethod.solve(problem);

            details.append("OPTIMAL ALLOCATION:\n");
            double totalCost = 0;
            for (VogelApproximationMethod.Allocation allocation : allocations) {
                details.append(String.format("%s → %s: %.0f passengers (Cost: %.0f)\n",
                    allocation.getSource().getName(),
                    allocation.getDestination().getName(),
                    allocation.getQuantity(),
                    allocation.getTotalCost()));
                totalCost += allocation.getTotalCost();
            }
            details.append(String.format("\nTotal Transportation Cost: %.0f\n", totalCost));
        } else {
            details.append("Insufficient locations for transportation problem demonstration.\n");
        }
    }

    private void runCriticalPathDemo(Location source, Location destination, StringBuilder details) {
        details.append("=== CRITICAL PATH METHOD DEMONSTRATION ===\n\n");
        details.append("Algorithm: Project scheduling and path analysis\n");
        details.append("Application: Finding critical paths in campus navigation\n\n");

        List<Route> routes = navigationService.findRoutes(source, destination, false, 3);
        
        if (!routes.isEmpty()) {
            details.append("CRITICAL PATH ANALYSIS:\n");
            
            Route longestRoute = routes.stream()
                .max(Comparator.comparingDouble(Route::getTotalTime))
                .orElse(routes.get(0));
            
            details.append(String.format("Critical Path (Longest): %s\n", longestRoute.getRouteType()));
            details.append(String.format("Duration: %.1f minutes\n", longestRoute.getTotalTime()));
            details.append(String.format("Distance: %.0f meters\n\n", longestRoute.getTotalDistance()));
            
            details.append("Path Activities (Edges):\n");
            List<Edge> edges = longestRoute.getEdges();
            double cumulativeTime = 0;
            
            for (int i = 0; i < edges.size(); i++) {
                Edge edge = edges.get(i);
                details.append(String.format("Activity %d: %s → %s\n", i + 1,
                    edge.getFrom().getName(), edge.getTo().getName()));
                details.append(String.format("  Duration: %.1f min, Start: %.1f min\n",
                    edge.getEffectiveTime(), cumulativeTime));
                cumulativeTime += edge.getEffectiveTime();
            }
            
            details.append(String.format("\nTotal Project Duration: %.1f minutes\n", cumulativeTime));
        } else {
            details.append("No routes found for critical path analysis.\n");
        }
    }

    private void displayAlgorithmDetails(Route route) {
        if (route == null) {
            return;
        }
        
        StringBuilder details = new StringBuilder(algorithmDetailsArea.getText());
        details.append("\n\n=== SELECTED ROUTE DETAILS ===\n");
        details.append(route.getDescription());
        
        algorithmDetailsArea.setText(details.toString());
    }

    @FXML
    private void switchToPrimary() throws Exception {
        App.setRoot("primary");
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Algorithm Demo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static class RouteListCell extends ListCell<Route> {
        @Override
        protected void updateItem(Route route, boolean empty) {
            super.updateItem(route, empty);
            if (empty || route == null) {
                setText(null);
            } else {
                setText(String.format("%s: %.0fm, %.1fmin", 
                    route.getRouteType(), route.getTotalDistance(), route.getTotalTime()));
            }
        }
    }
}