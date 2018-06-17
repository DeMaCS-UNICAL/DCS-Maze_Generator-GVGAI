% Program to compute one horizontal port and the 2 walls from the door (horizontally) straight to the border of the partition
% INPUT:
%   - "row/1" one value for each possibile row value
%   - "col/1" one value for each possibile column value
%   - "max_row/1" the maximum value of all the possible rows (could be computed from row/1)
%   - "max_col/1" the maximum value of all the possible colums (could be computed from col/1)
%   - "min_distance_wall/1" the minimum number of cells to be left from each perimetral cell
%   - "cell/3" the values already present in the partition (should be only the perimetral values)
% OUTPUT:
%   - "cell/3" the values already present in the partition plus the new values computed by this program, in the form of "cell(Row,Column,Content)"
%   - "new_door/3" the position of the new horizontal door, in the form of "new_door(Row,Column,hdoor)"

% No porte sui muri
unavailable_cells(X,Y) :- row(X), col(Y), min_row(X).
unavailable_cells(X,Y) :- row(X), col(Y), min_col(Y).
unavailable_cells(X,Y) :- row(X), col(Y), max_row(X).
unavailable_cells(X,Y) :- row(X), col(Y), max_col(Y).

% Minima distanza tra porta e muri
unavailable_cells(X,Y) :- min_distance_wall(D), row(X), col(Y), min_row(H), X-H<=D.
unavailable_cells(X,Y) :- min_distance_wall(D), row(X), col(Y), min_col(W), Y-W<=D.
unavailable_cells(X,Y) :- min_distance_wall(D), row(X), col(Y), max_row(H), H-X<=D.
unavailable_cells(X,Y) :- min_distance_wall(D), row(X), col(Y), max_col(W), W-Y<=D.

% Non ci sono altre porte su stessa riga/colonna
unavailable_cells(X,Y) :- cell(X,_,"vdoor"), col(Y).


free_cells(X,Y) :- row(X), col(Y), not unavailable_cells(X,Y).

1={new_door(X,Y,"hdoor") : free_cells(X,Y)}=1.

cell(X,Y,"hdoor") :- new_door(X,Y,_).
cell(X,Y2,"wall") :- new_door(X,Y1,_), col(Y2), Y1!=Y2, Y2<MAX, Y2>MIN, min_col(MIN), max_col(MAX).

#show cell/3.
#show new_door/3.
#show object_assignment6/6.
#show connected8/8.
#show assignment5/5.
#show partition4/4.
