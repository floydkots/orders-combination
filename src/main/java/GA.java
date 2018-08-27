import java.io.IOException;
import java.util.Arrays;

public class GA {


    public static void main (String[] args) throws IOException {
        long cpuTime = System.currentTimeMillis();
        long routeTime = 0;
        long runningTime;

        // The algorithm's parameters
        String fileName = "orders/6_orders.txt";
        final int POPULATION_SIZE = 50;
        final int GENERATIONS = 15000;

        int worstIndividuals = (int) POPULATION_SIZE / 10;
        if (worstIndividuals == 0) {
            worstIndividuals = 1;
        }

        System.out.println("Worst Individuals: " + worstIndividuals);
        System.out.println("Population: " + POPULATION_SIZE);
        System.out.println("Generations: " + GENERATIONS);
        System.out.println("File: " + fileName);


        final float MUTATION_RATE = 1;
        final float BIG_NUMBER = Float.MAX_VALUE;
        float parent1GenesProbability = 60;

        // Initialize Data
        Data data = new Data(fileName);
        Route route = new Route(data);


        Population population = new Population(POPULATION_SIZE, data.getMaxClusters(), data.getNumberOfStops());
        Population population1 = new Population(POPULATION_SIZE, data.getMaxClusters(), data.getNumberOfStops());

        // Evaluate initial population
        Cluster cluster;
        float individualCost;
        float[] populationCosts = new float[POPULATION_SIZE];
        float[] populationCosts1 = new float[POPULATION_SIZE];

        // Get cost of visiting node for each individual
        // in the population
        for (int p = 0; p < POPULATION_SIZE; p++) {
            individualCost = 0;
            for (int v = 0; v < data.getMaxVehicles(); v++) {
                cluster = population.getIndividual(p).getCluster(v);
                runningTime = System.currentTimeMillis();
                route.getOrders(cluster);
                individualCost += route.getFinalCost();
                runningTime = System.currentTimeMillis() - runningTime;
                routeTime += runningTime;
            }
            populationCosts[p] = individualCost;
        }


        // The Genetic Algorithm, iterations = GENERATIONS;
        Individual offspring;
        float offspringCost;
        float mutationCost = Float.MAX_VALUE;

        for (int gen = 0; gen < GENERATIONS; gen++) {
            if(gen == 6000) {
                for (int p = 0; p < POPULATION_SIZE; p++) {
                    populationCosts1[p] = populationCosts[p];
                    for (int point = 1; point < data.getNumberOfPoints(); point++) {
                        int sum = 0;
                        int t = -1;
                        int[] ones = new int[data.getMaxVehicles()];

                        for(int v = 0; v < data.getMaxVehicles(); v++) {
                            byte value = population.getPoint(p, v, point);
                            population1.setPoint(p, v, point, value);

                            if (population.getPoint(p, v, point) == 0) {
                                population.setPoint(p, v, point, 1);
                                t++;
                                ones[t] = v;
                            } else {
                                population.setPoint(p, v, point, 0);
                            }
                            sum += population.getPoint(p, v, point);
                        }

                        if (sum < 1) {
                            int randomCluster = Util.randomInt(data.getMaxClusters());
                            population.setPoint(p, randomCluster, point, 1);
                        }

                        if (sum > 1) {
                            for (int s = 1; s < sum; s++) {
                                int randomInt = Util.randomInt(sum);
                                while(ones[randomInt] == -1) {
                                    randomInt = Util.randomInt(sum);
                                }
                                population.setPoint(p, ones[randomInt], point, 0);
                                ones[randomInt] = -1;
                            }
                        }
                    }

                    // Evaluate the new mutated population
                    individualCost = 0;

                    Cluster line;
                    for (int v = 0; v < data.getMaxVehicles(); v++) {
                        line = new Cluster(population.getCluster(p, v));
                        runningTime = System.currentTimeMillis();
                        route.getOrders(line);
                        individualCost = individualCost + route.getFinalCost();
                        runningTime = System.currentTimeMillis() - runningTime;
                        routeTime = runningTime + routeTime;
                    }

                    populationCosts[p] = individualCost;
                }
            }


            if (gen == 12000) {
                float[] popCosts1 = new float[populationCosts1.length];
                float[] popCosts2 = new float[populationCosts.length];
                for (int pop = 0; pop < populationCosts1.length; pop++) {
                    popCosts1[pop] = populationCosts1[pop];
                    popCosts2[pop] = populationCosts[pop];
                }

                int[] cost1 = Util.bubble(popCosts1);
                int[] cost2 = Util.bubble(popCosts2);

                for (int t2 = POPULATION_SIZE/2; t2 < cost2.length; t2++) {
                    int t1 = t2 - POPULATION_SIZE/2;
                    populationCosts[cost2[t2]] = populationCosts[cost1[t1]];
                    population.setIndividual(cost2[t2], population1.getIndividual(cost1[t1]));
                }
            }

            // Select individual stochastically from
            // population as parent 1
            int randomInt = Util.randomInt(100000000);
            int parent1 = getParentA(POPULATION_SIZE, populationCosts, randomInt);

            // Select random individual from population as
            // parent 2, different from parent 1
            int parent2;
            do {
                parent2 = Util.randomInt(POPULATION_SIZE);
            } while(parent2 == parent1);

            // If parent2 is better than parent1 then switch
            if (populationCosts[parent1] > populationCosts[parent2]) {
                int parentx;
                parentx = parent1;
                parent1 = parentx;
                parent2 = parentx;
            }

            Individual p1 = population.getIndividual(parent1);
            Individual p2 = population.getIndividual(parent2);

            p1.confirmIsLegalIndividual();

            // Generate offspring then crossover parent 1 and 2
            offspring = population.crossover(p1, p2, parent1GenesProbability);

            // Evaluate offspring
            offspringCost = 0;
            Cluster line;
            for (int v = 0; v < data.getMaxVehicles(); v++) {
                line = new Cluster(offspring.getCluster(v));
                runningTime = System.currentTimeMillis();
                route.getOrders(line);
                offspringCost = offspringCost + route.getFinalCost();
                runningTime = System.currentTimeMillis() - runningTime;
                routeTime = runningTime + routeTime;
            }

            // If offspring's cost of visiting nodes is the
            // same as parent 1's then mutate
            int key = 0;
            if (Math.abs(offspringCost - populationCosts[parent1]) < 1E-5f) {
                key = 80;
            }

            // Mutation takes place with probability pm
            randomInt = Util.randomInt(100);
            if (randomInt >= 0 && randomInt <= MUTATION_RATE || key == 80) {
                key = 0;
                population.mutate(offspring);

                mutationCost = 0;
                // Evaluate mutated offspring
                for (int v = 0; v < data.getMaxVehicles(); v++) {
                    line = new Cluster(offspring.getCluster(v));
                    runningTime = System.currentTimeMillis();
                    route.getOrders(line);
                    mutationCost = mutationCost + route.getFinalCost();
                    runningTime = System.currentTimeMillis() - runningTime;
                    routeTime = runningTime + routeTime;
                }
            }

            if (mutationCost < Float.MAX_VALUE) {
                offspringCost = mutationCost;
            }
            mutationCost = Float.MAX_VALUE;

            // Insert the offspring to random individual
            // among the worst population
            // Z is proportional to M

            float[] worstCosts = new float[worstIndividuals];

            // Get costs of worst individuals
            int[] wno = new int[worstIndividuals];

            // Indices of corresponding individuals
            for (int z = 0; z < worstCosts.length; z++) {
                worstCosts[z] = populationCosts[z];
                wno[z] = z;
            }

            // smallest is the index of the smallest element in worst
            int smallest = findSmallest(worstCosts, POPULATION_SIZE);
            for (int w = worstIndividuals; w < populationCosts.length; w++) {
                if (worstCosts[smallest] < populationCosts[w]) {
                    worstCosts[smallest] = populationCosts[w];
                    wno[smallest] = w;
                }
                smallest = findSmallest(worstCosts, POPULATION_SIZE);
            }

            // Choose random individual among the worst with
            // the highest cost of visiting node
            int random = Util.randomInt(wno.length);
            int b = wno[random];

            // Offspring replaces the chosen individual in
            // the current population
            populationCosts[b] = offspringCost;
            try {
                offspring.confirmIsLegalIndividual();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            population.setIndividual(b, offspring);
        }

        System.out.println("Number of Orders: " + data.getNumberOfOrders());

        int smallest = findSmallest(populationCosts, POPULATION_SIZE);
        float[] distances = new float[data.getMaxVehicles()];
        int[][] routes = new int[data.getMaxVehicles()][data.getNumberOfPoints()];

        for(int v = 0; v < data.getMaxVehicles(); v++) {
            Cluster line = population.getCluster(smallest, v);
            route.getOrders(line);

            distances[v] = route.getTotalRouteDistance();
            routes[v] = route.getRoute();
        }

        System.out.println();
        System.out.println("Distances and Routes per Vehicle");
        System.out.println("================================================");
        System.out.println();

        float totalDist = 0;
        for (int v = 0; v < distances.length; v++) {
            System.out.println("------------------------------------------------");
            System.out.println("Vehicle " + v);
            System.out.println("Distance: " + distances[v] + " Km");
            System.out.println("Route: " + Arrays.toString(routes[v]));
            System.out.println("------------------------------------------------");
            System.out.println();

            totalDist += distances[v];
        }
        System.out.println();
        System.out.println("Total distance: " + totalDist + " Km");
        System.out.println("Total Cost: " + totalDist * data.DISTANCE_WEIGHT);
    }


    public static int findSmallest(float[] vector, int size) {
        int smallest = 2 * size;
        int item;
        float small1 = Float.MAX_VALUE;
        float small2;
        for (item = 0; item < vector.length; item++) {
            small2 = small1;
            small1 = Math.min(small1, vector[item]);
            if (small1 != small2) {
                smallest = item;
            }
        }
        return smallest;
    }


    public static int getParentA(int populationSize, float[] populationCosts, int randomInt) {
        // Select individual stochastically from population
        // as parent 1
        float S = 0;
        int B = 100000000;

        for (int p = 0; p < populationSize; p++) {
            S = S + 1 / populationCosts[p];
        }

        int[] probability = new int[populationSize];

        // probability * B of selecting individual 0 as parent A
        probability[0] = (int) (B / (populationCosts[0] * S));

        // Default parent if no parent is chosen because of
        // numeration errors
        int parent1 = populationSize - 1;

        for (int p = 1; p < populationSize; p++) {
            probability[p] = probability[p-1] + (int) (B / (populationCosts[p] * S));
        }

        if (randomInt < probability[0]) {
            parent1 = 0;
        }

        for (int p = 1; p < populationSize; p++) {
            if (randomInt >= probability[p-1] && randomInt < probability[p]) {
                parent1 = p;
            }
        }
        return parent1;
    }
}
