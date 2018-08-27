import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DistanceTest {
    private static final int STOP_0 = 0;
    private static final int STOP_1 = 1;
    private static final int STOP_2 = 2;
    private static final double DISTANCE_0_1 = 535.34;
    private static final double DISTANCE_2_2 = 0.0;
    private Data data;
    private Distance distance;

    @Before
    public void setUp() throws Exception {
        data = new Data("orders_1_test");
        distance = new Distance(data);
    }

    @Test
    public void getDistance() {
        assertEquals(DISTANCE_0_1, distance.getDistance(STOP_0, STOP_1), 0);
        assertEquals(DISTANCE_0_1, distance.getDistance(STOP_1, STOP_0), 0);
        assertEquals(DISTANCE_2_2, distance.getDistance(STOP_2, STOP_2), 0);
    }
}