# UG Navigate: Implementation Guide
## Optimal Routing Solution for University of Ghana Campus

---

## Table of Contents
1. [Project Overview](#project-overview)
2. [Architecture & Design](#architecture--design)
3. [Data Structures](#data-structures)
4. [Algorithms Implementation](#algorithms-implementation)
5. [Core Components](#core-components)
6. [User Interface](#user-interface)
7. [Code Logic Flow](#code-logic-flow)
8. [Performance Analysis](#performance-analysis)
9. [Future Enhancements](#future-enhancements)

---

## Project Overview

UG Navigate is a comprehensive campus navigation system designed specifically for the University of Ghana. The application implements multiple pathfinding algorithms and optimization techniques to provide students, staff, and visitors with optimal routes between campus locations.

### Key Features
- **Multi-algorithm pathfinding**: Dijkstra's Algorithm, A* Search, Haversine distance calculation
- **Landmark-based routing**: Find routes via specific types of locations (banks, libraries, etc.)
- **Real-time route analysis**: Distance, time, and efficiency calculations
- **Interactive search**: Location discovery and filtering
- **Educational algorithm demonstration**: Visual representation of different algorithms

---

## Architecture & Design

### Design Pattern: Model-View-Controller (MVC)
The application follows the MVC architectural pattern:

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│     VIEW        │    │   CONTROLLER    │    │     MODEL       │
│  (FXML Files)   │◄──►│ (Controllers)   │◄──►│  (Data Classes) │
│                 │    │                 │    │                 │
│ - primary.fxml  │    │ - PrimaryCtrl   │    │ - Location      │
│ - secondary.fxml│    │ - SecondaryCtrl │    │ - Route         │
└─────────────────┘    └─────────────────┘    │ - Edge          │
                                              │ - CampusGraph   │
                                              └─────────────────┘
```

### Package Structure
```
se16/
├── App.java                    # Main application entry point
├── PrimaryController.java      # Main navigation interface controller
├── SecondaryController.java    # Algorithm demonstration controller
├── algorithms/                 # Pathfinding and optimization algorithms
│   ├── AStarAlgorithm.java
│   ├── DijkstraAlgorithm.java
│   ├── RouteSorter.java
│   └── VogelApproximationMethod.java
├── data/
│   └── CampusGraph.java        # Campus data and graph structure
├── model/                      # Data models
│   ├── Location.java
│   ├── LocationType.java
│   ├── Edge.java
│   └── Route.java
└── services/
    └── NavigationService.java  # Core navigation logic
```

---

## Data Structures

### 1. Graph Representation
**Choice**: Adjacency List using HashMap
```java
Map<String, List<Edge>> adjacencyList = new HashMap<>();
```

**Rationale**:
- **Space Efficient**: O(V + E) space complexity
- **Fast Edge Access**: O(1) average time to access edges from a vertex
- **Dynamic**: Easy to add/remove vertices and edges
- **Campus Context**: Perfect for sparse graphs like campus layouts

### 2. Priority Queue (Heap)
**Usage**: Dijkstra's and A* algorithms
```java
PriorityQueue<LocationDistance> priorityQueue = new PriorityQueue<>();
```

**Rationale**:
- **Efficient Priority Operations**: O(log n) insertion and extraction
- **Optimal Path Finding**: Always processes the most promising node first
- **Memory Efficient**: No need to store all possible paths

### 3. Hash-based Collections
**HashMap for Distance Tracking**:
```java
Map<String, Double> distances = new HashMap<>();
Map<String, String> previousNodes = new HashMap<>();
```

**HashSet for Visited Tracking**:
```java
Set<String> visited = new HashSet<>();
```

**Benefits**:
- **O(1) Average Access Time**: Fast lookups and updates
- **Collision Handling**: Built-in collision resolution
- **Memory Efficiency**: Only stores necessary data

### 4. List Structures
**ArrayList for Routes and Paths**:
```java
List<Location> waypoints = new ArrayList<>();
List<Edge> edges = new ArrayList<>();
```

**Rationale**:
- **Dynamic Sizing**: Adjusts to route length
- **Index Access**: O(1) access for route reconstruction
- **Iteration Efficiency**: Fast sequential access for display

---

## Algorithms Implementation

### 1. Dijkstra's Algorithm
**Purpose**: Find shortest path between two points
**Time Complexity**: O((V + E) log V)
**Space Complexity**: O(V)

```java
public List<String> findShortestPath(String start, String end) {
    PriorityQueue<LocationDistance> pq = new PriorityQueue<>();
    Map<String, Double> distances = new HashMap<>();
    Map<String, String> previous = new HashMap<>();
    
    // Initialize distances
    for (String location : adjacencyList.keySet()) {
        distances.put(location, Double.POSITIVE_INFINITY);
    }
    distances.put(start, 0.0);
    pq.offer(new LocationDistance(start, 0.0));
    
    while (!pq.isEmpty()) {
        LocationDistance current = pq.poll();
        
        if (current.location.equals(end)) break;
        if (current.distance > distances.get(current.location)) continue;
        
        // Process neighbors
        for (Edge edge : getEdges(current.location)) {
            double newDistance = current.distance + edge.getDistance();
            if (newDistance < distances.get(edge.getTo())) {
                distances.put(edge.getTo(), newDistance);
                previous.put(edge.getTo(), current.location);
                pq.offer(new LocationDistance(edge.getTo(), newDistance));
            }
        }
    }
    
    return reconstructPath(previous, start, end);
}
```

**Why Dijkstra's?**
- **Guaranteed Optimal**: Always finds the shortest path
- **Well-suited for Campus**: Works well with positive edge weights (distances)
- **Reliable**: Proven algorithm with extensive real-world usage

### 2. A* Search Algorithm
**Purpose**: Heuristic-based pathfinding for faster results
**Time Complexity**: O(b^d) where b is branching factor, d is depth
**Space Complexity**: O(b^d)

```java
public List<String> findPath(String start, String end) {
    PriorityQueue<AStarNode> openSet = new PriorityQueue<>();
    Set<String> closedSet = new HashSet<>();
    Map<String, Double> gScore = new HashMap<>();
    Map<String, String> cameFrom = new HashMap<>();
    
    // Initialize
    gScore.put(start, 0.0);
    Location startLoc = getLocation(start);
    Location endLoc = getLocation(end);
    double heuristic = calculateHeuristic(startLoc, endLoc);
    
    openSet.offer(new AStarNode(start, 0.0, heuristic));
    
    while (!openSet.isEmpty()) {
        AStarNode current = openSet.poll();
        
        if (current.locationId.equals(end)) {
            return reconstructPath(cameFrom, start, end);
        }
        
        closedSet.add(current.locationId);
        
        for (Edge edge : getEdges(current.locationId)) {
            if (closedSet.contains(edge.getTo())) continue;
            
            double tentativeGScore = gScore.get(current.locationId) + edge.getDistance();
            
            if (tentativeGScore < gScore.getOrDefault(edge.getTo(), Double.POSITIVE_INFINITY)) {
                cameFrom.put(edge.getTo(), current.locationId);
                gScore.put(edge.getTo(), tentativeGScore);
                
                Location neighbor = getLocation(edge.getTo());
                double hScore = calculateHeuristic(neighbor, endLoc);
                openSet.offer(new AStarNode(edge.getTo(), tentativeGScore, hScore));
            }
        }
    }
    
    return new ArrayList<>(); // No path found
}
```

**Heuristic Function**: Haversine Distance
```java
private double calculateHeuristic(Location from, Location to) {
    return from.distanceTo(to); // Haversine formula
}
```

**Why A*?**
- **Faster than Dijkstra**: Uses heuristics to guide search
- **Optimal with Admissible Heuristic**: Haversine distance never overestimates
- **Memory Efficient**: Often explores fewer nodes than Dijkstra

### 3. Haversine Distance Formula
**Purpose**: Calculate accurate distances between GPS coordinates
**Usage**: Both as heuristic and for actual distance calculation

```java
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
```

**Why Haversine?**
- **Geographic Accuracy**: Accounts for Earth's curvature
- **Real-world Applicability**: Provides actual walking distances
- **Admissible Heuristic**: Never overestimates for A*

### 4. Sorting Algorithms
**Quick Sort Implementation** for route sorting:
```java
public static void quickSort(List<Route> routes, SortCriteria criteria, int low, int high) {
    if (low < high) {
        int pivotIndex = partition(routes, criteria, low, high);
        quickSort(routes, criteria, low, pivotIndex - 1);
        quickSort(routes, criteria, pivotIndex + 1, high);
    }
}
```

**Merge Sort Implementation** for stable sorting:
```java
public static void mergeSort(List<Route> routes, SortCriteria criteria) {
    if (routes.size() <= 1) return;
    
    int mid = routes.size() / 2;
    List<Route> left = new ArrayList<>(routes.subList(0, mid));
    List<Route> right = new ArrayList<>(routes.subList(mid, routes.size()));
    
    mergeSort(left, criteria);
    mergeSort(right, criteria);
    merge(routes, left, right, criteria);
}
```

**Algorithm Choice Logic**:
- **Quick Sort**: Average O(n log n), in-place sorting
- **Merge Sort**: Guaranteed O(n log n), stable sorting
- **Selection based on data size and stability requirements**

### 5. Vogel's Approximation Method
**Purpose**: Transportation optimization for complex routing scenarios
**Application**: Multi-destination route optimization

```java
public class VogelApproximationMethod {
    public List<Allocation> solve(TransportationProblem problem) {
        List<Allocation> allocations = new ArrayList<>();
        int[][] costs = copyMatrix(problem.getCosts());
        int[] supply = Arrays.copyOf(problem.getSupply(), problem.getSupply().length);
        int[] demand = Arrays.copyOf(problem.getDemand(), problem.getDemand().length);
        
        while (!isComplete(supply, demand)) {
            // Calculate penalties
            int[] rowPenalties = calculateRowPenalties(costs, supply, demand);
            int[] colPenalties = calculateColPenalties(costs, supply, demand);
            
            // Find maximum penalty
            int maxPenalty = findMaxPenalty(rowPenalties, colPenalties);
            
            // Allocate based on maximum penalty
            Allocation allocation = allocateMaxPenalty(costs, supply, demand, 
                                                     rowPenalties, colPenalties, maxPenalty);
            allocations.add(allocation);
        }
        
        return allocations;
    }
}
```

**Why VAM?**
- **Near-optimal Solutions**: Usually within 5-10% of optimal
- **Computational Efficiency**: Much faster than optimal methods
- **Practical Application**: Suitable for multi-stop campus tours

---

## Core Components

### 1. Location Model
**Purpose**: Represents campus locations with metadata
```java
public class Location {
    private final String id;
    private final String name;
    private final double latitude;
    private final double longitude;
    private final LocationType type;
    private final String description;
    
    // Constructor, getters, and utility methods
}
```

**Design Decisions**:
- **Immutable Fields**: Prevents accidental modification
- **Type Safety**: LocationType enum ensures valid categories
- **Built-in Distance Calculation**: Encapsulates Haversine formula

### 2. Edge Model
**Purpose**: Represents connections between locations
```java
public class Edge {
    private final String from;
    private final String to;
    private final double distance;
    private final double baseTime;
    private final String pathType;
    private final double difficultyMultiplier;
    
    public double getEffectiveTime() {
        return baseTime * difficultyMultiplier;
    }
}
```

**Features**:
- **Weighted Edges**: Distance and time weights
- **Path Types**: Different types of campus paths (walkway, stairs, etc.)
- **Dynamic Time Calculation**: Adjusts for path difficulty

### 3. Route Model
**Purpose**: Represents complete paths between locations
```java
public class Route {
    private final List<Location> waypoints;
    private final List<Edge> edges;
    private final double totalDistance;
    private final double totalTime;
    private final String routeType;
    private final String description;
    
    // Analysis methods and getters
}
```

**Capabilities**:
- **Complete Path Information**: All waypoints and connections
- **Performance Metrics**: Total distance and time
- **Route Classification**: Shortest, fastest, or landmark-based

### 4. NavigationService
**Purpose**: Central service coordinating all navigation operations
```java
public class NavigationService {
    private final CampusGraph campusGraph;
    private final DijkstraAlgorithm dijkstra;
    private final AStarAlgorithm aStar;
    private final RouteSorter routeSorter;
    
    public List<Route> findRoutes(Location source, Location destination, 
                                 boolean optimizeForTime, int maxRoutes) {
        // Implementation combining multiple algorithms
    }
}
```

**Responsibilities**:
- **Algorithm Coordination**: Manages different pathfinding algorithms
- **Route Analysis**: Provides detailed route information
- **Location Management**: Handles campus location data
- **Search Operations**: Implements location search functionality

---

## User Interface

### JavaFX Architecture
**Framework Choice**: JavaFX for rich desktop applications
**Pattern**: FXML + Controller separation

### Primary Interface Components

1. **Location Selection**
   ```xml
   <ComboBox fx:id="sourceComboBox" />
   <ComboBox fx:id="destinationComboBox" />
   ```
   - **Auto-complete functionality**
   - **Sorted location lists**
   - **Double-click selection from search results**

2. **Route Type Selection**
   ```xml
   <RadioButton fx:id="shortestRadio" text="Shortest Distance" />
   <RadioButton fx:id="fastestRadio" text="Fastest Time" />
   <RadioButton fx:id="landmarkRadio" text="Via Landmark" />
   ```
   - **Toggle group for mutual exclusion**
   - **Dynamic UI updates based on selection**

3. **Results Display**
   ```xml
   <ListView fx:id="routesListView" />
   <TextArea fx:id="routeDetailsArea" />
   ```
   - **Custom cell renderers for routes**
   - **Detailed turn-by-turn directions**
   - **Route analysis and metrics**

### Event Handling Strategy
```java
@FXML
private void initialize() {
    // Setup UI components
    setupToggleGroups();
    setupEventListeners();
    setupCellFactories();
    loadInitialData();
}
```

**Key UI Features**:
- **Real-time Search**: Live filtering as user types
- **Visual Feedback**: Status updates and progress indicators
- **Error Handling**: User-friendly error messages
- **Accessibility**: Keyboard navigation support

---

## Code Logic Flow

### 1. Application Startup
```
App.main() → launch() → start() → loadFXML("primary") → PrimaryController.initialize()
```

### 2. Location Loading
```
initialize() → loadLocations() → NavigationService.getAllLocations() → CampusGraph.getLocations()
```

### 3. Route Finding Process
```
User Selection → handleFindRoutes() → NavigationService.findRoutes() → 
Algorithm Selection → Path Calculation → Route Construction → UI Update
```

### 4. Algorithm Selection Logic
```java
if (landmarkRadio.isSelected()) {
    routes = navigationService.findLandmarkBasedRoutes(source, destination, landmarkType, 3);
} else {
    boolean optimizeForTime = fastestRadio.isSelected();
    routes = navigationService.findRoutes(source, destination, optimizeForTime, 3);
}
```

### 5. Route Analysis Pipeline
```
Route Creation → Distance Calculation → Time Estimation → 
Difficulty Assessment → Performance Analysis → User Display
```

---

## Performance Analysis

### Algorithm Complexity Comparison

| Algorithm | Time Complexity | Space Complexity | Use Case |
|-----------|----------------|------------------|----------|
| Dijkstra | O((V+E) log V) | O(V) | Guaranteed shortest path |
| A* | O(b^d) | O(b^d) | Faster heuristic search |
| Quick Sort | O(n log n) avg | O(log n) | Route sorting |
| Merge Sort | O(n log n) | O(n) | Stable route sorting |

### Campus Graph Statistics
- **Vertices (Locations)**: ~50 campus locations
- **Edges (Paths)**: ~150 connections
- **Graph Density**: Sparse (realistic campus layout)
- **Average Degree**: ~6 connections per location

### Performance Optimizations

1. **Lazy Loading**: Locations loaded on demand
2. **Caching**: Frequently accessed routes cached
3. **Early Termination**: Search stops when target found
4. **Heuristic Pruning**: A* eliminates unpromising paths

### Memory Usage
- **Graph Storage**: O(V + E) = O(200) entries
- **Algorithm Working Set**: O(V) = O(50) for most operations
- **Route Storage**: O(k) where k is number of routes displayed

---

## Future Enhancements

### 1. Advanced Features
- **Real-time Traffic**: Dynamic path weights based on crowd data
- **Weather Integration**: Adjust routes based on weather conditions
- **Accessibility Options**: Wheelchair-accessible route planning
- **Multi-modal Transportation**: Include shuttle and bicycle routes

### 2. Algorithm Improvements
- **Bidirectional Search**: Faster pathfinding for long routes
- **Dynamic Programming**: Optimize multi-destination routes
- **Machine Learning**: Learn user preferences for personalized routing

### 3. User Interface Enhancements
- **Interactive Maps**: Visual route display
- **Voice Navigation**: Audio turn-by-turn directions
- **Mobile App**: Cross-platform mobile application
- **Offline Mode**: Cached data for offline usage

### 4. Data Enhancements
- **Real-time Updates**: Live location and availability data
- **User-generated Content**: Reviews and ratings for locations
- **Event Integration**: Route planning around campus events
- **Crowd-sourced Data**: User-reported path conditions

---

## Conclusion

UG Navigate demonstrates a comprehensive implementation of graph algorithms and data structures for real-world navigation problems. The application successfully combines multiple pathfinding algorithms, optimization techniques, and modern UI frameworks to create a practical campus navigation solution.

The choice of algorithms and data structures was driven by the specific requirements of campus navigation:
- **Graph representation** for campus layout
- **Priority queues** for efficient pathfinding
- **Heuristic algorithms** for faster results
- **Sorting algorithms** for result organization

The modular architecture ensures maintainability and extensibility, while the JavaFX interface provides an intuitive user experience. The implementation serves as both a practical tool and an educational demonstration of computer science concepts applied to real-world problems.

---

*This document provides a comprehensive overview of the UG Navigate implementation. For specific code details, refer to the individual source files in the project repository.*
