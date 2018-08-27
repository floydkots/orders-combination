import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ClusterTest {
    private Cluster cluster;
    private final int POINTS = 5;

    @Before
    public void setUp() {
        cluster = new Cluster(POINTS);
    }

    @Test
    public void addPoint() {
        cluster.addPoint(4);
        assertEquals(1, cluster.getPoint(4));
    }

    @Test
    public void removePoint() {
        cluster.removePoint(0);
        assertEquals(0, cluster.getPoint(0));
    }

    @Test
    public void getPoint() {
        assertEquals(1, cluster.getPoint(0));
    }

    @Test
    public void getPoints() {
        assertEquals(POINTS, cluster.getNumberOfPoints());
    }

    @Test
    public void addDepot() {
        cluster.removePoint(0);
        cluster.addDepot();
        assertEquals(1, cluster.getPoint(0));
    }

    @Test
    public void getDepot() {
        assertEquals(1, cluster.getDepot());
    }

    @Test
    public void getOrdersInVehicle() {
        cluster.addPoint(1);
        cluster.addPoint(3);
        assertEquals(3, cluster.getOrdersInCluster());
    }

    @Test
    public void copyClusterCorrectly() {
        cluster.addPoint(4);
        cluster.removePoint(3);

        Cluster clusterCopy = new Cluster(cluster);

        cluster.addPoint(3);
        clusterCopy.removePoint(0);

        assertEquals(POINTS, clusterCopy.getNumberOfPoints());
        assertEquals(1, clusterCopy.getPoint(4));
        assertEquals(0, clusterCopy.getPoint(3));
        assertEquals(1, cluster.getPoint(0));

    }
}