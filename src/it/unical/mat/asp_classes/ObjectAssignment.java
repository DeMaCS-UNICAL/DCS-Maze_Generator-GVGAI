package it.unical.mat.asp_classes;

import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;

@Id("object_assignment5")
public class ObjectAssignment {
	
	@Param(0)
    private int minRow;
    @Param(1)
    private int minCol;
    @Param(2)
    private int maxRow;
    @Param(3)
    private int maxCol;
    @Param(4)
    private String type;
    
    public ObjectAssignment(int minRow, int minCol, int maxRow, int maxCol, String type, int object_id) {
		super();
		this.minRow = minRow;
		this.minCol = minCol;
		this.maxRow = maxRow;
		this.maxCol = maxCol;
		this.type = type;
	}

	public ObjectAssignment() {
	}

	public int getMinRow() {
		return minRow;
	}
	
	public void setMinRow(int minRow1) {
		this.minRow = minRow1;
	}
	
	public int getMinCol() {
		return minCol;
	}
	
	public void setMinCol(int minCol1) {
		this.minCol = minCol1;
	}
	
	public int getMaxRow() {
		return maxRow;
	}
	
	public void setMaxRow(int maxRow1) {
		this.maxRow = maxRow1;
	}
	
	public int getMaxCol() {
		return maxCol;
	}
	
	public void setMaxCol(int maxCol1) {
		this.maxCol = maxCol1;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	@Override
    public String toString() {
        return "object_assignment(p(" + minRow + "," + minCol + "," + maxRow + "," + maxCol + ")," + type + ").";
    }
}
