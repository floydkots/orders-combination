import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DataTest {
    private final int STOPS = 13;
    private final int ORDERS = STOPS / 2;
    private final int MAX_VEHICLES = 5;
    private final int MAX_VEHICLE_CAPACITY = 2;
    private final int MAX_CLUSTERS = MAX_VEHICLES;

    private Data data;

    @Before
    public void setUp() throws Exception {
        data = new Data("orders_1_test");
    }

    @Test
    public void getCoordinates() {
        float[][] coordinates = data.getCoordinates();
        assertEquals(STOPS, coordinates.length);
        assertEquals(2, coordinates[0].length);
    }

    @Test
    public void getOrders() {
        assertEquals(ORDERS, data.getNumberOfOrders());
    }

    @Test
    public void getMaxVehicles() {
        assertEquals(MAX_VEHICLES, data.getMaxVehicles());
    }

    @Test
    public void getMaxVehicleCapacity() {
        assertEquals(MAX_VEHICLE_CAPACITY, data.getMaxVehicleCapacity());
    }

    @Test
    public void getMaxClusters() {
        assertEquals(MAX_CLUSTERS, data.getMaxClusters());
    }
}