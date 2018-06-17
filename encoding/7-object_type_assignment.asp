free_cells(X,Y) :- min_row(MnR), min_col(MnC), max_row(MxR), max_col(MxC), row(X), col(Y), X > MnR, Y > MnC, X < MxR, Y < MxC, not cell(X,Y,"wall"), not cell(X,Y,"vdoor"), not cell(X,Y,"hdoor").
1={cell(X,Y,T):free_cells(X,Y)}=1 :- type(T).

#show cell/3.
