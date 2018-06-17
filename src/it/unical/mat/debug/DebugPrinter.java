package it.unical.mat.debug;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;

public class DebugPrinter {
    
    PrintWriter inputWriter;
    
    public DebugPrinter() {
        File file = new File("asp_input_output/input");
        try {
            Files.deleteIfExists(file.toPath());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void printOnFile(String toPrint) {
        try {
            this.inputWriter = new PrintWriter(new BufferedWriter(new FileWriter("asp_input_output/input", true)));
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
