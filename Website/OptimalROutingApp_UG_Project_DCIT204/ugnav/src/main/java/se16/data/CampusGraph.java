package se16.data;

import se16.model.*;
import java.util.*;

/**
 * Represents the UG campus as a graph with locations and connections
 */
public class CampusGraph {
    private final Map<String, Location> locations;
    private final Map<Location, List<Edge>> adjacencyList;

    public CampusGraph() {
        this.locations = new HashMap<>();
        this.adjacencyList = new HashMap<>();
        initializeCampusData();
    }

    /**
     * Initialize UG campus locations and connections
     */
    private void initializeCampusData() {
        // Main Academic Buildings
        addLocation("main_gate", "Main Gate", 5.6502, -0.1860, LocationType.ENTRANCE, "Main entrance to UG campus");
        addLocation("great_hall", "Great Hall", 5.6515, -0.1875, LocationType.LANDMARK, "Iconic ceremonial hall");
        addLocation("balme_library", "Balme Library", 5.6520, -0.1870, LocationType.LIBRARY, "Main university library");
        addLocation("admin_block", "Administration Block", 5.6510, -0.1865, LocationType.ADMINISTRATIVE, "Main administration building");
        
        // Academic Faculties
        addLocation("arts_faculty", "Faculty of Arts", 5.6525, -0.1880, LocationType.ACADEMIC, "Faculty of Arts building");
        addLocation("science_faculty", "Faculty of Science", 5.6530, -0.1885, LocationType.ACADEMIC, "Faculty of Science");
        addLocation("engineering_faculty", "College of Engineering", 5.6535, -0.1890, LocationType.ACADEMIC, "Engineering College");
        addLocation("business_school", "Business School", 5.6540, -0.1895, LocationType.ACADEMIC, "University of Ghana Business School");
        addLocation("medical_school", "Medical School", 5.6545, -0.1900, LocationType.ACADEMIC, "School of Medicine and Dentistry");
        
        // Residential Halls
        addLocation("legon_hall", "Legon Hall", 5.6480, -0.1820, LocationType.RESIDENTIAL, "Premier residential hall");
        addLocation("akuafo_hall", "Akuafo Hall", 5.6485, -0.1825, LocationType.RESIDENTIAL, "Traditional residential hall");
        addLocation("commonwealth_hall", "Commonwealth Hall", 5.6490, -0.1830, LocationType.RESIDENTIAL, "Commonwealth residential hall");
        addLocation("mensah_sarbah_hall", "Mensah Sarbah Hall", 5.6495, -0.1835, LocationType.RESIDENTIAL, "Mensah Sarbah residential hall");
        addLocation("volta_hall", "Volta Hall", 5.6500, -0.1840, LocationType.RESIDENTIAL, "Volta residential hall");
        
        // Facilities and Services
        addLocation("night_market", "Night Market", 5.6475, -0.1850, LocationType.DINING, "Popular food court");
        addLocation("jqb", "JQB (Pent Hall)", 5.6505, -0.1845, LocationType.DINING, "Jean Nelson Aka Jeannette Quarcoopome Building");
        addLocation("diaspora", "Diaspora", 5.6470, -0.1815, LocationType.RECREATIONAL, "Student recreational center");
        addLocation("university_hospital", "University Hospital", 5.6550, -0.1905, LocationType.MEDICAL, "Teaching hospital");
        
        // Banks and ATMs
        addLocation("gcb_bank", "GCB Bank", 5.6508, -0.1855, LocationType.BANK, "Ghana Commercial Bank branch");
        addLocation("uba_bank", "UBA Bank", 5.6512, -0.1858, LocationType.BANK, "United Bank for Africa");
        addLocation("stanbic_atm", "Stanbic ATM", 5.6518, -0.1862, LocationType.BANK, "Stanbic Bank ATM");
        
        // Transport and Parking
        addLocation("shuttle_station", "Shuttle Station", 5.6500, -0.1855, LocationType.TRANSPORT, "Campus shuttle stop");
        addLocation("taxi_rank", "Taxi Rank", 5.6498, -0.1852, LocationType.TRANSPORT, "Main taxi station");
        addLocation("car_park_a", "Car Park A", 5.6515, -0.1850, LocationType.PARKING, "Main parking area");
        addLocation("car_park_b", "Car Park B", 5.6520, -0.1885, LocationType.PARKING, "Academic area parking");

        // Create connections between locations
        createCampusConnections();
    }

    private void addLocation(String id, String name, double lat, double lon, 
                           LocationType type, String description) {
        Location location = new Location(id, name, lat, lon, type, description);
        locations.put(id, location);
        adjacencyList.put(location, new ArrayList<>());
    }

