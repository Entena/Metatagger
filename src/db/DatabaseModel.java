/**
 * 
 */
package db;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Tim Eck
 *
 */
public class DatabaseModel {

    private DatabaseConnector dbConn;
    
    private String insertSongSQLTemplate = "";
    private String updateSongSQLTemplate = "";
    private String deleteSongSQLTemplate = "";
    
    /**
     * 
     */
    public DatabaseModel(DatabaseConnector dbConn) {
        this.dbConn = dbConn;
        
        // Load the insert sql template file
        try {
            insertSongSQLTemplate = DatabaseHelper.SQLFromFile(
                                                DatabaseHelper.SQL_FOLDER_PATH +
                                                "insert_song_template.sql");
            updateSongSQLTemplate = DatabaseHelper.SQLFromFile(
                                                DatabaseHelper.SQL_FOLDER_PATH +
                                                "update_song_template.sql");
            deleteSongSQLTemplate = DatabaseHelper.SQLFromFile(
                                                DatabaseHelper.SQL_FOLDER_PATH +
                                                "delete_song_template.sql");
            
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
     * @return the song object or null.
     */
    public synchronized DBSong insertSong(
                              String name, String filepath, String album,
                              String artist, int lastPlayed, int playCount){
        
        // Build the parameters table for the template
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("name", '\'' + name + '\'');
        params.put("filepath",  '\'' + filepath + '\'');
        params.put("album",  '\'' + album + '\'');
        params.put("artist",  '\'' + artist + '\'');
        params.put("lastplayed", Integer.toString(lastPlayed));
        params.put("playcount", Integer.toString(playCount));
        
        String completedSQL = DatabaseHelper.SQLBuilder(insertSongSQLTemplate,
                                                        params);
        DBSong song = null;
        try {
            Statement stmt = dbConn.getDBConnection().createStatement();
            stmt.execute(completedSQL);
            stmt.execute("SELECT last_insert_rowid()");
            song = new DBSong( stmt.getResultSet().getInt(1), name, filepath,
                               album, artist, lastPlayed, playCount);
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Could not insert the song into the database.");
            e.printStackTrace();
        }
        return song;
    }
    
    /**
     * Given a DBSong, updates it corresponding record in the database.
     * Afterwards the song.isDirty() should return false if the operation was
     * successful
     * @param song The song with updated fields to be synced up in the db
     * @return true if the song was successfully updated, false otherwise
     */
    public boolean updateSong(DBSong song){
        String completedSQL = DatabaseHelper.SQLBuilder(updateSongSQLTemplate,
                                                        song.getSongParams());
        try {
            Statement stmt = dbConn.getDBConnection().createStatement();
            stmt.execute(completedSQL);
            stmt.close();
            song.markClean();
        } catch (SQLException e) {
            System.err.println("Could not update the song in the database.");
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public ArrayList<Integer> getAllSongIds(){
        ArrayList<Integer> ids = new ArrayList<Integer>();
        
        return ids;
    }
    
    /**
     * Given a song's id, deletes it and it's meta data from the db
     * @param songId The id of the song to remove
     * @return true is the song and it's meta data were removed, false
     * otherwise.
     */
    public boolean deleteSong(int songId){
        
        // Build the parameters table for the template
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("songid", Integer.toString(songId));
        
        String completedSQL = DatabaseHelper.SQLBuilder(deleteSongSQLTemplate,
                                                        params);
        try {
            Statement stmt = dbConn.getDBConnection().createStatement();
            stmt.execute(completedSQL);
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Could not delete the song in the database.");
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public
    
    public boolean addMetaData( String songId, String metaTag, String value){
        return false;
    }
}
