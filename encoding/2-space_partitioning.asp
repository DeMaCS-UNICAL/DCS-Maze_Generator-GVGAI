% Program to compute one horizontal/vertical port and the 2 walls from the door (horizontally/vertically) straight to the border of the partition
% INPUT:
%   - "row/1" one value for each possibile row value
%   - "col/1" one value for each possibile column value
%   - "max_row/1" the maximum value of all the possible rows (could be computed from row/1)
%   - "max_col/1" the maximum value of all the possible colums (could be computed from col/1)
%   - "min_distance_wall/1" the minimum number of cells to be left from each perimetral cell
%   - "cell/3" the values already present in the partition (should be only the perimetral values)
%   - "orientation/1" if the door/wall should be horizontal or vertical
% OUTPUT:
%   - "cell/3" the values already present in the partition plus the new values computed by this program, in the form of "cell(Row,Column,Content)"
%   - "new_door/3" the position of the new horizontal door, in the form of "new_door(Row,Column,Type)"

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
unavailable_cells(X,Y) :- cell(X,_,"vdoor"), col(Y), orientation(horizontal).
unavailable_cells(X,Y) :- cell(_,Y,"hdoor"), row(X), orientation(vertical).


door_type("hdoor") :- orientation(horizontal).
door_type("vdoor") :- orientation(vertical).

free_cells(X,Y) :- row(X), col(Y), not unavailable_cells(X,Y).

1={new_door(X,Y,Dtype) : free_cells(X,Y)}=1 :- door_type(Dtype).

cell(X,Y,Dtype) :- new_door(X,Y,_), door_type(Dtype).
cell(X,Y2,"wall") :- new_door(X,Y1,_), col(Y2), Y1!=Y2, orientation(horizontal), Y2<MAX, Y2>MIN, min_col(MIN), max_col(MAX).
cell(X2,Y,"wall") :- new_door(X1,Y,_), row(X2), X1!=X2, orientation(vertical), X2<MAX, X2>MIN, min_row(MIN), max_row(MAX).