    private void createCampusConnections() {
        // Main pathways and connections with realistic walking times and traffic factors
        
        // From Main Gate
        addBidirectionalEdge("main_gate", "admin_block", 200, 2.5, 1.2, "main_road");
        addBidirectionalEdge("main_gate", "shuttle_station", 150, 2.0, 1.1, "walkway");
        addBidirectionalEdge("main_gate", "taxi_rank", 100, 1.5, 1.1, "walkway");
        
        // Central campus connections
        addBidirectionalEdge("admin_block", "great_hall", 300, 3.5, 1.0, "ceremonial_path");
        addBidirectionalEdge("admin_block", "balme_library", 250, 3.0, 1.1, "walkway");
        addBidirectionalEdge("great_hall", "balme_library", 200, 2.5, 1.0, "walkway");
        
        // Academic area connections
        addBidirectionalEdge("balme_library", "arts_faculty", 300, 3.5, 1.2, "academic_path");
        addBidirectionalEdge("arts_faculty", "science_faculty", 250, 3.0, 1.1, "academic_path");
        addBidirectionalEdge("science_faculty", "engineering_faculty", 200, 2.5, 1.0, "academic_path");
        addBidirectionalEdge("engineering_faculty", "business_school", 300, 3.5, 1.1, "academic_path");
        addBidirectionalEdge("business_school", "medical_school", 250, 3.0, 1.0, "academic_path");
        
        // Residential hall connections
        addBidirectionalEdge("legon_hall", "akuafo_hall", 200, 2.5, 1.0, "residential_path");
        addBidirectionalEdge("akuafo_hall", "commonwealth_hall", 250, 3.0, 1.0, "residential_path");
        addBidirectionalEdge("commonwealth_hall", "mensah_sarbah_hall", 200, 2.5, 1.0, "residential_path");
        addBidirectionalEdge("mensah_sarbah_hall", "volta_hall", 150, 2.0, 1.0, "residential_path");
        
        // Connect residential to academic areas
        addBidirectionalEdge("volta_hall", "admin_block", 400, 5.0, 1.3, "main_road");
        addBidirectionalEdge("legon_hall", "night_market", 300, 3.5, 1.4, "busy_path");
        addBidirectionalEdge("commonwealth_hall", "balme_library", 500, 6.0, 1.2, "campus_road");
        
        // Food and dining connections
        addBidirectionalEdge("night_market", "jqb", 400, 5.0, 1.5, "food_court_area");
        addBidirectionalEdge("jqb", "volta_hall", 200, 2.5, 1.2, "walkway");
        addBidirectionalEdge("night_market", "diaspora", 300, 3.5, 1.3, "student_area");
        
        // Bank connections
        addBidirectionalEdge("gcb_bank", "admin_block", 150, 2.0, 1.1, "service_road");
        addBidirectionalEdge("uba_bank", "gcb_bank", 100, 1.5, 1.0, "banking_area");
        addBidirectionalEdge("stanbic_atm", "balme_library", 200, 2.5, 1.1, "walkway");
        
        // Transport connections
        addBidirectionalEdge("shuttle_station", "taxi_rank", 80, 1.0, 1.2, "transport_area");
        addBidirectionalEdge("shuttle_station", "car_park_a", 150, 2.0, 1.1, "parking_road");
        addBidirectionalEdge("car_park_a", "car_park_b", 400, 5.0, 1.0, "parking_road");
        
        // Medical facility connections
        addBidirectionalEdge("medical_school", "university_hospital", 200, 2.5, 1.0, "medical_complex");
        addBidirectionalEdge("university_hospital", "car_park_b", 300, 3.5, 1.1, "hospital_road");
    }

    private void addBidirectionalEdge(String fromId, String toId, double distance, 
                                    double walkingTime, double trafficFactor, String pathType) {
        Location from = locations.get(fromId);
        Location to = locations.get(toId);
        
        if (from != null && to != null) {
            Edge edge1 = new Edge(from, to, distance, walkingTime, trafficFactor, pathType);
            Edge edge2 = new Edge(to, from, distance, walkingTime, trafficFactor, pathType);
            
            adjacencyList.get(from).add(edge1);
            adjacencyList.get(to).add(edge2);
        }
    }

    // Public methods
    public Collection<Location> getAllLocations() {
        return locations.values();
    }

    public Location getLocation(String id) {
        return locations.get(id);
    }

    public Location getLocationByName(String name) {
        return locations.values().stream()
                .filter(loc -> loc.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public List<Edge> getEdges(Location location) {
        return adjacencyList.getOrDefault(location, new ArrayList<>());
    }

    public List<Location> getLocationsByType(LocationType type) {
        return locations.values().stream()
                .filter(loc -> loc.getType() == type)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public List<Location> searchLocationsByKeyword(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return locations.values().stream()
                .filter(loc -> loc.getName().toLowerCase().contains(lowerKeyword) ||
                              loc.getDescription().toLowerCase().contains(lowerKeyword) ||
                              loc.getType().getDisplayName().toLowerCase().contains(lowerKeyword))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
}
