package it.unical.mat.asp_classes;

import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;

@Id("connected")
public class Connected {

    @Param(0)
    private String partition1;
    @Param(1)
    private String partition2;

    public Connected() {
        super();
    }
    
    public Connected(String partition1, String partition2) {
        super();
        this.partition1 = partition1;
        this.partition2 = partition2;
    }
    
    public String getPartition1() {
        return partition1;
    }
    
    public String getPartition2() {
        return partition2;
    }
    
    public void setPartition1(String partition1) {
        this.partition1 = partition1;
    }
    
    public void setPartition2(String partition2) {
        this.partition2 = partition2;
    }
    
    @Override
    public String toString() {
        return "connected(" + partition1 + "," + partition2 + ").";
    }
    
}
