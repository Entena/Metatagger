package db;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Helper class that has common functions to help with db operations.
 * 
 * @author Tim Eck
 *
 */
public class DatabaseHelper {

    public DatabaseHelper() { }

    public static String SQLFromFile(String file) throws IOException{
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuilder sb = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null){
            sb.append(line);
        }
        reader.close();
        return sb.toString();
    }
    
}
