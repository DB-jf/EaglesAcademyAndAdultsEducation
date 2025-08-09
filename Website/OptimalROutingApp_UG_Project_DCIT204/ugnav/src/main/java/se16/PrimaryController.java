package se16;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import se16.model.*;
import se16.services.NavigationService;
import java.util.List;

public class PrimaryController {
    
    @FXML private ComboBox<Location> sourceComboBox;
    @FXML private ComboBox<Location> destinationComboBox;
    @FXML private ComboBox<LocationType> landmarkTypeComboBox;
    @FXML private RadioButton shortestRadio;
    @FXML private RadioButton fastestRadio;
    @FXML private RadioButton landmarkRadio;
    @FXML private Button findRoutesButton;
    @FXML private ListView<Route> routesListView;
    @FXML private TextArea routeDetailsArea;
    @FXML private TextField searchField;
    @FXML private ListView<Location> searchResultsList;
    @FXML private Label statusLabel;
    
    private NavigationService navigationService;
    private ToggleGroup routeTypeGroup;
    
    @FXML
    private void initialize() {
        navigationService = new NavigationService();
        setupUI();
        loadLocations();
    }
    
    private void setupUI() {
        // Setup toggle group for route types
        routeTypeGroup = new ToggleGroup();
        shortestRadio.setToggleGroup(routeTypeGroup);
        fastestRadio.setToggleGroup(routeTypeGroup);
        landmarkRadio.setToggleGroup(routeTypeGroup);
        shortestRadio.setSelected(true);
        
        // Setup location type combo box
        landmarkTypeComboBox.setItems(FXCollections.observableArrayList(LocationType.values()));
        landmarkTypeComboBox.setValue(LocationType.BANK);
        
        // Setup list view cell factories
        routesListView.setCellFactory(listView -> new RouteListCell());
        searchResultsList.setCellFactory(listView -> new LocationListCell());
        
        // Setup event handlers
        routesListView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> displayRouteDetails(newValue));
        
        // Enable/disable landmark combo box based on radio button selection
        landmarkRadio.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            landmarkTypeComboBox.setDisable(!isSelected);
        });
        
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.trim().isEmpty()) {
                performSearch(newValue.trim());
            } else {
                searchResultsList.getItems().clear();
            }
        });
        
        searchResultsList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Location selected = searchResultsList.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    if (sourceComboBox.getValue() == null) {
                        sourceComboBox.setValue(selected);
                    } else if (destinationComboBox.getValue() == null) {
                        destinationComboBox.setValue(selected);
                    }
                }
            }
        });
        
        // Initial status
        statusLabel.setText("Welcome to UG Navigate! Select source and destination to find routes.");
    }
    
    private void loadLocations() {
        ObservableList<Location> locations = FXCollections.observableArrayList(
            navigationService.getAllLocations());
        locations.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        
        sourceComboBox.setItems(locations);
        destinationComboBox.setItems(locations);
    }
    
    @FXML
    private void handleFindRoutes() {
        Location source = sourceComboBox.getValue();
        Location destination = destinationComboBox.getValue();
        
        if (source == null || destination == null) {
            showAlert("Please select both source and destination locations.");
            return;
        }
        
        if (source.equals(destination)) {
            showAlert("Source and destination cannot be the same.");
            return;
        }
        
        try {
            List<Route> routes;
            RadioButton selected = (RadioButton) routeTypeGroup.getSelectedToggle();
            
            if (selected == landmarkRadio) {
                LocationType landmarkType = landmarkTypeComboBox.getValue();
                if (landmarkType == null) {
                    showAlert("Please select a landmark type.");
                    return;
                }
                routes = navigationService.findLandmarkBasedRoutes(
                    source.getName(), destination.getName(), landmarkType, 3);
                statusLabel.setText(String.format("Found %d routes via %s", routes.size(), landmarkType.getDisplayName()));
            } else {
                boolean optimizeForTime = selected == fastestRadio;
                routes = navigationService.findRoutes(source, destination, optimizeForTime, 3);
                statusLabel.setText(String.format("Found %d %s routes", routes.size(), 
                    optimizeForTime ? "fastest" : "shortest"));
            }
            
            routesListView.setItems(FXCollections.observableArrayList(routes));
            
            if (routes.isEmpty()) {
                routeDetailsArea.setText("No routes found between the selected locations.");
            } else {
                routesListView.getSelectionModel().selectFirst();
            }
            
        } catch (Exception e) {
            showAlert("Error finding routes: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleAlgorithmDemo() {
        try {
            App.setRoot("secondary");
        } catch (Exception e) {
            showAlert("Error opening algorithm demo: " + e.getMessage());
        }
    }
    
    @FXML
    private void performSearch(String keyword) {
        try {
            List<Location> results = navigationService.searchLocations(keyword);
            searchResultsList.setItems(FXCollections.observableArrayList(results));
        } catch (Exception e) {
            showAlert("Error searching locations: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleClearSearch() {
        searchField.clear();
        searchResultsList.getItems().clear();
    }
    
    @FXML
    private void handleSwapLocations() {
        Location source = sourceComboBox.getValue();
        Location destination = destinationComboBox.getValue();
        
        sourceComboBox.setValue(destination);
        destinationComboBox.setValue(source);
    }
    
    private void displayRouteDetails(Route route) {
        if (route == null) {
            routeDetailsArea.clear();
            return;
        }
        
        StringBuilder details = new StringBuilder();
        details.append(route.getDescription()).append("\n");
        
        // Detailed turn-by-turn directions
        details.append("\n=== DIRECTIONS ===\n");
        List<Location> waypoints = route.getWaypoints();
        List<Edge> edges = route.getEdges();
        
        for (int i = 0; i < waypoints.size(); i++) {
            Location waypoint = waypoints.get(i);
            details.append(String.format("%d. %s", i + 1, waypoint.getName()));
            
            if (waypoint.getType() == LocationType.LANDMARK || 
                waypoint.getType() == LocationType.BANK ||
                waypoint.getType() == LocationType.LIBRARY) {
                details.append(" ★");
            }
            details.append("\n");
            
            if (i < edges.size()) {
                Edge edge = edges.get(i);
                details.append(String.format("   → Walk %.0fm via %s (%.1f min)\n", 
                    edge.getDistance(), edge.getPathType(), edge.getEffectiveTime()));
            }
        }
        
        // Route analysis
        NavigationService.RouteAnalysis analysis = navigationService.analyzeRoute(route);
        details.append("\n=== ANALYSIS ===\n");
        details.append(analysis.getSummary());
        
        routeDetailsArea.setText(details.toString());
    }
    
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("UG Navigate");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // Custom cell for route list
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
    
    // Custom cell for location list
    private static class LocationListCell extends ListCell<Location> {
        @Override
        protected void updateItem(Location location, boolean empty) {
            super.updateItem(location, empty);
            if (empty || location == null) {
                setText(null);
            } else {
                setText(String.format("%s (%s)", location.getName(), location.getType().getDisplayName()));
            }
        }
    }
}
