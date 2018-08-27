import java.io.IOException;

public class Data {
    private int stops;
    private float[][] coordinates;
    private final int MAX_VEHICLES = 5;
    private final int MAX_VEHICLE_CAPACITY = 2;
    private final int MAX_CLUSTERS = MAX_VEHICLES;

    public Data(String fileName) throws IOException {
        Reader reader = new Reader(fileName);
        this.stops = reader.getStops();
        this.coordinates = reader.getCoordinates();
    }

    float[][] getCoordinates() {
        return coordinates;
    }

    int getNumberOfStops() {
        // Stops should be at least 2 i.e.
        // starting and stopping at the depot
        return stops > 2 ? stops : 2;
    }

    int getNumberOfPoints() {
        return (getNumberOfStops() / 2) + 1;
    }

    int getNumberOfOrders() {
        return (getNumberOfStops() - 1) / 2;
    }

    int getMaxClusters() {
        return MAX_CLUSTERS;
    }

    int getMaxVehicles() {
        return MAX_VEHICLES;
    }

    int getMaxVehicleCapacity() {
        return MAX_VEHICLE_CAPACITY;
    }
}
