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

### Input
The list of coordinates is supplied as a text file. After the header line, the first pair of coordinates
represents the depot. Which is the assumed location of the partners at the time the orders are made.

Within the list of coordinates, the first half of coordinate pairs are the pickup locations, while the
remaining half as the drop-off locations.

The sample orders are found in the `src/main/java/resources/orders/` folder.

### Output
The program makes output in the following format. This sample output is taken from running it on
the `6_orders.txt` input file.

It first outputs overall details of the Genetic Algorithm, and the input.

Thereafter, it outputs the distances and routes of the final optimum clustering that was settled on.

Finally, it outputs the total distance and total cost of the pickups and drop-offs.

`Route: [0, 0]` for instance, indicates that the vehicle did not leave the depot, i.e it's route was
from the depot, to the depot, without any intermediate stops.

`Route: [0, 1, 7, 0]` indicates that the vehicle started from the depot at `0`, then to the stops `1` and `7`, to pick and drop the order respectively, and finally back to the depot.

```text
Worst Individuals: 5
Population: 50
Generations: 15000
File: orders/6_orders.txt
Number of Orders: 6

Distances and Routes per Vehicle
================================================

------------------------------------------------
Vehicle 0
Distance: 0.0 Km
Route: [0, 0]
------------------------------------------------

------------------------------------------------
Vehicle 1
Distance: 1318.05 Km
Route: [0, 1, 7, 0]
------------------------------------------------

------------------------------------------------
Vehicle 2
Distance: 0.0 Km
Route: [0, 0]
------------------------------------------------

------------------------------------------------
Vehicle 3
Distance: 4748.51 Km
Route: [0, 2, 5, 8, 4, 10, 11, 6, 3, 12, 9, 0]
------------------------------------------------

------------------------------------------------
Vehicle 4
Distance: 0.0 Km
Route: [0, 0]
------------------------------------------------


Total distance: 6066.5596 Km
Total Cost: 90998.39
```
