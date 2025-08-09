# UG Navigate: Algorithms, Pseudocodes & Flowcharts
## Detailed Algorithmic Analysis and Implementation Guide

---

## Table of Contents
1. [Dijkstra's Algorithm](#dijkstras-algorithm)
2. [A* Search Algorithm](#a-search-algorithm)
3. [Haversine Distance Formula](#haversine-distance-formula)
4. [Quick Sort Algorithm](#quick-sort-algorithm)
5. [Merge Sort Algorithm](#merge-sort-algorithm)
6. [Vogel's Approximation Method](#vogels-approximation-method)
7. [Route Search Algorithm](#route-search-algorithm)
8. [Campus Graph Construction](#campus-graph-construction)

---

## Dijkstra's Algorithm

### Algorithm Overview
Dijkstra's algorithm finds the shortest path between nodes in a weighted graph. It's particularly suitable for campus navigation where all edge weights (distances) are positive.

### Step-by-Step Process

#### Step 1: Initialization
1. Create a priority queue (min-heap) for unvisited nodes
2. Set distance to source = 0, all other distances = ∞
3. Add all nodes to priority queue
4. Create previous node tracking map

#### Step 2: Main Loop
1. Extract node with minimum distance from queue
2. If this is the destination, stop
3. For each unvisited neighbor:
   - Calculate tentative distance = current distance + edge weight
   - If tentative distance < neighbor's current distance:
     - Update neighbor's distance
     - Update previous node pointer
     - Add/update neighbor in priority queue

#### Step 3: Path Reconstruction
1. Start from destination
2. Follow previous node pointers back to source
3. Reverse the path to get source → destination route

### Pseudocode
```
ALGORITHM Dijkstra(graph, source, destination)
INPUT: 
    graph - weighted graph with locations and edges
    source - starting location
    destination - target location
OUTPUT: 
    shortest path from source to destination

BEGIN
    // Initialize data structures
    distances ← new Map()
    previous ← new Map()
    priorityQueue ← new PriorityQueue()
    visited ← new Set()
    
    // Set initial distances
    FOR each location in graph.getAllLocations() DO
        distances[location] ← INFINITY
        previous[location] ← null
    END FOR
    
    distances[source] ← 0
    priorityQueue.add(LocationDistance(source, 0))
    
    // Main algorithm loop
    WHILE priorityQueue is not empty DO
        current ← priorityQueue.extractMin()
        
        IF current.location = destination THEN
            BREAK  // Found shortest path to destination
        END IF
        
        IF current.location in visited THEN
            CONTINUE  // Skip already processed nodes
        END IF
        
        visited.add(current.location)
        
        // Check all neighbors
        FOR each edge in graph.getEdges(current.location) DO
            neighbor ← edge.getTo()
            
            IF neighbor not in visited THEN
                newDistance ← distances[current.location] + edge.getDistance()
                
                IF newDistance < distances[neighbor] THEN
                    distances[neighbor] ← newDistance
                    previous[neighbor] ← current.location
                    priorityQueue.add(LocationDistance(neighbor, newDistance))
                END IF
            END IF
        END FOR
    END WHILE
    
    // Reconstruct path
    path ← reconstructPath(previous, source, destination)
    RETURN path
END

FUNCTION reconstructPath(previous, source, destination)
BEGIN
    path ← new List()
    current ← destination
    
    WHILE current ≠ null DO
        path.addFirst(current)
        current ← previous[current]
    END WHILE
    
    IF path.first() = source THEN
        RETURN path
    ELSE
        RETURN null  // No path exists
    END IF
END
```

### Flowchart
```
    [START]
       |
   [Initialize distances, 
    priority queue, visited set]
       |
   [Set source distance = 0,
    others = ∞]
       |
   [Add source to priority queue]
       |
       v
┌─[Priority queue empty?]
│     |
│    No
│     |
│  [Extract minimum node]
│     |
│  [Is destination?] ──Yes──> [FOUND PATH]
│     |                           |
│    No                          |
│     |                          |
│  [Mark as visited]             |
│     |                          |
│  [For each neighbor]           |
│     |                          |
│  [Calculate new distance]      |
│     |                          |
│  [Better path?] ──Yes──> [Update distance,
│     |                    add to queue]
│    No                          |
│     |                          |
│  [Next neighbor] ──────────────┘
│     |
└─────┘
       |
      Yes
       |
   [No path found]
       |
    [END]
```

### Complexity Analysis
- **Time Complexity**: O((V + E) log V) using binary heap
- **Space Complexity**: O(V) for distances and previous arrays
- **Best Case**: O(V log V) when destination is close to source
- **Worst Case**: O((V + E) log V) when exploring entire graph

---

## A* Search Algorithm

### Algorithm Overview
A* is an informed search algorithm that uses heuristics to find the optimal path more efficiently than Dijkstra's algorithm. It maintains two costs: g(n) - actual cost from start, and h(n) - heuristic estimate to goal.

### Step-by-Step Process

#### Step 1: Initialization
1. Create open set (priority queue) and closed set
2. Initialize g-score and f-score maps
3. Set g(start) = 0, f(start) = h(start, goal)
4. Add start node to open set

#### Step 2: Main Search Loop
1. Current = node in open set with lowest f-score
2. If current = goal, reconstruct and return path
3. Move current from open set to closed set
4. For each neighbor of current:
   - Skip if in closed set
   - Calculate tentative g-score
   - If better path found, update scores and parent

#### Step 3: Heuristic Function
Uses Haversine distance as admissible heuristic (never overestimates actual cost)

### Pseudocode
```
ALGORITHM AStar(graph, source, destination, optimizeForTime)
INPUT:
    graph - weighted graph
    source - starting location  
    destination - target location
    optimizeForTime - boolean flag for optimization type
OUTPUT:
    optimal path using A* heuristic

BEGIN
    openSet ← new PriorityQueue()
    closedSet ← new Set()
    gScore ← new Map()
    fScore ← new Map()
    previous ← new Map()
    
    // Initialize all scores to infinity
    FOR each location in graph.getAllLocations() DO
        gScore[location] ← INFINITY
        fScore[location] ← INFINITY
    END FOR
    
    // Initialize source
    gScore[source] ← 0
    fScore[source] ← heuristic(source, destination, optimizeForTime)
    openSet.add(AStarNode(source, fScore[source]))
    
    WHILE openSet is not empty DO
        current ← openSet.extractMin()
        
        IF current.location = destination THEN
            RETURN reconstructPath(previous, source, destination)
        END IF
        
        closedSet.add(current.location)
        
        FOR each edge in graph.getEdges(current.location) DO
            neighbor ← edge.getTo()
            
            IF neighbor in closedSet THEN
                CONTINUE
            END IF
            
            tentativeGScore ← gScore[current.location] + edge.getCost(optimizeForTime)
            
            IF tentativeGScore < gScore[neighbor] THEN
                previous[neighbor] ← current.location
                gScore[neighbor] ← tentativeGScore
                fScore[neighbor] ← tentativeGScore + heuristic(neighbor, destination, optimizeForTime)
                
                // Update open set
                openSet.removeIf(node.location = neighbor)
                openSet.add(AStarNode(neighbor, fScore[neighbor]))
            END IF
        END FOR
    END WHILE
    
    RETURN null  // No path found
END

FUNCTION heuristic(from, to, optimizeForTime)
BEGIN
    distance ← haversineDistance(from, to)
    
    IF optimizeForTime THEN
        RETURN distance / 83.33  // Convert to minutes (5 km/h walking speed)
    ELSE
        RETURN distance
    END IF
END
```

### Flowchart
```
    [START]
       |
    [Initialize open/closed sets,
     g-score, f-score maps]
       |
    [Set g(source)=0, 
     f(source)=h(source,dest)]
       |
    [Add source to open set]
       |
       v
┌─[Open set empty?]
│     |
│    No
│     |
│  [Get node with lowest f-score]
│     |
│  [Is destination?] ──Yes──> [Reconstruct path]
│     |                           |
│    No                          |
│     |                          |
│  [Move to closed set]          |
│     |                          |
│  [For each neighbor]           |
│     |                          |
│  [In closed set?] ──Yes──┐     |
│     |                    │     |
│    No                    │     |
│     |                    │     |
│  [Calculate tentative    │     |
│   g-score]               │     |
│     |                    │     |
│  [Better path?] ──No─────┤     |
│     |                    │     |
│    Yes                   │     |
│     |                    │     |
│  [Update g,f scores      │     |
│   and parent]            │     |
│     |                    │     |
│  [Update open set]       │     |
│     |                    │     |
│  [Next neighbor] ◄───────┘     |║
│     |                          |
└─────┘                          |
       |                         |
      Yes                        |
       |                         |
   [No path found] ◄──────────────┘
       |
    [END]
```

### Complexity Analysis
- **Time Complexity**: O(b^d) where b is branching factor, d is depth
- **Space Complexity**: O(b^d) for storing nodes in open/closed sets
- **Optimality**: Guaranteed if heuristic is admissible and consistent
- **Performance**: Generally faster than Dijkstra due to heuristic guidance

---

## Haversine Distance Formula

### Algorithm Overview
Calculates the great-circle distance between two points on Earth using their latitude and longitude coordinates. Essential for geographic navigation systems.

### Mathematical Foundation
The Haversine formula determines the distance between two points on a sphere given their latitude and longitude:

```
a = sin²(Δφ/2) + cos φ1 ⋅ cos φ2 ⋅ sin²(Δλ/2)
c = 2 ⋅ atan2(√a, √(1−a))
d = R ⋅ c
```

Where:
- φ is latitude, λ is longitude, R is earth's radius
- Δφ is the difference in latitude, Δλ is the difference in longitude

### Step-by-Step Process

#### Step 1: Convert to Radians
Convert latitude and longitude from degrees to radians

#### Step 2: Calculate Differences
Compute differences in latitude and longitude

#### Step 3: Apply Haversine Formula
Use trigonometric functions to calculate angular distance

#### Step 4: Convert to Linear Distance
Multiply by Earth's radius to get distance in meters

### Pseudocode
```
ALGORITHM haversineDistance(location1, location2)
INPUT:
    location1 - first location with lat/lng
    location2 - second location with lat/lng
OUTPUT:
    distance in meters between the two locations

BEGIN
    R ← 6371000  // Earth's radius in meters
    
    // Convert degrees to radians
    lat1Rad ← toRadians(location1.latitude)
    lat2Rad ← toRadians(location2.latitude)
    deltaLatRad ← toRadians(location2.latitude - location1.latitude)
    deltaLonRad ← toRadians(location2.longitude - location1.longitude)
    
    // Haversine formula
    a ← sin(deltaLatRad/2) * sin(deltaLatRad/2) +
        cos(lat1Rad) * cos(lat2Rad) *
        sin(deltaLonRad/2) * sin(deltaLonRad/2)
    
    c ← 2 * atan2(sqrt(a), sqrt(1-a))
    
    distance ← R * c
    
    RETURN distance
END

FUNCTION toRadians(degrees)
BEGIN
    RETURN degrees * (π / 180)
END
```

### Flowchart
```
    [START]
       |
    [Input: lat1, lng1, lat2, lng2]
       |
    [Set Earth radius R = 6371000m]
       |
    [Convert degrees to radians:
     lat1Rad, lat2Rad, ΔlatRad, ΔlngRad]
       |
    [Calculate a = sin²(Δlat/2) + 
     cos(lat1) × cos(lat2) × sin²(Δlng/2)]
       |
    [Calculate c = 2 × atan2(√a, √(1-a))]
       |
    [Calculate distance = R × c]
       |
    [Return distance in meters]
       |
    [END]
```

### Implementation Considerations
- **Accuracy**: Good for distances up to ~20km (campus scale)
- **Performance**: Fast computation using built-in math functions
- **Limitations**: Assumes Earth is a perfect sphere
- **Alternative**: Vincenty's formula for higher precision over long distances

---

## Quick Sort Algorithm

### Algorithm Overview
Quick Sort is a divide-and-conquer algorithm that sorts routes by selecting a pivot element and partitioning the array around it.

### Step-by-Step Process

#### Step 1: Choose Pivot
Select a pivot element (typically first, last, or median)

#### Step 2: Partition
Rearrange array so elements smaller than pivot come before it, larger elements come after

#### Step 3: Recursive Sort
Recursively apply quick sort to sub-arrays

### Pseudocode
```
ALGORITHM quickSort(routes, criteria, low, high)
INPUT:
    routes - list of routes to sort
    criteria - sorting criteria (DISTANCE, TIME, EFFICIENCY)
    low - starting index
    high - ending index
OUTPUT:
    sorted routes list

BEGIN
    IF low < high THEN
        pivotIndex ← partition(routes, criteria, low, high)
        quickSort(routes, criteria, low, pivotIndex - 1)
        quickSort(routes, criteria, pivotIndex + 1, high)
    END IF
END

FUNCTION partition(routes, criteria, low, high)
BEGIN
    pivot ← routes[high]  // Choose last element as pivot
    i ← low - 1
    
    FOR j ← low TO high - 1 DO
        IF compareRoutes(routes[j], pivot, criteria) ≤ 0 THEN
            i ← i + 1
            swap(routes[i], routes[j])
        END IF
    END FOR
    
    swap(routes[i + 1], routes[high])
    RETURN i + 1
END

FUNCTION compareRoutes(route1, route2, criteria)
BEGIN
    SWITCH criteria
        CASE DISTANCE:
            RETURN route1.getTotalDistance() - route2.getTotalDistance()
        CASE TIME:
            RETURN route1.getTotalTime() - route2.getTotalTime()
        CASE EFFICIENCY:
            RETURN route1.getEfficiencyScore() - route2.getEfficiencyScore()
    END SWITCH
END
```

### Flowchart
```
    [START: quickSort(routes, low, high)]
       |
    [low < high?] ──No──> [END]
       |
      Yes
       |
    [pivotIndex = partition(routes, low, high)]
       |
    [quickSort(routes, low, pivotIndex-1)]
       |
    [quickSort(routes, pivotIndex+1, high)]
       |
    [END]

    [PARTITION PROCESS]
    [START: partition(routes, low, high)]
       |
    [pivot = routes[high], i = low-1]
       |
    [FOR j = low to high-1]
       |
    [routes[j] ≤ pivot?] ──Yes──> [i++, swap(routes[i], routes[j])]
       |                              |
      No                              |
       |                              |
    [Next j] ◄─────────────────────────┘
       |
    [swap(routes[i+1], routes[high])]
       |
    [RETURN i+1]
```

---

## Merge Sort Algorithm

### Algorithm Overview
Merge Sort is a stable, divide-and-conquer sorting algorithm that guarantees O(n log n) performance.

### Step-by-Step Process

#### Step 1: Divide
Split the array into two halves

#### Step 2: Conquer
Recursively sort both halves

#### Step 3: Merge
Combine sorted halves into final sorted array

### Pseudocode
```
ALGORITHM mergeSort(routes, criteria)
INPUT:
    routes - list of routes to sort
    criteria - sorting criteria
OUTPUT:
    sorted routes list

BEGIN
    IF routes.size() ≤ 1 THEN
        RETURN routes
    END IF
    
    mid ← routes.size() / 2
    left ← routes.subList(0, mid)
    right ← routes.subList(mid, routes.size())
    
    left ← mergeSort(left, criteria)
    right ← mergeSort(right, criteria)
    
    RETURN merge(left, right, criteria)
END

FUNCTION merge(left, right, criteria)
BEGIN
    result ← new List()
    i ← 0, j ← 0
    
    WHILE i < left.size() AND j < right.size() DO
        IF compareRoutes(left[i], right[j], criteria) ≤ 0 THEN
            result.add(left[i])
            i ← i + 1
        ELSE
            result.add(right[j])
            j ← j + 1
        END IF
    END WHILE
    
    // Add remaining elements
    WHILE i < left.size() DO
        result.add(left[i])
        i ← i + 1
    END WHILE
    
    WHILE j < right.size() DO
        result.add(right[j])
        j ← j + 1
    END WHILE
    
    RETURN result
END
```

### Flowchart
```
    [START: mergeSort(routes)]
       |
    [Size ≤ 1?] ──Yes──> [RETURN routes]
       |
      No
       |
    [Split into left and right halves]
       |
    [left = mergeSort(left)]
       |
    [right = mergeSort(right)]
       |
    [result = merge(left, right)]
       |
    [RETURN result]

    [MERGE PROCESS]
    [START: merge(left, right)]
       |
    [Initialize i=0, j=0, result=empty]
       |
       v
┌─[i < left.size AND j < right.size?]
│     |
│    Yes
│     |
│  [left[i] ≤ right[j]?] ──Yes──> [Add left[i] to result, i++]
│     |                              |
│    No                              |
│     |                              |
│  [Add right[j] to result, j++] ◄───┘
│     |
└─────┘
       |
      No
       |
    [Add remaining left elements]
       |
    [Add remaining right elements]
       |
    [RETURN result]
```

---

## Vogel's Approximation Method

### Algorithm Overview
VAM is a heuristic method for solving transportation problems. Used for optimizing multi-destination routes in campus navigation.

### Step-by-Step Process

#### Step 1: Calculate Penalties
For each row and column, find the difference between the two smallest costs

#### Step 2: Select Maximum Penalty
Choose the row or column with the highest penalty

#### Step 3: Allocate
In the selected row/column, allocate to the cell with minimum cost

#### Step 4: Update
Reduce supply/demand and repeat until all allocated

### Pseudocode
```
ALGORITHM vogelApproximation(supplies, demands, costs)
INPUT:
    supplies - array of supply values
    demands - array of demand values  
    costs - 2D cost matrix
OUTPUT:
    allocation matrix with optimal assignments

BEGIN
    allocations ← new List()
    supplyCopy ← copy(supplies)
    demandCopy ← copy(demands)
    costMatrix ← copy(costs)
    
    WHILE NOT allAllocated(supplyCopy, demandCopy) DO
        // Calculate penalties
        rowPenalties ← calculateRowPenalties(costMatrix, supplyCopy, demandCopy)
        colPenalties ← calculateColPenalties(costMatrix, supplyCopy, demandCopy)
        
        // Find maximum penalty
        maxPenalty ← findMaxPenalty(rowPenalties, colPenalties)
        
        IF maxPenalty.isRow THEN
            row ← maxPenalty.index
            col ← findMinCostColumn(costMatrix, row, demandCopy)
        ELSE
            col ← maxPenalty.index  
            row ← findMinCostRow(costMatrix, col, supplyCopy)
        END IF
        
        // Make allocation
        allocation ← min(supplyCopy[row], demandCopy[col])
        allocations.add(Allocation(row, col, allocation, costMatrix[row][col]))
        
        // Update supplies and demands
        supplyCopy[row] ← supplyCopy[row] - allocation
        demandCopy[col] ← demandCopy[col] - allocation
        
        // Mark row/column as exhausted if needed
        IF supplyCopy[row] = 0 THEN
            markRowExhausted(costMatrix, row)
        END IF
        
        IF demandCopy[col] = 0 THEN
            markColExhausted(costMatrix, col)
        END IF
    END WHILE
    
    RETURN allocations
END

FUNCTION calculateRowPenalties(costs, supplies, demands)
BEGIN
    penalties ← new Array(supplies.length)
    
    FOR i ← 0 TO supplies.length - 1 DO
        IF supplies[i] > 0 THEN
            validCosts ← getValidRowCosts(costs, i, demands)
            sort(validCosts)
            
            IF validCosts.length ≥ 2 THEN
                penalties[i] ← validCosts[1] - validCosts[0]
            ELSE IF validCosts.length = 1 THEN
                penalties[i] ← validCosts[0]
            ELSE
                penalties[i] ← 0
            END IF
        ELSE
            penalties[i] ← -1  // Row exhausted
        END IF
    END FOR
    
    RETURN penalties
END
```

### Flowchart
```
    [START: Vogel's Approximation]
       |
    [Initialize supplies, demands, costs]
       |
       v
┌─[All allocated?]
│     |
│    No
│     |
│  [Calculate row penalties]
│     |
│  [Calculate column penalties]
│     |
│  [Find maximum penalty (row/col)]
│     |
│  [Is row penalty max?] ──Yes──> [Find min cost in row]
│     |                              |
│    No                              |
│     |                              |
│  [Find min cost in column] ◄───────┘
│     |
│  [Calculate allocation = 
│   min(supply, demand)]
│     |
│  [Update supply and demand]
│     |
│  [Mark exhausted rows/columns]
│     |
└─────┘
       |
      Yes
       |
    [Return allocation list]
       |
    [END]
```

---

## Route Search Algorithm

### Algorithm Overview
Comprehensive search algorithm that combines location filtering, keyword matching, and proximity-based ranking.

### Step-by-Step Process

#### Step 1: Keyword Processing
Normalize and tokenize search keywords

#### Step 2: Location Filtering
Filter locations by type, name, and description

#### Step 3: Relevance Scoring
Score locations based on match quality

#### Step 4: Proximity Ranking
Rank results by distance from current location

### Pseudocode
```
ALGORITHM searchLocations(keyword, currentLocation, locationTypes)
INPUT:
    keyword - search string
    currentLocation - user's current position (optional)
    locationTypes - filter by location types (optional)
OUTPUT:
    ranked list of matching locations

BEGIN
    results ← new List()
    searchTerms ← tokenize(keyword.toLowerCase())
    
    FOR each location in allLocations DO
        // Apply type filter if specified
        IF locationTypes ≠ null AND location.type NOT IN locationTypes THEN
            CONTINUE
        END IF
        
        score ← calculateRelevanceScore(location, searchTerms)
        
        IF score > 0 THEN
            result ← SearchResult(location, score)
            
            // Add proximity score if current location available
            IF currentLocation ≠ null THEN
                distance ← haversineDistance(currentLocation, location)
                result.proximityScore ← 1.0 / (1.0 + distance / 1000.0)
            END IF
            
            results.add(result)
        END IF
    END FOR
    
    // Sort by combined relevance and proximity
    sort(results, by: (relevanceScore * 0.7 + proximityScore * 0.3) DESC)
    
    RETURN results.locations
END

FUNCTION calculateRelevanceScore(location, searchTerms)
BEGIN
    score ← 0
    locationText ← (location.name + " " + location.description).toLowerCase()
    
    FOR each term in searchTerms DO
        // Exact name match (highest weight)
        IF location.name.toLowerCase().contains(term) THEN
            score ← score + 10
        END IF
        
        // Description match (medium weight)  
        IF location.description.toLowerCase().contains(term) THEN
            score ← score + 5
        END IF
        
        // Fuzzy match (lowest weight)
        IF fuzzyMatch(locationText, term) THEN
            score ← score + 2
        END IF
        
        // Location type match
        IF location.type.displayName.toLowerCase().contains(term) THEN
            score ← score + 3
        END IF
    END FOR
    
    RETURN score
END
```

### Flowchart
```
    [START: searchLocations(keyword)]
       |
    [Tokenize and normalize keyword]
       |
    [Initialize results list]
       |
       v
┌─[For each location in campus]
│     |
│  [Apply type filter] ──Filtered──> [Next location]
│     |                                   |
│   Pass                                  |
│     |                                   |
│  [Calculate relevance score]            |
│     |                                   |
│  [Score > 0?] ──No──> [Next location] ──┘
│     |
│    Yes
│     |
│  [Create search result]
│     |
│  [Current location available?] ──Yes──> [Calculate proximity score]
│     |                                      |
│    No                                      |
│     |                                      |
│  [Add to results] ◄─────────────────────────┘
│     |
└─[Next location]
       |
    [Sort by relevance + proximity]
       |
    [Return top results]
       |
    [END]
```

---

## Campus Graph Construction

### Algorithm Overview
Constructs a weighted graph representing the University of Ghana campus with locations as nodes and paths as edges.

### Step-by-Step Process

#### Step 1: Location Initialization
Create location nodes with GPS coordinates and metadata

#### Step 2: Edge Construction
Define connections between locations with distance and time weights

#### Step 3: Graph Validation
Ensure connectivity and validate path weights

### Pseudocode
```
ALGORITHM buildCampusGraph()
INPUT: none
OUTPUT: weighted graph representing UG campus

BEGIN
    graph ← new CampusGraph()
    
    // Step 1: Add all campus locations
    locations ← initializeLocations()
    FOR each location in locations DO
        graph.addLocation(location)
    END FOR
    
    // Step 2: Create edges between connected locations
    connections ← defineConnections()
    FOR each connection in connections DO
        fromLocation ← graph.getLocation(connection.from)
        toLocation ← graph.getLocation(connection.to)
        
        // Calculate edge properties
        distance ← haversineDistance(fromLocation, toLocation)
        baseTime ← estimateWalkingTime(distance, connection.pathType)
        difficulty ← getPathDifficulty(connection.pathType)
        
        // Create bidirectional edges
        edge1 ← new Edge(fromLocation, toLocation, distance, baseTime, 
                        connection.pathType, difficulty)
        edge2 ← new Edge(toLocation, fromLocation, distance, baseTime,
                        connection.pathType, difficulty)
        
        graph.addEdge(edge1)
        graph.addEdge(edge2)
    END FOR
    
    // Step 3: Validate graph connectivity
    IF NOT isConnected(graph) THEN
        THROW GraphValidationException("Campus graph is not fully connected")
    END IF
    
    RETURN graph
END

FUNCTION initializeLocations()
BEGIN
    locations ← new List()
    
    // Academic buildings
    locations.add(Location("BAL", "Balme Library", 5.6502, -0.1883, LIBRARY, "Main university library"))
    locations.add(Location("GH", "Great Hall", 5.6512, -0.1875, ACADEMIC, "Main auditorium"))
    locations.add(Location("NNB", "N.N. Building", 5.6495, -0.1890, ACADEMIC, "Faculty of Arts"))
    
    // Residential halls
    locations.add(Location("LEG", "Legon Hall", 5.6525, -0.1865, RESIDENTIAL, "Male residential hall"))
    locations.add(Location("COM", "Commonwealth Hall", 5.6485, -0.1895, RESIDENTIAL, "Male residential hall"))
    
    // Service locations
    locations.add(Location("GCB", "GCB Bank", 5.6508, -0.1878, BANK, "University branch"))
    locations.add(Location("POST", "Post Office", 5.6505, -0.1885, SERVICE, "Campus postal service"))
    
    // Recreation
    locations.add(Location("POOL", "Swimming Pool", 5.6490, -0.1870, RECREATION, "University pool"))
    locations.add(Location("GYM", "Gymnasium", 5.6488, -0.1875, RECREATION, "Sports facility"))
    
    // Transportation
    locations.add(Location("OKPO", "Okponglo Station", 5.6480, -0.1900, TRANSPORT, "Main transport hub"))
    locations.add(Location("BUS", "Campus Bus Stop", 5.6500, -0.1880, TRANSPORT, "Internal bus stop"))
    
    RETURN locations
END

FUNCTION defineConnections()
BEGIN
    connections ← new List()
    
    // Main campus walkways
    connections.add(Connection("BAL", "GH", "paved_walkway"))
    connections.add(Connection("BAL", "NNB", "paved_walkway"))
    connections.add(Connection("GH", "LEG", "paved_walkway"))
    connections.add(Connection("NNB", "COM", "dirt_path"))
    
    // Service area connections
    connections.add(Connection("GCB", "POST", "paved_walkway"))
    connections.add(Connection("BAL", "GCB", "paved_walkway"))
    connections.add(Connection("POST", "BUS", "paved_walkway"))
    
    // Recreation connections
    connections.add(Connection("POOL", "GYM", "paved_walkway"))
    connections.add(Connection("LEG", "POOL", "stairs"))
    connections.add(Connection("COM", "GYM", "dirt_path"))
    
    // Transportation connections
    connections.add(Connection("BUS", "OKPO", "road"))
    connections.add(Connection("COM", "OKPO", "paved_walkway"))
    
    RETURN connections
END
```

### Complexity Analysis Summary

| Algorithm | Time Complexity | Space Complexity | Use Case |
|-----------|----------------|------------------|----------|
| Dijkstra | O((V+E) log V) | O(V) | Guaranteed shortest path |
| A* | O(b^d) | O(b^d) | Heuristic-guided search |
| Quick Sort | O(n log n) avg | O(log n) | Fast route sorting |
| Merge Sort | O(n log n) | O(n) | Stable route sorting |
| Haversine | O(1) | O(1) | Distance calculation |
| VAM | O(mn(m+n)) | O(mn) | Transportation optimization |
| Search | O(n) | O(k) | Location discovery |
| Graph Build | O(V+E) | O(V+E) | One-time initialization |

---

## Implementation Notes

### Performance Optimizations
1. **Lazy Loading**: Campus data loaded on first access
2. **Caching**: Frequently requested routes cached in memory
3. **Early Termination**: Algorithms stop when target found
4. **Heuristic Pruning**: A* eliminates unpromising paths

### Error Handling
1. **Input Validation**: All user inputs validated before processing
2. **Graph Connectivity**: Ensures valid paths exist between locations
3. **Numerical Stability**: Handles floating-point precision issues
4. **Graceful Degradation**: Fallback algorithms when primary fails

### Scalability Considerations
1. **Modular Design**: Easy to add new locations and algorithms
2. **Configuration-Driven**: Campus data externally configurable
3. **Algorithm Selection**: Automatic algorithm choice based on problem size
4. **Memory Management**: Efficient data structure usage

This comprehensive algorithmic guide provides the theoretical foundation and practical implementation details for the UG Navigate campus navigation system.
