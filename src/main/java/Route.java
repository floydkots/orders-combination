public class Route {
    private int ordersInCluster;
    private int slotsInCluster;

    private final int NO_NODE = -2;

    private int[] orderOfOrdersInRoute;
    private int[] ordersInVehicleAfterServicingNode;
    private int[] orderPositionsInCluster;

    private int[] ordersServed;
    private int[] ordersNotYetServed;
    private int[] ordersInVehicle;

    private float[] currentDistance;

    private Distance distance;
    private Data data;

    public Route(Data data) {
        this.data = data;
        this.distance = new Distance(data);
    }

    public float getTotalRouteDistance() {
        float dist = 0;
        int sLength = orderOfOrdersInRoute.length - 1;
        int[] sOrders = orderOfOrdersInRoute;

        for (int len = 0; len < sLength; len++) {
            dist += distance.getDistance(sOrders[len], sOrders[len + 1]);
        }
        return dist;
    }

    int[] getRoute() {
        return orderOfOrdersInRoute;
    }

    int[] getNumberOfOrdersInVehicleAfterServicingNode() {
        return ordersInVehicleAfterServicingNode;
    }

    public void getOrders(Cluster cluster) {
        // Number of orders in the cluster
        ordersInCluster = 0;

        for (int point = 1; point < cluster.getNumberOfPoints(); point++) {
            ordersInCluster += cluster.getPoint(point);
        }

        // Total number of slots in the cluster
        slotsInCluster = cluster.getLength() - 1;

        // If there are any orders in the cluster, then
        // continue, otherwise return the depot to depot route
        if (ordersInCluster > 0) {
            orderPositionsInCluster = new int[ordersInCluster];
            int count = 0;
            for (int point = 1; point < cluster.getNumberOfPoints(); point++) {
                if (cluster.getPoint(point) == 1) {
                    orderPositionsInCluster[count] = point;
                    count++;
                }
            }
            route();
        } else {
            orderOfOrdersInRoute = new int[2];
        }
    }


    /**
     * Generate route for a cluster.
     *
     * The algorithm is a greedy, modified version of the
     * nearest neighbour search algorithm
     */
    public void route() {
        int routeStops = 2 * ordersInCluster + 2;
        orderOfOrdersInRoute = new int[routeStops];
        ordersInVehicleAfterServicingNode = new int[routeStops];
        currentDistance = new float[routeStops];

        int nextNode = -1;
        int currentNode = 0;
        float minimumCost;

        int sumOfOrdersServed = 0;
        int sumOfOrdersNotYetServed = 0;
        int sumOfOrdersInCluster = 0;

        int[] firstOrder = new int[orderPositionsInCluster.length];
        int[] firstPosition = new int[orderPositionsInCluster.length];

        ordersServed = new int[ordersInCluster];
        ordersNotYetServed = new int[ordersInCluster];
        ordersInVehicle = new int[ordersInCluster];

        // 4 closest nodes to be considered as next node.
        int[] nearest4 = new int[4];


        // Find order in orderPositionsInCluster of the
        // shortest distance from the depot and set it as the
        // first order.
        int firstOrderNumber = -5;
        int firstOrderPosition = -5;
        float minDistance = Float.MAX_VALUE;

        float[] distances = new float[orderPositionsInCluster.length];

        for (int pos = 0; pos < orderPositionsInCluster.length; pos++) {
            distances[pos] = distance.getDistance(0, orderPositionsInCluster[pos]);
            if (minDistance > distances[pos]) {
                minDistance = distances[pos];
                firstOrderPosition = pos;
                firstOrderNumber = orderPositionsInCluster[pos];
            }
        }

        // -1 indicates no order for ordersServed and ordersInVehicle
        // All order numbers set into ordersNotYetServed
        for (int pos = 0; pos < orderPositionsInCluster.length; pos++) {
            ordersNotYetServed[pos] = orderPositionsInCluster[pos];
            ordersServed[pos] = -1;
            ordersInVehicle[pos] = -1;
        }

        // Add first order to vehicle
        ordersInVehicle[firstOrderPosition] = firstOrderNumber;
        ordersNotYetServed[firstOrderPosition] = -1;

        // Start at depot
        orderOfOrdersInRoute[0] = 0;
        // Then to first order
        orderOfOrdersInRoute[1] = firstOrderNumber;
        ordersInVehicleAfterServicingNode[0] = getLoadChange(0);
        ordersInVehicleAfterServicingNode[1] = ordersInVehicleAfterServicingNode[0] + getLoadChange(firstOrderNumber);

        // Calculate sums and use them as stopping criteria
        // in while loop

        for (int i = 0; i < ordersServed.length; i++) {
            sumOfOrdersServed += ordersServed[i];
            sumOfOrdersNotYetServed += ordersNotYetServed[i];
            sumOfOrdersInCluster += orderPositionsInCluster[i];
        }

        // Set current node to depot, next node to first order
        currentNode = 0;
        nextNode = firstOrderNumber;

        // Update the current distance
        currentDistance[0] = 0;
        currentDistance[1] = currentDistance[0] + distance.getDistance(currentNode, nextNode);

        // Set current node to first order
        currentNode = nextNode;

        // Counter in while loop, used with
        // orderOfOrdersInRoute and currentDistance
        int counter = 1;


        while (sumOfOrdersNotYetServed > -ordersNotYetServed.length || sumOfOrdersServed < sumOfOrdersInCluster) {
            int countOfErrors = 0;

            for (int i = 0; i < nearest4.length; i++) {
                nearest4[i] = -1;
            }

            for(int i = 0; i < nearest4.length; i++) {
                // Find a node that is closest to the
                // currentNode but hasn't been included in
                // the nearest4
                if (counter < currentDistance.length) {
                    nextNode = getClosest(currentNode, nearest4);
                } else {
                    System.out.println("Current Distance has a problem");
                }

                if (nextNode == NO_NODE && i == 0) {
                    System.out.println("KURT???");
                }

                if (nextNode == NO_NODE) {
                    break;
                } else {
                    // set nextNode into nearest4
                    int count = 0;
                    for (int j = 0; j < nearest4.length; j++) {
                        if (nearest4[j] != -1) {
                            count++;
                        }
                    }
                    nearest4[count] = nextNode;
                }
            }


            minimumCost = Float.MAX_VALUE;
            // Evaluate the cost of visiting the nearest 4
            // nodes, and choose the cheapest option
            for (int i = 0; i < nearest4.length; i++)  {
                if (nearest4[i] > -1) {
                    if (minimumCost > getCostOfVisitingNode(nearest4[i], currentNode)) {
                        minimumCost = getCostOfVisitingNode(nearest4[i], currentNode);
                        nextNode = nearest4[i];
                    }
                }
            }

            // At this point something has gone wrong.
            // The algorithm should never have nextNode == NO_NODE.
            // It should have stopped before
            if (nextNode == NO_NODE) {
                System.out.println("BREAK");
                break;
            }

            counter++;
            visit(nextNode);
            if (counter < currentDistance.length) {
                currentDistance[counter] = currentDistance[counter - 1] + distance.getDistance(currentNode, nextNode);
            }

            currentNode = nextNode;
            ordersInVehicleAfterServicingNode[counter] = ordersInVehicleAfterServicingNode[counter-1] + getLoadChange(currentNode);

            // At this point, the while loop has run more
            // times than there are stops
            if (counter > orderOfOrdersInRoute.length) {
                System.out.println("NB! Extra Loop, currentNode = " + currentNode);
                countOfErrors++;
            }

            // If okay, we add the current node into the route
            if (counter < orderOfOrdersInRoute.length - 1 && currentNode > 0) {
                orderOfOrdersInRoute[counter] = currentNode;
            } else {
                System.out.println("Error in Route at node = " + currentNode);
            }

            if (countOfErrors > 0) {
                System.out.println("Before: \n" +
                        "Sum of orders served: " + sumOfOrdersServed + " \n" +
                        "Sum of orders not yet served: " + sumOfOrdersNotYetServed);
            }

            // Recalculate the stopping criteria
            sumOfOrdersServed = 0;
            sumOfOrdersNotYetServed = 0;
            for (int i = 0; i < ordersServed.length; i++) {
                sumOfOrdersServed += ordersServed[i];
                sumOfOrdersNotYetServed += ordersNotYetServed[i];
            }

            if (countOfErrors > 0) {
                System.out.println("After: \n" +
                        "Sum of orders served: " + sumOfOrdersServed + "\n" +
                        "Sum of orders not yet served: " + sumOfOrdersNotYetServed);

                System.out.println("Sum of orders in cluster " + sumOfOrdersInCluster + "\n" +
                        "ordersNotYetServed.length = " + ordersNotYetServed.length);
            }
        }
    }


    /**
     * Get the node that is closest to currentNode
     * @param currentNode
     * @param nearestNodes
     * @return
     */
    public int getClosest(int currentNode, int[] nearestNodes) {
        float minimumDist = Float.MAX_VALUE;
        float newMinimumDist;

        int closestNode = NO_NODE;
        int count;

        // destination of c not in nearestNodes
        for (int c = 0; c < ordersInVehicle.length; c++) {
            if (ordersInVehicle[c] != -1) {
                count = 0;
                for (int j = 0; j < nearestNodes.length; j++) {
                    if (nearestNodes[j] != ordersInVehicle[c] + slotsInCluster) {
                        count++;
                    }
                }

                if (count == nearestNodes.length) {
                    newMinimumDist = getSeparation(currentNode, ordersInVehicle[c] + slotsInCluster);
                    if (minimumDist > newMinimumDist) {
                        minimumDist = newMinimumDist;
                        closestNode = ordersInVehicle[c] + slotsInCluster;
                    }
                }

            }
        }


        // Now we assume that the orders are moved alone
        // i.e demand in each node is 1 or -1
        count = 0;
        for (int c = 0; c < ordersInVehicle.length; c++) {
            if (ordersInVehicle[c] != -1) {
                count++;
            }
        }

        if (count == data.getMaxVehicleCapacity()) {
            return closestNode;
        }

        if (count > data.getMaxVehicleCapacity()) {
            System.out.println("Vehicle capacity violated.");
            return closestNode;
        }


        // Origin of c not in nearestNodes
        for (int c = 0; c < ordersNotYetServed.length; c++) {
            if (ordersNotYetServed[c] != -1) {
                count = 0;
                for (int j = 0; j < nearestNodes.length; j++) {
                    if (nearestNodes[j] != ordersNotYetServed[c]) {
                        count++;
                    }
                }

                if (count == nearestNodes.length) {
                    newMinimumDist = getSeparation(currentNode, ordersNotYetServed[c]);
                    if (minimumDist > newMinimumDist) {
                        minimumDist = newMinimumDist;
                        closestNode = ordersNotYetServed[c];
                    }
                }
            }
        }
        return closestNode;
    }


    /**
     * Return the distance separation between currentNode and
     * nextNode.
     * @param currentNode
     * @param nextNode
     * @return
     */
    public float getSeparation(int currentNode, int nextNode) {
        return distance.getDistance(currentNode, nextNode);
    }



    public float getCostOfVisitingNode(int nextNode, int currentNode) {
        int[] visitedNodes = new int[4];
        int[] nearestNodes = new int[4];

        int cc = 0;
        float totalCost = 0;

        for (int i = 0; i < 4; i++) {
            visitedNodes[i] = -1;
            nearestNodes[i] = -1;
        }

        for (int i = 0; i < 4; i++) {
            totalCost += getMoveCost(currentNode, nextNode);
            if (nextNode > 0) {
                visit(nextNode);
            }
            visitedNodes[i] = nextNode;
            currentNode = nextNode;

            nextNode = getClosest(currentNode, nearestNodes);

            if (nextNode == NO_NODE) {
                cc++;
                if (cc == 1) {
                    nextNode = 0;
                    totalCost = totalCost + getMoveCost(currentNode, nextNode);
                } else {
                    break;
                }
            }
        }

        for (int i = visitedNodes.length - 1; i >= 0; i--) {
            if (visitedNodes[i] > 0) {
                unvisit(visitedNodes[i]);
            }
        }

        return totalCost;
    }



    /**
     * Cost of moving from current node to next node
     * @param currentNode
     * @param nextNode
     * @return
     */
    public float getMoveCost(int currentNode, int nextNode) {
        return data.DISTANCE_WEIGHT * distance.getDistance(currentNode, nextNode);
    }


    public float getCostOfRoute(int[] route) {
        int currentNode;
        int nextNode;

        for (int i = 0; i < ordersInVehicle.length; i++) {
            ordersInVehicle[i] = -1;
            ordersServed[i] = -1;
            ordersNotYetServed[i] = orderPositionsInCluster[i];
        }

        ordersInVehicleAfterServicingNode[0] = 0;

        currentNode = route[0];
        for (int i = 0; i < currentDistance.length; i++) {
            currentDistance[i] = 0;
        }

        // update distance, ordersInVehicleAfterServicingNode
        for (int i = 1; i < route.length; i++) {
            nextNode = route[i];
            ordersInVehicleAfterServicingNode[i] = ordersInVehicleAfterServicingNode[i-1] + getLoadChange(route[i]);
            currentDistance[i] = currentDistance[i-1] + distance.getDistance(currentNode, nextNode);

            currentNode = nextNode;
            if (currentNode > 0) {
                visit(currentNode);
            }
        }

        // TODO Find way to simplify, deduplicate
        assert currentDistance[currentDistance.length - 1] == getTotalRouteDistance();

        return getTotalRouteDistance() * data.DISTANCE_WEIGHT;
    }


    private int getLoadChange(int node) {
        if (node == 0) return 0; // At depot, no load change
        if (node > slotsInCluster) return -1; // drop-off
        return 1; // pick-up
    }


    public void visit(int gnode) {
        for (int c = 0; c < orderPositionsInCluster.length; c++) {
            if (gnode == orderPositionsInCluster[c]) {
                ordersNotYetServed[c] = -1;
                ordersInVehicle[c] = orderPositionsInCluster[c];
                break;
            }

            if (gnode == orderPositionsInCluster[c] + slotsInCluster) {
                ordersServed[c] = orderPositionsInCluster[c];
                ordersInVehicle[c] = -1;
                break;
            }
        }
    }


    public void unvisit(int gnode) {
        for (int c = orderPositionsInCluster.length - 1; c >= 0; c--) {
            if (gnode == orderPositionsInCluster[c]) {
                ordersNotYetServed[c] = orderPositionsInCluster[c];
                ordersInVehicle[c] = -1;
                break;
            }

            if (gnode == orderPositionsInCluster[c] + slotsInCluster) {
                ordersServed[c] = -1;
                ordersInVehicle[c] = orderPositionsInCluster[c];
                break;
            }
        }
    }


    float getFinalCost() {
        if (orderOfOrdersInRoute.length > 2) {
            return getCostOfRoute(orderOfOrdersInRoute);
        }
        return 0;
    }
}
