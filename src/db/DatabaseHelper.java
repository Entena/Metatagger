package db;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;

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
    public static final String SQL_FOLDER_PATH = "sql/";
    
    /*
     * Songs table columns 
     */
    public static final String SONGS_TABLE = "Songs";
    public static final String SONG_ID_COLUMN = "SONG_ID";
    public static final String NAME_COLUMN = "NAME";
    public static final String FILEPATH_COLUMN = "FILEPATH";
    public static final String ALBUM_COLUMN = "ALBUM";
    public static final String ARTIST_COLUMN = "ARTIST";
    public static final String LAST_PLAYED_COLUMN = "LAST_PLAYED";
    public static final String PLAY_COUNT = "PLAY_COUNT";
    
    /*
     * MetaData table columns
     */
    public static final String META_DATA_TABLE = "MetaData";
    public static final String META_ID_COLUMN = "META_ID";
    public static final String SONG_ID_FORIEGN_KEY_COLUMN = "SONG_ID";
    public static final String META_VALUE_COLUMN = "META_VALUE";
    
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
            sb.append(' ');
        }
        reader.close();
        return sb.toString();
    }
    
    /**
     * Helper function to build a complete sql statement from a templated sql
     * statement. The complete sql statement will be build by replacing keys in
     * the sql with values specified in the parameters hashmap.<br/> 
     * 
     * A sql template will look like this: <br/>
     * SELECT *column_name* FROM SomeTable WHERE id=*specified_id*;<br/>
     * Then the parameters hashmap will look something like this:<br/>
     * keys:value<br/>
     * column_name:name<br/>
     * specified_id:1<br/>
     * 
     * @param sqlTemplate
     * @param parameters
     * @return
     */
    public static String SQLBuilder(String sqlTemplate, HashMap<String, String> parameters){
        StringBuilder sb = new StringBuilder(sqlTemplate);
        
        // Loop over ever key in the parameters hash map
        for(String key : parameters.keySet()){
            String value = parameters.get(key);
            
            // The keys in the sql will be formated *key*. And so that is what
            // we need to search in the sql by.
            String realKey = "*" + key + "*";
            int keyLength = realKey.length();
            
            int startLocation = 0;
            // Keep searching the sql until we there are no more keys for use to
            // replace with their value.
            while( (startLocation = sb.indexOf(realKey)) != -1){
                sb.replace(startLocation, startLocation + keyLength, value);
            }
        }
        return sb.toString();
    }
}
