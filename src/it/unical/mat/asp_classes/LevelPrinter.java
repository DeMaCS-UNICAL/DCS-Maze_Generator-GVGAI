package it.unical.mat.asp_classes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;

public class LevelPrinter {
PrintWriter inputWriter;
    
    public LevelPrinter() {
        File file = new File("level_map/level_map");
        try {
            Files.deleteIfExists(file.toPath());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void printOnFile(String toPrint) {
        try {
            this.inputWriter = new PrintWriter(new BufferedWriter(new FileWriter("level_map/level_map", true)));
        } catch (FileNotFoundException e) {
            System.err.println("File not found !");
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            System.err.println("Encoding error !");
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        inputWriter.print(toPrint);
        inputWriter.close();
    }
}
