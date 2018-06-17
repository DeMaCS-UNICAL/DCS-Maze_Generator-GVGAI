package it.unical.mat.util;

import java.util.Properties;

public class Config {
	   private Properties configFile;
	   
	   public Config(String configurationFile ){
		   configFile = new java.util.Properties();
		   try {
			   configFile.load(this.getClass().getClassLoader().getResourceAsStream(configurationFile));
		   	   }catch(Exception eta){
		   		   eta.printStackTrace();
		   	   }
	   }
	 
	   public String getProperty(String key)
	   {
		   String value = this.configFile.getProperty(key);
		   return value;
	   }
}
