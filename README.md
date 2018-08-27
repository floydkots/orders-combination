# Orders Combination

# Problem Definition
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


