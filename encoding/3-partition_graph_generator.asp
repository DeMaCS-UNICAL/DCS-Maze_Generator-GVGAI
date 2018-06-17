% Program to compute the graph of the partitions
% INPUT:
%   - "row/1" one value for each possibile row value
%   - "col/1" one value for each possibile column value
%   - "cell/3" the values already present in the partition (should be only the perimetral values)
% OUTPUT:
%   - "partition/1" describes the partition ids, in the form of "partition(c(Rmin, Cmin, Rmax, Cmax))"
%   - "connected/2" eRists the value "connected(P1,P2)" if the partition P1 is connected to the partition P2, i.e. there is a door that connects P1 and P2"

%#show partition/3.
%#show connected/2.
%#show partition_door/3.
%#show free_adjacent/4.
%#show free_cells/2.

%#show partition4/4.
%#show connected8/8.

free_cells(R,C) :- row(R), col(C), not cell(R,C).
cell(R,C) :- cell(R,C,_).

partition_door(c(R-1,C), top, d(R,C)) :- cell(R,C,"hdoor"), free_cells(R-1,C).
partition_door(c(R+1,C), bottom, d(R,C)) :- cell(R,C,"hdoor"), free_cells(R+1,C).
partition_door(c(R,C-1), left, d(R,C)) :- cell(R,C,"vdoor"), free_cells(R,C-1).
partition_door(c(R,C+1), right, d(R,C)) :- cell(R,C,"vdoor"), free_cells(R,C+1).

partition_door(c(RF,CF),T,D) :- partition_door(c(R,C),T,D), free_adjacent(RF,CF,R,C).

partition_door_type(T, D) :- partition_door(_, T, D).
partition(p(Rmin-1, Cmin-1, Rmax+1, Cmax+1), T, D) :- partition_door_type(T,D),
                                        #min{R: partition_door(c(R,C), T, D)} = Rmin,
                                        #min{C: partition_door(c(R,C), T, D)} = Cmin,
                                        #max{R: partition_door(c(R,C), T, D)} = Rmax,
                                        #max{C: partition_door(c(R,C), T, D)} = Cmax.

connected(P1, P2) :- partition(P1, T1, D), partition(P2, T2, D), opposite(T1, T2).

% TODO : Remove when embasp will support ground funcional terms
partition4(Rmin, Cmin, Rmax, Cmax) :- partition(p(Rmin, Cmin, Rmax, Cmax), _, _).
connected8(Rmin1, Cmin1, Rmax1, Cmax1, Rmin2, Cmin2, Rmax2, Cmax2) :- connected(p(Rmin1, Cmin1, Rmax1, Cmax1), p(Rmin2, Cmin2, Rmax2, Cmax2)).

opposite(top, bottom).
opposite(left, right).
opposite(T1, T2) :- opposite(T2, T1).


free_adjacent(R1,C1,R2,C2) :- free_cells(R1,C1), free_cells(R2,C2), step(DR,DC), R2 = R1 + DR, C2 = C1 + DC.

step(0,1).
step(0,-1).
step(1,0).
step(-1,0).

#show connected8/8.
#show partition4/4.
