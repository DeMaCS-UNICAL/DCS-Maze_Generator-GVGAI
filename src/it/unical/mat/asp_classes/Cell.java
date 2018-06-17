package it.unical.mat.asp_classes;

import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;
import it.unical.mat.util.Configuration;

@Id("cell")
public class Cell {
    
    @Param(0)
    private int row;
    @Param(1)
    private int column;
    @Param(2)
    private String type;
    
    public Cell(int r, int c, String t) {
        setRow(r);
        setColumn(c);
        setType(t);
    }
    
    public Cell() {
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
    
    public String getGVGAI() {
        switch (type) {
            case "wall":
                return "w";
            case "pellet":
                return ".";
            case "hdoor":
                return "|";
            case "vdoor":
                return "=";
            case "avatar":
                return "A";
            case "exit":
            	return "x";
            case "goal":
            	return "g";
            case "key":
            	return "+";
            case "enemy":
            	return "2";
            case "trap":
				return "t";
            default:
                throw new IllegalArgumentException("Char " + type + " not implemented yet !");
        }
    }
    
    @Override
    public String toString() {
        return "cell(" + row + "," + column + "," + type + "). ";
    }
    
}
