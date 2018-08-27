public class Distance {
    private final static float AVERAGE_RADIUS_OF_EARTH_KM = 6371;
    private static Data data;
    private static float[][] distances;


    public Distance(Data data) {
        Distance.data = data;
        calculateDistances();
    }


    /**
     * Use the Haversine formula to calculate the approximate
     * distance between two stops
     * @param s1 Stop 1
     * @param s2 Stop 2
     * @return the approximate distance in Km.
     */
    private float calculateDistance(int s1, int s2) {
        float[][] coordinates = data.getCoordinates();
        float latDistance = (float) Math.toRadians(coordinates[s1][0] - coordinates[s2][0]);
        float lonDistance = (float) Math.toRadians(coordinates[s1][1] - coordinates[s2][1]);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(coordinates[s1][0])) *
                Math.cos(Math.toRadians(coordinates[s2][0])) *
                Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // return the distance rounded off to 2 decimal places
        return (float) (Math.round(AVERAGE_RADIUS_OF_EARTH_KM * c * 100.0) / 100.0);
    }


    private void calculateDistances() {
        int arraySize = data.getNumberOfStops() + 1;
        distances = new float[arraySize][arraySize];
        for (int stop1 = 0; stop1 < arraySize; stop1++) {
            for (int stop2 = 0; stop2 < arraySize; stop2++) {
                distances[stop1][stop2] = calculateDistance(stop1, stop2);
            }
        }
    }

    float getDistance(int stop1, int stop2) {
        return distances[stop1][stop2];
    }
}
