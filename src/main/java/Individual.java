class Individual {
    private int numberOfClusters;
    private int points;
    private int depots;

    private Cluster[] individual;

    Individual(int clusters, int points) {
        this.numberOfClusters = clusters;
        this.points = points;
        initializeIndividual();
    }

    Individual(Individual individual) {
        try {
            individual.confirmIsLegalIndividual();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        this.numberOfClusters = individual.getNumberOfClusters();
        this.points = individual.getPoints();
        this.depots = individual.getDepots();
        this.individual = new Cluster[numberOfClusters];
        for (int v = 0; v < individual.getNumberOfClusters(); v++) {
            this.individual[v] = new Cluster(individual.getCluster(v));
        }

        confirmIsLegalIndividual();
    }

    private int getPoints() {
        return points;
    }

    private int getDepots() {
        return depots;
    }

    private Cluster createCluster() {
        return new Cluster(points);
    }

    private void assignPointToRandomCluster(int point) {
        int randomCluster = Util.randomInt(numberOfClusters);
        individual[randomCluster].addPoint(point);
    }

    private void initializeIndividual() {
        individual = new Cluster[numberOfClusters];
        for (int cluster = 0; cluster < numberOfClusters; cluster++) {
            individual[cluster] = createCluster();
        }
        for (int point = 1; point < points; point++) {
            assignPointToRandomCluster(point);
        }

        confirmIsLegalIndividual();
    }

    private boolean hasCorrectDepots() {
        depots = 0;
        final int FIRST_POINT = 0;

        for (int cluster = 0; cluster < numberOfClusters; cluster++) {
            depots += individual[cluster].getDepot();
        }
        return depots == numberOfClusters;
    }

    private boolean hasUniquelyAssignedPoints() {
        boolean uniqueAssignment = true;
        for (int point = 1; point < points; point++) {
            int pointCount = 0;
            for (int cluster = 0; cluster < numberOfClusters; cluster++) {
                pointCount += individual[cluster].getPoint(point);
            }

            switch (pointCount) {
                case 0: // No point assigned
                    assignPointToRandomCluster(point);
                    break;
                case 1: // point uniquely assigned to one cluster
                    break;
                default:
                    uniqueAssignment = false;
            }
            if (!uniqueAssignment) {
                break;
            }
        }
        return uniqueAssignment;
    }

    @Override
    public String toString() {
        StringBuilder representation = new StringBuilder();
        for (int i = 0; i < numberOfClusters; i++) {
            representation.append(individual[i].toString()).append("\n");
        }
        return representation.toString();
    }

    void confirmIsLegalIndividual() {
        boolean isLegal = hasCorrectDepots() && hasUniquelyAssignedPoints();
        if (!isLegal) {
            throw new IllegalStateException("Illegal Individual!\n" + this.toString());
        }
    }

    Cluster getCluster(int cluster) {
        return individual[cluster];
    }

    int getNumberOfClusters() {
        return numberOfClusters;
    }
}
