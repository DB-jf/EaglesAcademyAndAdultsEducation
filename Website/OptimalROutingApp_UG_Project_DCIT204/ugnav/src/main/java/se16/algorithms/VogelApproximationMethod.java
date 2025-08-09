package se16.algorithms;

import se16.model.*;
import java.util.*;

/**
 * Implementation of Vogel's Approximation Method for transportation optimization
 * Applied to campus navigation for optimizing routes based on demand and capacity
 */
public class VogelApproximationMethod {
    
    /**
     * Represents a transportation problem for campus routing
     */
    public static class TransportationProblem {
        private final List<Location> sources;
        private final List<Location> destinations;
        private final double[][] costs;
        private final double[] supply;
        private final double[] demand;
        
        public TransportationProblem(List<Location> sources, List<Location> destinations,
                                   double[][] costs, double[] supply, double[] demand) {
            this.sources = sources;
            this.destinations = destinations;
            this.costs = costs;
            this.supply = supply;
            this.demand = demand;
        }
        
        public List<Location> getSources() { return sources; }
        public List<Location> getDestinations() { return destinations; }
        public double[][] getCosts() { return costs; }
        public double[] getSupply() { return supply; }
        public double[] getDemand() { return demand; }
    }
    
    /**
     * Represents an allocation in the transportation solution
     */
    public static class Allocation {
        private final Location source;
        private final Location destination;
        private final double quantity;
        private final double cost;
        
        public Allocation(Location source, Location destination, double quantity, double cost) {
            this.source = source;
            this.destination = destination;
            this.quantity = quantity;
            this.cost = cost;
        }
        
        public Location getSource() { return source; }
        public Location getDestination() { return destination; }
        public double getQuantity() { return quantity; }
        public double getCost() { return cost; }
        public double getTotalCost() { return quantity * cost; }
    }
    
