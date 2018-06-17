% Program to generate the "walkable" cells of the partitions of type "room"
% INPUT:
%   - "row/1" one value for each possibile row value
%   - "col/1" one value for each possibile column value
%   - "max_row/1" the maximum value of all the possible rows (could be computed from row/1)
%   - "max_col/1" the maximum value of all the possible colums (could be computed from col/1)
%   - "cell/3" the values already present in the partition (should be only the perimetral values)
% OUTPUT:
%   - "cell/3" the values already present in the partition plus the new values computed by this program, in the form of "cell(Row,Column,Content)"

step(0,1).
step(0,-1).
step(1,0).
step(-1,0).

% "free_cells/2" calculates all free cells
free_cells(X,Y) :- row(X), col(Y), min_row(MnR), max_row(MxR), min_col(MnC), max_col(MxC), X > MnR, X < MxR, Y > MnC, Y < MxC.

% calculates the maximum free cells number
free_cells_rows_number(NR) :- min_row(MnR), max_row(MxR), N = MxR - MnR, NR = N - 1.
free_cells_col_number(NC) :- min_col(MnC), max_col(MxC), N = MxC - MnC, NC = N - 1.
max_number_cell(N) :- free_cells_rows_number(NC), free_cells_col_number(NR), N = NR * NC.

% - choice rule that calculates a total number of walls equal to 1/3 of the free cells total number
Mn<{cell(X,Y,"wall"):free_cells(X,Y)}<=Mx :- max_number_cell(N), Mn = N/4, Mx = N/2.

% "adjacent_wall/3" calculates all direct adjacent of a wall
adjacent_wall(X,Y,W,Z) :- cell(X,Y,"wall"), cell(W,Z,"wall"), step(S1,S2), W = X + S1, Z = Y + S2.

% It's not possible to find a wall without a neighbor wall
:- cell(X,Y,"wall"), N = #count{W,Z: adjacent_wall(X,Y,W,Z)}, N < 1.

door(X,Y) :- cell(X,Y,"hdoor").
door(X,Y) :- cell(X,Y,"vdoor").

% "next_door/2" calculates the free cells near the doors
next_door(W,Z) :- door(X,Y), free_cells(W,Z), step(S1,S2), W = X + S1, Z = Y + S2.

% It's not possible to find a wall in front of a door
:- cell(X,Y,"wall"), next_door(X,Y).

% "passable/4" calculates the path from the free cell near a door
passable(X,Y,W,Z) :- next_door(X,Y), free_cells(W,Z), not cell(W,Z,"wall"), step(S1,S2), W = X + S1, Z = Y + S2.
passable(W,T,V,Z) :- passable(W,T,X,Y), free_cells(V,Z), not cell(V,Z,"wall"), step(S1,S2), V = X + S1, Z = Y + S2.

% It isn't possible that there aren't paths beetween different free cells near doors
:- next_door(W,Z), free_cells(X,Y), not cell(X,Y,"wall"), not passable(W,Z,X,Y).

% Rules that calculate all walls coordinates of all quarters (first, second, third and fourth ):
% - "first_quarter/2" all walls coordinates of the first quarter
% - "second_quarter/2" all walls coordinates of the second quarter
% - "third_quarter/2" all walls coordinates of the third quarter
% - "fourth_quarter/2" all walls coordinates of the fourth quarter

half_column(N) :- max_col(MxC), min_col(MnC), C = MxC - MnC, HC = C / 2, N = MnC + HC.
half_row(N) :- max_row(MxR), min_row(MnR), R = MxR - MnR, HR = R / 2, N = MnR + HR.

first_quarter(X,Y) :- cell(X,Y,"wall"), min_row(MnR), min_col(MnC), half_row(HR), half_column(HC), X > MnR, X <= HR, Y > MnC, Y <= HC.
second_quarter(X,Y) :- cell(X,Y,"wall"), min_row(MnR), max_col(MxC), half_row(HR), half_column(HC), X > MnR, X <= HR, Y > HC, Y < MxC.
third_quarter(X,Y) :- cell(X,Y,"wall"), min_col(MnC), max_row(MxR), half_row(HR), half_column(HC), X > HR, X < MxR, Y > MnC, Y <= HC.
fourth_quarter(X,Y) :- cell(X,Y,"wall"), max_row(MxR), max_col(MxC), half_row(HR), half_column(HC), X > HR, X < MxR, Y > HC, Y < MxC.

%Rules that calculate
corner(X,Y) :- min_row(MnR), min_col(MnC), X = MnR + 1, Y = MnC + 1, not next_door(X,Y).
corner(X,Y) :- min_row(MnR), max_col(MxC), X = MnR + 1, Y = MxC - 1, not next_door(X,Y).
corner(X,Y) :- max_row(MxR), min_col(MnC), X = MxR - 1, Y = MnC + 1, not next_door(X,Y).
corner(X,Y) :- max_row(MxR), max_col(MxC), X = MxR -1, Y = MxC - 1, not next_door(X,Y).

:- corner(X,Y), not cell(X,Y,"wall").

% These weak constraints calculate the differences of the distances between the corner and the walls of
% each quarter and than uses it as cost of the weak constraints.
% Their purpose is to minimize the distances between walls and corners of each quarter.
:~ first_quarter(X,Y), min_row(MnR), min_col(MnC), D1 = X - MnR, D2 = Y - MnC, D = D1 + D2. [D@2]
:~ second_quarter(X,Y), min_row(MnR), max_col(MxC), D1 = X - MnR, D2 = MxC - Y, D = D1 + D2. [D@2]
:~ third_quarter(X,Y), max_row(MxR), min_col(MnC), D1 = MxR - X, D2 = Y - MnC, D = D1 + D2. [D@2]
:~ fourth_quarter(X,Y), max_row(MxR), max_col(MxC), D1 = MxR - X, D2 = MxC - Y, D = D1 + D2. [D@2]

#show cell/3.
