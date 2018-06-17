package it.unical.mat.debug;

import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;

@Id("unavailable_cells")
public class UnavCells {
    
    @Param(0)
    private int row;
    @Param(1)
    private int column;
    
    public UnavCells(int r, int c) {
        this.row = r;
        this.column = c;
    }
    
    public UnavCells() {
    }
    
    public int getRow() {
        return row;
    }
    
    public void setRow(int row) {
        this.row = row;
    }
    
    public int getColumn() {
        return column;
    }
    
    public void setColumn(int column) {
        this.column = column;
    }
    
    
    @Override
    public String toString() {
        return "unav_cell(" + row + "," + column + "). ";
    }
    
}
