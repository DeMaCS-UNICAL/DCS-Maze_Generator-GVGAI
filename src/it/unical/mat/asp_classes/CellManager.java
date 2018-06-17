package it.unical.mat.asp_classes;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class CellManager {
    
    HashMap<Pair<Integer, Integer>, Cell> cells = new HashMap<>();
    HashMap<String, Set<Cell>> cellsType = new HashMap<>();
    
    public CellManager() {
        cellsType.put("hdoor", new HashSet<>());
        cellsType.put("vdoor", new HashSet<>());
    }
    
    public CellManager(HashMap<Pair<Integer, Integer>, Cell> cells, HashMap<String, Set<Cell>> cellsType) {
        super();
        this.cells = cells;
        this.cellsType = cellsType;
    }
    
    public HashMap<Pair<Integer, Integer>, Cell> getCells() {
        return cells;
    }
    
    public HashMap<String, Set<Cell>> getCellsType() {
        return cellsType;
    }
    
    public void addCell(Cell cell) {
        cells.put(new Pair<Integer, Integer>(cell.getRow(), cell.getColumn()), cell);
        this.addCellType(cell);
    }
    
    private void addCellType(Cell cell) {
        if (cellsType.containsKey(cell.getGVGAI()))
            cellsType.get(cell.getGVGAI()).add(cell);
        else {
            Set<Cell> setCells = new HashSet<>();
            setCells.add(cell);
            cellsType.put(cell.getGVGAI(), setCells);
        }
        
    }
    
    public Collection<Cell> getSetCells() {
        return cells.values();
    }
    
    public Collection<Cell> getSetCellsType(String type) {
        return cellsType.get(type);
    }
    
}
