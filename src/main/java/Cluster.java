import java.util.Arrays;

public class Cluster {
    static final int CAPACITY = 2;

    private int points;
    private byte[] cluster;

    Cluster() { }

    Cluster(int points) {
        this.points = points;
        initializeCluster();
    }

    byte[] getClusterByteArray() {
        return cluster;
    }

    int getLength() {
        return this.cluster.length;
    }

    Cluster(Cluster otherCluster) {
        this.points = otherCluster.getNumberOfPoints();
        initializeCluster();
        this.cluster = Arrays.copyOf(otherCluster.getClusterByteArray(), cluster.length);
    }

    private void initializeCluster() {
        cluster = new byte[points];
        addDepot();
    }

    void setPoint(int point, int value) {
        cluster[point] = (byte) value;
    }

    void setPoint(int point, byte value) {
        cluster[point] = value;
    }

    void addPoint(int point) {
        setPoint(point, 1);
    }

    void removePoint(int point) {
        setPoint(point, 0);
    }

    byte getPoint(int point) {
        return cluster[point];
    }

    int getNumberOfPoints() {
        return points;
    }

    void addDepot() {
        addPoint(0);
    }

    int getDepot() {
        return getPoint(0);
    }

    int getOrdersInCluster() {
        int count = 0;
        for (int point = 0; point < points; point++) {
            count += cluster[point];
        }
        return count;
    }

    @Override
    public String toString() {
        return Arrays.toString(cluster);
    }
}
