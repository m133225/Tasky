package logic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class PropertyHandler {
    File configFile = null;
    Properties propObject = null;
    
    PropertyHandler(String configFilePath, Properties propObject) throws Exception {
        this.propObject = propObject;
        this.configFile = new File(configFilePath);
        
        BufferedReader bufReader;
        try {
            bufReader = new BufferedReader(new FileReader(configFile));

            propObject.load(bufReader);
            bufReader.close();
        } catch (FileNotFoundException e) {
            try {
                configFile.createNewFile();
                
                BufferedWriter bufWriter = new BufferedWriter(new FileWriter(configFile));
                propObject.store(bufWriter, "default properties");
            } catch (IOException e2) {
                throw new Exception("Error creating new configuration file.");
            }
        } catch (IOException e) {
            throw new Exception("Error reading configuration file");
        }
    }
    
    public boolean setProperty(String key, String value) {
        propObject.setProperty(key, value);
        return writeProperties();
    }
    
    public String getProperty(String key){
        return propObject.getProperty(key);
    }
    
    boolean writeProperties() {
        BufferedWriter bufWriter;
        try {
            bufWriter = new BufferedWriter(new FileWriter(configFile));
            propObject.store(bufWriter, null);
            bufWriter.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
