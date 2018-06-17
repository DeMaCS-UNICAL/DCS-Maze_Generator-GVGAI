package it.unical.mat.asp_classes;

import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;

@Id("assignment5")
public class Assignment {
	
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
    
    public Assignment(int minRow1, int minCol1, int maxRow1, int maxCol1, String type) {
		super();
		this.minRow = minRow1;
		this.minCol = minCol1;
		this.maxRow = maxRow1;
		this.maxCol = maxCol1;
		this.type = type;
	}
    
    public Assignment() {
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
        return "assignment(p(" + minRow + "," + minCol + "," + maxRow + "," + maxCol + ",)" + type + ").";
    }
}
