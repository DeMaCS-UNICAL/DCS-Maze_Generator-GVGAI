package it.unical.mat.asp_classes;

import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;

@Id("new_door")
public class NewDoor {
    
    @Param(0)
    private int row;
    @Param(1)
    private int column;
    @Param(2)
    private String type;
    
    public NewDoor(int r, int c, String t) {
        setRow(r);
        setColumn(c);
        setType(t);
    }
    
    public NewDoor() {
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
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
        this.convertTypeFormat();
    }
    
    public void convertTypeFormat() {
        type = type.replaceAll("\"", "");
    }
    
    @Override
    public String toString() {
        return "new_door(" + row + "," + column + "," + type + "). ";
    }
    
}
