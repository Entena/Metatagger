/**
 * 
 */
package db;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

/**
 * @author Tim Eck
 *
 */
public class DatabaseModel {

    private DatabaseConnector dbConn;
    
    private String insertSQLTemplate = "";
    private String updateSQLTemplate = "";
    
    /**
     * 
     */
    public DatabaseModel(DatabaseConnector dbConn) {
        this.dbConn = dbConn;
        
        // Load the insert sql template file
        try {
            insertSQLTemplate = DatabaseHelper.SQLFromFile(DatabaseHelper.SQL_FOLDER_PATH + "insert_song_template.sql");
            updateSQLTemplate = DatabaseHelper.SQLFromFile(DatabaseHelper.SQL_FOLDER_PATH + "update_song_template.sql");
        } catch (IOException e) {
            System.err.println("Could not load the sql template files. Make sure that the sql files are accessible.");
            e.printStackTrace();
            System.exit(100);
        } 
    }
    
    /**
     * Inserts a song into the database.
     * @param name
     * @param filepath
     * @param album
     * @param artist
     * @param lastPlayed
     * @param playCount
     * @return the song id for the song that was just inserted or -1 if there
     * was some error.
     */
    public int insertSong( String name, String filepath, String album,
                            String artist, int lastPlayed, int playCount){
        
        // Build the parameters table for the template
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("name", '\'' + name + '\'');
        params.put("filepath",  '\'' + filepath + '\'');
        params.put("album",  '\'' + album + '\'');
        params.put("artist",  '\'' + artist + '\'');
        params.put("lastplayed", Integer.toString(lastPlayed));
        params.put("playcount", Integer.toString(playCount));
        
        String completedSQL = DatabaseHelper.SQLBuilder(insertSQLTemplate, params);
        int songId = -1;
        try {
            Statement stmt = dbConn.getDBConnection().createStatement();
            stmt.execute(completedSQL);
            stmt.execute("SELECT last_insert_rowid()");
            songId = stmt.getResultSet().getInt(1);
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Could not insert the song into the database.");
            e.printStackTrace();
        }
        return songId;
    }
    
    public boolean updateSong( int songId, String name, String filepath,
                               String album, String artist, int lastPlayed,
                               int playCount){
        
        // Build the parameters table for the template
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("name", '\'' + name + '\'');
        params.put("filepath",  '\'' + filepath + '\'');
        params.put("album",  '\'' + album + '\'');
        params.put("artist",  '\'' + artist + '\'');
        params.put("lastplayed", Integer.toString(lastPlayed));
        params.put("playcount", Integer.toString(playCount));
        params.put("songid", Integer.toString(songId));
        
        String completedSQL = DatabaseHelper.SQLBuilder(updateSQLTemplate, params);
        try {
            Statement stmt = dbConn.getDBConnection().createStatement();
            stmt.execute(completedSQL);
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Could not update the song in the database.");
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public boolean addMetaData( String songId, String metaTag, String value){
        return false;
    }
}
