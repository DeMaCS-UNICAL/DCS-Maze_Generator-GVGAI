% Program to compute the assignment of each partition to a specific type: room, corridor or empty
% INPUT:
%   - "partition/1" one value for each partition
%   - "connected/2" exists the value "connected(P1,P2)" if the partition P1 is connected to the partition P2, i.e. there is a door that connects P1 and P2
%   - "start_partition/1" the value of the start partition
%   - "num_partitions/1" the number of the partitions
%   - "empty_percentage_range/2" the percentage range of the empty partitions number
% OUTPUT:
%   - "assignment/2" the specification of the type of each partition, in the form of "assignment(P,Type)"

% choice rule that assigns a type for each partition
1={assignment(P,T):type(T)}=1 :- partition(P).

assignment5(Rmin, Cmin, Rmax, Cmax,T) :- assignment(p(Rmin, Cmin, Rmax, Cmax),T).

% "num_empty_partitions/1" calculates empty partitions number
num_empty_partitions(NE) :- NE = #count{P: assignment(P,"empty")}.

% "empty_rooms_range/2" calculates the minimum percentage and the maxumum percentage related to the partitions number
empty_partitions_range(A,B) :- num_partitions(N), empty_percentage_range(N1,N2), A = ( N * N1 ) / 100, B = ( N * N2 ) / 100.

% It's infeasible to have an empty partitions number lower than the minimum percentage and greater than the maximum percentage
:- num_empty_partitions(NE), empty_partitions_range(A,B), NE < A.
:- num_empty_partitions(NE), empty_partitions_range(A,B), NE > B.

% "passable_partition" calculates all partitions not empty
passable_partition(P) :- assignment(P,T), T != "empty".

% It's infeasible to have a start partition that isn't passable
:- start_partition(P), not passable_partition(P).

% "start_connected" calculates the rooms that are directly and indirectly connected
% to the start room
start_connected(P1) :- connected(P,P1), start_partition(P), passable_partition(P1).
start_connected(P1) :- start_connected(P2), connected(P2,P1), passable_partition(P1).

% it's infeasible to have a room not empty that isn't connected to the start room
:- passable_partition(P), not start_connected(P).

#show assignment5/5.