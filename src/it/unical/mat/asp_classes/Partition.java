package it.unical.mat.asp_classes;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;

@Id("partition4")
public class Partition {
    
    public static Partition getPartition(String partitionTuple) {
        // Create a Pattern object
        Pattern r = Pattern.compile("\\d+,\\d+,\\d+,\\d+");
        
        // Now create matcher object.
        Matcher m = r.matcher(partitionTuple);
        if (m.find()) {
            int minRow = Integer.parseInt(m.group(0));
            int minCol = Integer.parseInt(m.group(1));
            int maxRow = Integer.parseInt(m.group(2));
            int maxCol = Integer.parseInt(m.group(3));
            
            return new Partition(minRow, minCol, maxRow, maxCol);
        }
        return new Partition();
    }
    
    // @Param(0)
    // private String partitionTuple;
    @Param(0)
    private int minRow;
    @Param(1)
    private int minCol;
    @Param(2)
    private int maxRow;
    @Param(3)
    private int maxCol;
    
    private List<Pair> doors = new ArrayList<>();
    
    private String Type;
    
    public Partition() {
    }
    
    public Partition(int minRow, int minCol, int maxRow, int maxCol) {
        super();
        this.minRow = minRow;
        this.minCol = minCol;
        this.maxRow = maxRow;
        this.maxCol = maxCol;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Partition other = (Partition) obj;
        if (maxCol != other.maxCol)
            return false;
        if (maxRow != other.maxRow)
            return false;
        if (minCol != other.minCol)
            return false;
        if (minRow != other.minRow)
            return false;
        return true;
    }
    
    public int getMaxCol() {
        return maxCol;
    }
    
    public int getMaxRow() {
        return maxRow;
    }
    
    public int getMinCol() {
        return minCol;
    }
    
    public int getMinRow() {
        return minRow;
    }
    
    public int getSize() {
        return (maxRow - minRow + 1) * (maxCol - minCol + 1);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + maxCol;
        result = prime * result + maxRow;
        result = prime * result + minCol;
        result = prime * result + minRow;
        return result;
    }
    
    // TODO Remove set when embasp will support ground functional terms
    public void setMaxCol(int maxCol) {
        this.maxCol = maxCol;
    }
    
    public void setMaxRow(int maxRow) {
        this.maxRow = maxRow;
    }
    
    public void setMinCol(int minCol) {
        this.minCol = minCol;
    }
    
    public void setMinRow(int minRow) {
        this.minRow = minRow;
    }
    
    public List<Pair> getDoors(){
    	return doors;
    }
    
    public String getType() {
		return Type;
	}

	public void setType(String type) {
		Type = type;
	}

	@Override
    public String toString() {
        return "partition(p(" + minRow + "," + minCol + "," + maxRow + "," + maxCol + ")).";
    }
    
}
