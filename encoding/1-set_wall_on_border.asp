% Program to compute the perimeter of the map
% INPUT:
%   - "row/1" one value for each possibile row value
%   - "col/1" one value for each possibile column value
%   - "max_row/1" the maximum value of all the possible rows (could be computed from row/1)
%   - "max_col/1" the maximum value of all the possible colums (could be computed from col/1)
% OUTPUT:
%   - "cell/3" all the positions of wall cells of the perimeter, in the form of "cell(Row,Column,wall)"

cell(1,Y,wall) :- col(Y).
cell(X,Y,wall) :- col(Y), max_row(X).

cell(X,1,wall) :- row(X).
cell(X,Y,wall) :- row(X), max_col(Y).

#show cell/3.
