package it.unical.mat.asp_classes;

import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;

@Id("connected8")
public class Connected8 {
    
    @Param(0)
    private int minRow1;
    @Param(1)
    private int minCol1;
    @Param(2)
    private int maxRow1;
    @Param(3)
    private int maxCol1;

    @Param(4)
    private int minRow2;
    @Param(5)
    private int minCol2;
    @Param(6)
    private int maxRow2;
    @Param(7)
    private int maxCol2;
    
    public Connected8() {
        super();
    }
    
    public int getMaxCol1() {
        return maxCol1;
    }

    public int getMaxCol2() {
        return maxCol2;
    }

    public int getMaxRow1() {
        return maxRow1;
    }

    public int getMaxRow2() {
        return maxRow2;
    }

    public int getMinCol1() {
        return minCol1;
    }

    public int getMinCol2() {
        return minCol2;
    }

    public int getMinRow1() {
        return minRow1;
    }

    public int getMinRow2() {
        return minRow2;
    }

    public void setMaxCol1(int maxCol1) {
        this.maxCol1 = maxCol1;
    }

    public void setMaxCol2(int maxCol2) {
        this.maxCol2 = maxCol2;
    }

    public void setMaxRow1(int maxRow1) {
        this.maxRow1 = maxRow1;
    }

    public void setMaxRow2(int maxRow2) {
        this.maxRow2 = maxRow2;
    }

    public void setMinCol1(int minCol1) {
        this.minCol1 = minCol1;
    }

    public void setMinCol2(int minCol2) {
        this.minCol2 = minCol2;
    }

    public void setMinRow1(int minRow1) {
        this.minRow1 = minRow1;
    }

    public void setMinRow2(int minRow2) {
        this.minRow2 = minRow2;
    }

    @Override
    public String toString() {
        return "connected8(" + minRow1 + "," + minCol1 + "," + maxRow1 + "," + maxCol1 + "," + minRow2 + "," + minCol2
                + "," + maxRow2 + "," + maxCol2 + ").";
    }

}
