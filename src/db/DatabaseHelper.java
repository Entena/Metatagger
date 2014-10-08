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

    /**
     * Relative folder path to the folder that contains the sql scripts.
     */
    public static final String SQL_FOLDER_PATH = "db/sql/";
    
    public DatabaseHelper() { }

    /**
     * Given a file path, attempts to load the file and turn it into a string.
     * @param file The file to attempt to load.
     * @return The file in the form of a string with out newlines.
     * @throws IOException
     */
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