    /**
     * Solve transportation problem using Vogel's Approximation Method
     */
    public static List<Allocation> solve(TransportationProblem problem) {
        int m = problem.getSources().size();
        int n = problem.getDestinations().size();
        
        // Create working copies
        double[][] costs = deepCopy(problem.getCosts());
        double[] supply = Arrays.copyOf(problem.getSupply(), m);
        double[] demand = Arrays.copyOf(problem.getDemand(), n);
        
        List<Allocation> allocations = new ArrayList<>();
        boolean[] rowEliminated = new boolean[m];
        boolean[] colEliminated = new boolean[n];
        
        while (!allEliminated(rowEliminated) || !allEliminated(colEliminated)) {
            // Calculate penalties for rows and columns
            double[] rowPenalties = calculateRowPenalties(costs, rowEliminated, colEliminated);
            double[] colPenalties = calculateColumnPenalties(costs, rowEliminated, colEliminated);
            
            // Find maximum penalty
            int maxPenaltyRow = -1, maxPenaltyCol = -1;
            double maxPenalty = -1;
            boolean isRowPenalty = true;
            
            for (int i = 0; i < m; i++) {
                if (!rowEliminated[i] && rowPenalties[i] > maxPenalty) {
                    maxPenalty = rowPenalties[i];
                    maxPenaltyRow = i;
                    isRowPenalty = true;
                }
            }
            
            for (int j = 0; j < n; j++) {
                if (!colEliminated[j] && colPenalties[j] > maxPenalty) {
                    maxPenalty = colPenalties[j];
                    maxPenaltyCol = j;
                    isRowPenalty = false;
                }
            }
            
            // Make allocation based on maximum penalty
            if (isRowPenalty && maxPenaltyRow != -1) {
                int minCostCol = findMinCostInRow(costs[maxPenaltyRow], colEliminated);
                makeAllocation(problem, allocations, maxPenaltyRow, minCostCol, 
                             supply, demand, rowEliminated, colEliminated);
            } else if (maxPenaltyCol != -1) {
                int minCostRow = findMinCostInColumn(costs, maxPenaltyCol, rowEliminated);
                makeAllocation(problem, allocations, minCostRow, maxPenaltyCol, 
                             supply, demand, rowEliminated, colEliminated);
            } else {
                // Fallback: find any remaining allocation
                for (int i = 0; i < m; i++) {
                    if (!rowEliminated[i]) {
                        for (int j = 0; j < n; j++) {
                            if (!colEliminated[j]) {
                                makeAllocation(problem, allocations, i, j, 
                                             supply, demand, rowEliminated, colEliminated);
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        }
        
        return allocations;
    }
    
    private static double[] calculateRowPenalties(double[][] costs, boolean[] rowEliminated, boolean[] colEliminated) {
        int m = costs.length;
        double[] penalties = new double[m];
        
        for (int i = 0; i < m; i++) {
            if (rowEliminated[i]) {
                penalties[i] = -1;
                continue;
            }
            
            List<Double> availableCosts = new ArrayList<>();
            for (int j = 0; j < costs[i].length; j++) {
                if (!colEliminated[j]) {
                    availableCosts.add(costs[i][j]);
                }
            }
            
            if (availableCosts.size() >= 2) {
                Collections.sort(availableCosts);
                penalties[i] = availableCosts.get(1) - availableCosts.get(0);
            } else if (availableCosts.size() == 1) {
                penalties[i] = availableCosts.get(0);
            } else {
                penalties[i] = 0;
            }
        }
        
        return penalties;
    }
    
    private static double[] calculateColumnPenalties(double[][] costs, boolean[] rowEliminated, boolean[] colEliminated) {
        int n = costs[0].length;
        double[] penalties = new double[n];
        
        for (int j = 0; j < n; j++) {
            if (colEliminated[j]) {
                penalties[j] = -1;
                continue;
            }
            
            List<Double> availableCosts = new ArrayList<>();
            for (int i = 0; i < costs.length; i++) {
                if (!rowEliminated[i]) {
                    availableCosts.add(costs[i][j]);
                }
            }
            
            if (availableCosts.size() >= 2) {
                Collections.sort(availableCosts);
                penalties[j] = availableCosts.get(1) - availableCosts.get(0);
            } else if (availableCosts.size() == 1) {
                penalties[j] = availableCosts.get(0);
            } else {
                penalties[j] = 0;
            }
        }
        
        return penalties;
    }
    
    private static int findMinCostInRow(double[] row, boolean[] colEliminated) {
        int minIndex = -1;
        double minCost = Double.MAX_VALUE;
        
        for (int j = 0; j < row.length; j++) {
            if (!colEliminated[j] && row[j] < minCost) {
                minCost = row[j];
                minIndex = j;
            }
        }
        
        return minIndex;
    }
    
    private static int findMinCostInColumn(double[][] costs, int col, boolean[] rowEliminated) {
        int minIndex = -1;
        double minCost = Double.MAX_VALUE;
        
        for (int i = 0; i < costs.length; i++) {
            if (!rowEliminated[i] && costs[i][col] < minCost) {
                minCost = costs[i][col];
                minIndex = i;
            }
        }
        
        return minIndex;
    }
    
    private static void makeAllocation(TransportationProblem problem, List<Allocation> allocations,
                                     int row, int col, double[] supply, double[] demand,
                                     boolean[] rowEliminated, boolean[] colEliminated) {
        double allocation = Math.min(supply[row], demand[col]);
        
        if (allocation > 0) {
            allocations.add(new Allocation(
                problem.getSources().get(row),
                problem.getDestinations().get(col),
                allocation,
                problem.getCosts()[row][col]
            ));
            
            supply[row] -= allocation;
            demand[col] -= allocation;
            
            if (supply[row] == 0) {
                rowEliminated[row] = true;
            }
            if (demand[col] == 0) {
                colEliminated[col] = true;
            }
        }
    }
    
    private static boolean allEliminated(boolean[] eliminated) {
        for (boolean b : eliminated) {
            if (!b) return false;
        }
        return true;
    }
    
    private static double[][] deepCopy(double[][] original) {
        double[][] copy = new double[original.length][];
        for (int i = 0; i < original.length; i++) {
            copy[i] = Arrays.copyOf(original[i], original[i].length);
        }
        return copy;
    }
    
    /**
     * Create a transportation problem from campus routing data
     */
    public static TransportationProblem createCampusTransportationProblem(
            List<Location> sources, List<Location> destinations, 
            double[] sourceCapacity, double[] destinationDemand) {
        int m = sources.size();
        int n = destinations.size();
        double[][] costs = new double[m][n];
        
        // Calculate costs based on distances
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                costs[i][j] = sources.get(i).distanceTo(destinations.get(j));
            }
        }
        
        return new TransportationProblem(sources, destinations, costs, sourceCapacity, destinationDemand);
    }
}
