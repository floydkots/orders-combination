class Util {

    /**
     * Get a random integer value between 0 and Max - 1
     * @param Max Upper bound of the random int's interval
     * @return a random integer on the interval [0, Max-1]
     */
    public static int randomInt(int Max) {
        return (int)(Math.random() * Max);
    }

    // Bubble sort the matrix
    public static int [] bubble(float[] matrix) {
        int[] place = new int[matrix.length];
        for (int t = 0; t < place.length; t++) {
            place[t] = t;
        }

        for (int t1 = matrix.length - 1; t1 > 0; t1--) {
            for (int t2 = 0; t2 < t1; t2++) {
                if (matrix[t2] > matrix[t2 + 1]) {
                    float temp = matrix[t2];
                    int tplace = place[t2];
                    matrix[t2] = matrix[t2+1];
                    matrix[t2+1] = temp;
                    place[t2] = place[t2 + 1];
                    place[t2+1] = tplace;
                }
            }
        }

        return place;
    }
}
