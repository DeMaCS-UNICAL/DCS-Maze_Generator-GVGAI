% Program to generate the "walkable" cells of the partitions of type "corridor"
% INPUT:
%   - "row/1" one value for each possibile row value
%   - "col/1" one value for each possibile column value
%   - "min_row/1" the minimum value of all the possible rows (could be computed from row/1)
%   - "min_col/1" the minimum value of all the possible colums (could be computed from col/1)
%   - "max_row/1" the maximum value of all the possible rows (could be computed from row/1)
%   - "max_col/1" the maximum value of all the possible colums (could be computed from col/1)
%   - "cell/3" the values already present in the partition (should be only the perimetral values)
% OUTPUT:
%   - "cell/3" the values already present in the partition plus the new values computed by this program, in the form of "cell(Row,Column,Content)"

%col(1..5).row(1..50).min_row(1).max_row(50).min_col(1).max_col(5).cell(9,5,"vdoor").

step(0,1).
step(0,-1).
step(1,0).
step(-1,0).

diagonal_step(1,1).
diagonal_step(1,-1).
diagonal_step(-1,1).
diagonal_step(-1,-1).

% "free_cells/2" calculates all free cells
free_cells(X,Y) :- row(X), col(Y), min_row(MnR), max_row(MxR), min_col(MnC), max_col(MxC), X > MnR, X < MxR, Y > MnC, Y < MxC.

% calculates the maximum free cells number
free_cells_rows_number(NR) :- min_row(MnR), max_row(MxR), N = MxR - MnR, NR = N - 1.
free_cells_col_number(NC) :- min_col(MnC), max_col(MxC), N = MxC - MnC, NC = N - 1.
max_number_cell(N) :- free_cells_rows_number(NC), free_cells_col_number(NR), N = NR * NC.

% - choice rule that calculates a total number of walls equal to 1/3 of the free cells total number
Mn<{cell(X,Y,"wall"):free_cells(X,Y)}<Mx:- max_number_cell(N), Mn = N/4, Mx = N/2.

% "adjacent_wall/3" calculates all direct adjacent of a wall
next_door(W,Z) :- door(X,Y), free_cells(W,Z), step(S1,S2), W = X + S1, Z = Y + S2.

% It's not possible to find a wall in front of a door
:- cell(X,Y,"wall"), next_door(X,Y).

% "passable/4" calculates the path from the free cell near a door
passable(X,Y,W,Z) :- next_door(X,Y), free_cells(W,Z), not cell(W,Z,"wall"), step(S1,S2), W = X + S1, Z = Y + S2.
passable(W,T,V,Z) :- passable(W,T,X,Y), free_cells(V,Z), not cell(V,Z,"wall"), step(S1,S2), V = X + S1, Z = Y + S2.
door(X,Y) :- cell(X,Y,"hdoor").
door(X,Y) :- cell(X,Y,"vdoor").

% It isn't possible that there aren't paths beetween different free cells near doors
:- next_door(W,Z), free_cells(X,Y), not cell(X,Y,"wall"), not passable(W,Z,X,Y).

% "adjacent/4" calculates all direct adjacent of all not wall cell
adjacent(X,Y,W,Z) :- free_cells(X,Y), free_cells(W,Z), not cell(X,Y,"wall"), not cell(W,Z,"wall"), step(S1,S2), W = X + S1, Z = Y + S2.

% "diagonal_adjacent/4" calculates all diagonal adjacent of all not wall cell
diagonal_adjacent(X,Y,W,Z) :- free_cells(X,Y), free_cells(W,Z), not cell(X,Y,"wall"), not cell(W,Z,"wall"), diagonal_step(S1,S2), W = X + S1, Z = Y + S2.

% it's impossible to find a corridor cell that has the number of adjacent not equal to 2
:- free_cells(X,Y), not next_door(X,Y), not cell(X,Y,"wall"), #count{W,Z: adjacent(X,Y,W,Z)} != 2.

% it's ipossible to find a corridor cell that has the number of diagonal adjacent greater than 1
%:- free_cells(X,Y), not cell(X,Y,"wall"), #count{W,Z: diagonal_adjacent(X,Y,W,Z)} > 1.

% it's impossible to find a next to a door cell that has the number of adjacent greater than 3
:- next_door(X,Y), #count{W,Z: adjacent(X,Y,W,Z)} > 3.

% this weak constraint minimizes the number of wall
:~ cell(X,Y,"wall"). [1@1]

#show cell/3.