public class Population {
    private Individual[] individuals;
    private int[][] ordersInClusters;
    private int size;
    private int clusters;
    private int stops;
    private int points;

    public Population(int size, int clusters, int stops) {
        this.size = size;
        this.clusters = clusters;
        this.stops = stops;
        this.points = stops/2 + 1;

        initializePopulation();
    }

    private void initializePopulation() {
        individuals = new Individual[size];
        for(int count = 0; count < size; count++) {
            individuals[count] = new Individual(clusters, points);
        }
    }

    Individual getIndividual(int individual) {
        return individuals[individual];
    }

    void setIndividual(int position, Individual individual) {
        this.individuals[position] = new Individual(individual);
    }

    Cluster getCluster(int individual, int cluster) {
        return getIndividual(individual)
                .getCluster(cluster);
    }

    byte getPoint(int individual, int cluster, int point) {
        return getIndividual(individual)
                .getCluster(cluster)
                .getPoint(point);
    }

    void setPoint(int individual, int cluster, int point, int value) {
        getIndividual(individual)
                .getCluster(cluster)
                .setPoint(point, value);
    }

    private void countOrdersInClusters() {
        ordersInClusters = new int[size][clusters];
        int[][] counts = ordersInClusters;
        for (int indv = 0; indv < size; indv++) {
            for (int vhcl = 0; vhcl < clusters; vhcl++) {
                Individual individual = individuals[indv];
                Cluster cluster = individual.getCluster(vhcl);
                counts[indv][vhcl] += cluster.getOrdersInCluster();
            }
        }
    }

    public int getOrdersInCluster(int individual, int cluster) {
        countOrdersInClusters();
        return ordersInClusters[individual][cluster];
    }

    Individual correctMatrix(Individual matrix, int randomClusterA) {
        int randomCluster;
        int clusters = matrix.getNumberOfClusters();
        int points = matrix.getCluster(0).getNumberOfPoints();

        for (int cluster = 0; cluster < clusters; cluster++) {
            matrix.getCluster(cluster).addDepot();
        }

        for (int point = 1; point < points; point++) {
            int pointCount = 0;

            for (int cluster = 0; cluster < clusters; cluster++) {
                pointCount += matrix.getCluster(cluster).getPoint(point);
            }

            switch (pointCount) {

                case 0:
                    randomCluster = Util.randomInt(clusters);
                    while (randomCluster == randomClusterA) {
                        randomCluster = Util.randomInt(clusters);
                    }
                    matrix.getCluster(randomCluster).addPoint(point);
                    break;

                case 1:
                    break;

                case 2:
                    for (int cluster = 0; cluster < clusters; cluster++) {
                        if (matrix.getCluster(cluster).getPoint(point) == 1 && cluster != randomClusterA) {
                            matrix.getCluster(cluster).removePoint(point);
                        }
                    }
                    break;

                default:
                    System.out.println("Count of points: " + pointCount +
                            " Something has gone wrong during the crossover matrix correction");
            }
        }

        try {
            matrix.confirmIsLegalIndividual();
        } catch (Exception e) {
            correctMatrix(matrix, randomClusterA);
            e.printStackTrace();
        }
        return matrix;
    }


    // Choose a random point and move it to another cluster
    public void mutate(Individual offspring) {
        int i = 0;
        int out = 0;
        int numberOfClusters = offspring.getNumberOfClusters();
        int numberOfPoints = offspring.getCluster(0).getNumberOfPoints();
        int randomPoint;

        do {
            randomPoint = Util.randomInt(numberOfPoints);
        } while (randomPoint == 0);


        while (out == 0) {
            out = offspring.getCluster(i).getPoint(randomPoint);
            i++;
        }

        offspring.getCluster(i-1).removePoint(randomPoint);

        int randomCluster = Util.randomInt(numberOfClusters);
        while (randomCluster == i-1) {
            randomCluster = Util.randomInt(numberOfClusters);
        }

        offspring.getCluster(randomCluster).addPoint(randomPoint);
    }


    public Individual crossover(Individual A, Individual B, float probA) {
        int randomInt;
        int clusters = A.getNumberOfClusters();
        int points = A.getCluster(0).getNumberOfPoints();

        A.confirmIsLegalIndividual();

        Individual offspring = new Individual(A);

        // Choose a random cluster from both parents
        int randomClusterA = Util.randomInt(clusters);
        int randomClusterB = Util.randomInt(clusters);

        int[] template = new int[points];

        // Populate template based on probability of
        // choosing a gene from parent A (probA)
        for (int point = 0; point < points; point++) {
            randomInt = Util.randomInt(100);
            if (randomInt > probA - 1) {
                template[point] = 1;
            }
        }

        // RECIPE FOR CROSSING OVER
        // It template[point] == 1, then, in the offspring
        // we cross over randomClusterA's point with
        // randomClusterB's point
        for (int point = 0; point < points; point++) {
            if (template[point] == 1) {
                byte value = B.getCluster(randomClusterB).getPoint(point);
                offspring.getCluster(randomClusterA).setPoint(point, value);
            }
        }

        return correctMatrix(offspring, randomClusterA);
    }

}
