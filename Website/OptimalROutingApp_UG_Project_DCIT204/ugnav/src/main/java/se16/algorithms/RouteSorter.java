package se16.algorithms;

import se16.model.Route;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Collection of sorting algorithms for routes
 */
public class RouteSorter {
    
    /**
     * Quick Sort implementation for routes
     */
    public static void quickSort(List<Route> routes, Comparator<Route> comparator) {
        if (routes.size() <= 1) return;
        quickSortHelper(routes, 0, routes.size() - 1, comparator);
    }
    
    private static void quickSortHelper(List<Route> routes, int low, int high, Comparator<Route> comparator) {
        if (low < high) {
            int pivotIndex = partition(routes, low, high, comparator);
            quickSortHelper(routes, low, pivotIndex - 1, comparator);
            quickSortHelper(routes, pivotIndex + 1, high, comparator);
        }
    }
    
    private static int partition(List<Route> routes, int low, int high, Comparator<Route> comparator) {
        Route pivot = routes.get(high);
        int i = low - 1;
        
        for (int j = low; j < high; j++) {
            if (comparator.compare(routes.get(j), pivot) <= 0) {
                i++;
                swap(routes, i, j);
            }
        }
        
        swap(routes, i + 1, high);
        return i + 1;
    }
    
    /**
     * Merge Sort implementation for routes
     */
    public static void mergeSort(List<Route> routes, Comparator<Route> comparator) {
        if (routes.size() <= 1) return;
        
        List<Route> temp = new ArrayList<>(routes.size());
        for (int i = 0; i < routes.size(); i++) {
            temp.add(null);
        }
        
        mergeSortHelper(routes, temp, 0, routes.size() - 1, comparator);
    }
    
    private static void mergeSortHelper(List<Route> routes, List<Route> temp, int left, int right, Comparator<Route> comparator) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            
            mergeSortHelper(routes, temp, left, mid, comparator);
            mergeSortHelper(routes, temp, mid + 1, right, comparator);
            merge(routes, temp, left, mid, right, comparator);
        }
    }
    
    private static void merge(List<Route> routes, List<Route> temp, int left, int mid, int right, Comparator<Route> comparator) {
        // Copy data to temp arrays
        for (int i = left; i <= right; i++) {
            temp.set(i, routes.get(i));
        }
        
        int i = left, j = mid + 1, k = left;
        
        while (i <= mid && j <= right) {
            if (comparator.compare(temp.get(i), temp.get(j)) <= 0) {
                routes.set(k++, temp.get(i++));
            } else {
                routes.set(k++, temp.get(j++));
            }
        }
        
        while (i <= mid) {
            routes.set(k++, temp.get(i++));
        }
        
        while (j <= right) {
            routes.set(k++, temp.get(j++));
        }
    }
    
    /**
     * Heap Sort implementation for routes
     */
    public static void heapSort(List<Route> routes, Comparator<Route> comparator) {
        int n = routes.size();
        
        // Build heap
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(routes, n, i, comparator);
        }
        
        // Extract elements from heap one by one
        for (int i = n - 1; i > 0; i--) {
            swap(routes, 0, i);
            heapify(routes, i, 0, comparator);
        }
    }
    
    private static void heapify(List<Route> routes, int n, int i, Comparator<Route> comparator) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;
        
        if (left < n && comparator.compare(routes.get(left), routes.get(largest)) > 0) {
            largest = left;
        }
        
        if (right < n && comparator.compare(routes.get(right), routes.get(largest)) > 0) {
            largest = right;
        }
        
        if (largest != i) {
            swap(routes, i, largest);
            heapify(routes, n, largest, comparator);
        }
    }
    
    /**
     * Sort routes by multiple criteria using different algorithms
     */
    public static List<Route> sortByCriteria(List<Route> routes, SortCriteria criteria, SortAlgorithm algorithm) {
        List<Route> sortedRoutes = new ArrayList<>(routes);
        Comparator<Route> comparator = getComparator(criteria);
        
        switch (algorithm) {
            case QUICK_SORT:
                quickSort(sortedRoutes, comparator);
                break;
            case MERGE_SORT:
                mergeSort(sortedRoutes, comparator);
                break;
            case HEAP_SORT:
                heapSort(sortedRoutes, comparator);
                break;
            default:
                sortedRoutes.sort(comparator);
        }
        
        return sortedRoutes;
    }
    
    private static Comparator<Route> getComparator(SortCriteria criteria) {
        switch (criteria) {
            case DISTANCE:
                return Comparator.comparingDouble(Route::getTotalDistance);
            case TIME:
                return Comparator.comparingDouble(Route::getTotalTime);
            case LANDMARKS:
                return Comparator.comparingInt(route -> route.getLandmarks().size());
            case COMBINED:
                return Comparator.comparingDouble((Route route) -> 
                    route.getTotalDistance() * 0.6 + route.getTotalTime() * 10 * 0.4);
            default:
                return Comparator.comparingDouble(Route::getTotalDistance);
        }
    }
    
    private static void swap(List<Route> routes, int i, int j) {
        Route temp = routes.get(i);
        routes.set(i, routes.get(j));
        routes.set(j, temp);
    }
    
    public enum SortCriteria {
        DISTANCE, TIME, LANDMARKS, COMBINED
    }
    
    public enum SortAlgorithm {
        QUICK_SORT, MERGE_SORT, HEAP_SORT, DEFAULT
    }
}
