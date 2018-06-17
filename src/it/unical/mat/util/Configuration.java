package it.unical.mat.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

public class Configuration {
    
    private String configfile;
    
    private Logger Log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private Properties prop = null;

    public int getRowSize() {
        if (prop == null || prop.getProperty("rowSize") == null)
            return 50;
        return Integer.parseInt(prop.getProperty("rowSize"));
    }
    
    public int getColumnSize() {
        if (prop == null || prop.getProperty("columnSize") == null)
            return 50;
        return Integer.parseInt(prop.getProperty("columnSize"));
    }
    
    public int getMinDistanceWall() {
        if (prop == null || prop.getProperty("minDistanceWall") == null)
            return 3;
        return Integer.parseInt(prop.getProperty("minDistanceWall"));
    }
    
    public int getRandomAnswersetNumber() {
        if (prop == null || prop.getProperty("randomAnswersetNumber") == null)
            return 10;
        return Integer.parseInt(prop.getProperty("randomAnswersetNumber"));
    }
    
    public int getRandomSeed() {
        if (prop == null || prop.getProperty("randomSeed") == null)
            return -1;
        return Integer.parseInt(prop.getProperty("randomSeed"));
    }
    
    public int getMinRoomSize() {
        if (prop == null || prop.getProperty("minRoomSize") == null)
            return 40;
        return Integer.parseInt(prop.getProperty("minRoomSize"));
    }
    
    public double getPruningPercentage() {
        if (prop == null || prop.getProperty("pruningPercentage") == null)
            return 0.05;
        return Double.parseDouble(prop.getProperty("pruningPercentage"));
    }
    
    public double getSameOrientationPercentage() {
        if (prop == null || prop.getProperty("sameOrientationPercentage") == null)
            return 0.20;
        return Double.parseDouble(prop.getProperty("sameOrientationPercentage"));
    }
    
    public String getEncodingFolder() {
        if (prop == null || prop.getProperty("encodingFolder") == null)
            return "encoding" + File.separator;  
        return prop.getProperty("encodingFolder").toString() + File.separator;
    }
    
    public String getGame() {
        if (prop == null || prop.getProperty("game") == null)
            return "encoding" + File.separator;  
        return prop.getProperty("game").toString();
    }
    
    public boolean isDebugMode() {
        if (prop == null || prop.getProperty("debug") == null)
            return false; 
        return Boolean.parseBoolean(prop.getProperty("debug"));
    }

    
    
    public Configuration(String configfile) {
		super();
		if (prop == null)
            try {
            	this.configfile = configfile;
                prop = new Properties();
                Log.info("loading configuration from '" + configfile + "'");
                prop.load(new FileInputStream(configfile));
            } catch (final IOException e) {
                Log.warning("Could not open configuration file.");
                Log.warning(e.toString());
                Log.warning("Falling back to defaults.");

                prop = null;
            }
	}
}