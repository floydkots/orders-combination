# Orders Combination

## Task Description
A company providing on-demand delivery services needs to find opportunities for combining
delivery orders such that its customers can share cost and drivers and riders on their platform
can maximise their earnings by doing more deliveries at a lower fuel cost.

Consider two orders A and B. Order A’s pickup location is X and drop-off is Y. Order B’s pickup
is at location X and drop-off at Z. One can get to Z via Y. Say the distance from X to Y is 5 km, Z
is 3 km from Y, and the order cost is KSh 30/km. Also, the two orders are placed within a short
span of each other. In this example assume order B is placed a minute after order A. These
orders can be combined in a way that a partner picks up both at X, delivers A at Y then
proceeds to deliver B at Z.

Given the location coordinates for each order (e.g., -1.300176, 36.776714), design and
implement an algorithm in Java that demonstrates how the company can find such orders.
Come up with a general solution that can find all possible order combination opportunities. Also
bear in mind that paths for such orders will not always be overlapping as in the case above, but
could also be intersecting, or just near each other without touching.


## Solution

In this solution, the problem is modelled as a dial-a-ride problem (DARP) with multiple vehicles and depots.

I went for the meta-heuristic algorithms approach as opposed to an exact algorithm due to the extensibility
and more practical nature of the former. For instance, in this case, there is no consideration of time
windows, and thus the objective function has the sole objective of minimizing the costs by finding the
option that covers the least distance. However, the objective function can easily be extended to factor in
more constraints.

The rationale behind this solution is that, by finding the globally optimum total cost of picking up and
delivering the orders, then the individual costs and distances are also minimized thus saving
costs for both the clients and partners.

The solution takes the classical 2-step approach, that is cluster first, route second.
So, we first cluster the orders based on available vehicles (partners) and their locations.
Then we generate the best possible route and calculate the costs. Then we iterate on the process.

The idea is to apply a **Genetic Algorithm** with a `POPULATION_SIZE` of **50**, and **1500** `GENERATIONS`.
These values were obtained by running simulations and picking the best numbers. After clustering, we then
use a modified, and greedy **Nearest Neighbour Search** to determine the best route to follow within the
cluster.

