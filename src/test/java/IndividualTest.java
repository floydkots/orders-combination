import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class IndividualTest {

    private Individual individual;
    private final int CLUSTERS = 2;
    private final int POINTS = 5;

    @Before
    public void setUp() {
        individual = new Individual(CLUSTERS, POINTS);
    }

    @Test
    public void getNumberOfClusters() {
        assertEquals(CLUSTERS, individual.getNumberOfClusters());
    }

    @Test
    public void getCluster() {
        assertEquals(5, individual.getCluster(0).getNumberOfPoints());
        assertEquals(1, individual.getCluster(1).getPoint(0));
    }

    @Test
    public void uniqueAssignmentOfPoints() {
        for (int p = 1; p < POINTS; p++) {
            int assignments = 0;
            for (int v = 0; v < CLUSTERS; v++) {
                assignments += individual.getCluster(v).getPoint(p);
            }
            assertEquals(1, assignments);
        }
    }

    @Test
    public void copiesIndividualCorrectly() {
        Individual copyOfIndividual = new Individual(individual);

        for (int cluster = 0; cluster < individual.getNumberOfClusters(); cluster++) {
            byte[] expected = individual.getCluster(cluster).getClusterByteArray();
            byte[] actual = copyOfIndividual.getCluster(cluster).getClusterByteArray();
            assertArrayEquals(expected, actual);
        }

        assertEquals(CLUSTERS, copyOfIndividual.getNumberOfClusters());
        assertEquals(POINTS, copyOfIndividual.getCluster(0).getNumberOfPoints());

        individual.getCluster(1).removePoint(0);
        assertEquals(1, copyOfIndividual.getCluster(1).getDepot());

        individual.getCluster(0).addPoint(2);
        copyOfIndividual.getCluster(0).removePoint(2);
        assertEquals(0, copyOfIndividual.getCluster(0).getPoint(2));
        assertEquals(1, individual.getCluster(0).getPoint(2));
    }
}