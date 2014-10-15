/**
 * 
 */
package db;

import java.io.IOException;
import java.sql.ResultSet;
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
    
    private String selectSongSQLTemplate = "";
    private String selectAllSongsSQLTemplate = "";
    private String selectSongIdsSQLTemplate = "";
    
    private String selectSongIdsBPMRangeTemplate = "";
    private String selectSongIdsPlayCountRangeTemplate = "";
    
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
            

            selectSongSQLTemplate = DatabaseHelper.SQLFromFile(
                                                DatabaseHelper.SQL_FOLDER_PATH +
                                                "select_song_template.sql");
            selectAllSongsSQLTemplate = DatabaseHelper.SQLFromFile(
                                                DatabaseHelper.SQL_FOLDER_PATH +
                                                "select_all_songs_template.sql");
            selectSongIdsSQLTemplate = DatabaseHelper.SQLFromFile(
                                                DatabaseHelper.SQL_FOLDER_PATH +
                                                "select_song_ids_template.sql");
            
            selectSongIdsBPMRangeTemplate = DatabaseHelper.SQLFromFile(
                                            DatabaseHelper.SQL_FOLDER_PATH +
                                            "select_song_bpm_range_template.sql");
            selectSongIdsPlayCountRangeTemplate = DatabaseHelper.SQLFromFile(
                                    DatabaseHelper.SQL_FOLDER_PATH +
                                    "select_song_playcount_range_template.sql");
            
            
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
    public synchronized DBSong insertSong( String name, String filepath,
                                           String album, String artist,
                                           int lastPlayed, int playCount,
                                           int bpm){
        return insertSong(new DBSong( 0, name, filepath, album, artist,
                                      lastPlayed, playCount, bpm));
    }
    
    /**
     * Given a DBSong object, create a new song entry in the db using its field
     * values. Note that the songId field is ignored.
     * @param song
     * @return
     */
    public synchronized DBSong insertSong(DBSong song){
        String completedSQL = DatabaseHelper.SQLBuilder(insertSongSQLTemplate,
                                                        song.getSongParams());
        DBSong newSong = null;
        try {
            Statement stmt = dbConn.getDBConnection().createStatement();
            stmt.execute(completedSQL);
            stmt.execute("SELECT last_insert_rowid()");
            newSong = new DBSong( stmt.getResultSet().getInt(1), song.getName(),
                                  song.getFilepath(), song.getAlbum(),
                                  song.getArtist(), song.getLastPlayed(),
                                  song.getPlayCount(), song.getBPM());
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Could not insert the song into the database.");
            e.printStackTrace();
        }
        return newSong;
    }
    
    /**
     * Given a DBSong, updates it corresponding record in the database.
     * Afterwards the song.isDirty() should return false if the operation was
     * successful
     * @param song The song with updated fields to be synced up in the db
     * @return true if the song was successfully updated, false otherwise
     */
    public boolean updateSong(DBSong song){
        // Skip updating is the song is not dirty
        if(!song.isDirty())
            return true;
        
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
    
    /**
     * Queries the db asking to for all of the song ids that it currently has.
     * @return a list of song ids that can be used to get individual songs
     */
    public ArrayList<Integer> getAllSongIds(){
        ArrayList<Integer> ids = new ArrayList<Integer>();
        try {
            Statement stmt = dbConn.getDBConnection().createStatement();
            ResultSet result = stmt.executeQuery(selectSongIdsSQLTemplate);
            while(result.next()){
                ids.add(result.getInt(DatabaseHelper.SONG_ID_COLUMN));
            }
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Could not select the song ids from the database.");
            e.printStackTrace();
            return ids;
        }
        return ids;
    }
    
    /**
     * Gets all the songs for the database and then creates their corresponding
     * DBSong objects
     * @return
     */
    public ArrayList<DBSong> getAllSongs(){
        ArrayList<DBSong> songs = new ArrayList<DBSong>();
        try {
            Statement stmt = dbConn.getDBConnection().createStatement();
            ResultSet result = stmt.executeQuery(selectAllSongsSQLTemplate);
            while(result.next()){
                songs.add(dbsongFromResultSet(result));
            }
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Could not select the songs from the database.");
            e.printStackTrace();
            return songs;
        }
        return songs;
    }
    
    /**
     * Given a song id, attempts to select the song from the database and create
     * itself DBSong object.
     * @param songId The id of the song to select from the database.
     * @return
     */
    public DBSong getSong(int songId){
        // Build the parameters table for the template
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("songid", Integer.toString(songId));
        
        String completedSQL = DatabaseHelper.SQLBuilder(selectSongSQLTemplate,
                                                        params);
        DBSong song = null;
        try {
            Statement stmt = dbConn.getDBConnection().createStatement();
            stmt.execute(completedSQL);
            ResultSet result = stmt.getResultSet();
            song = dbsongFromResultSet(result);
            result.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Could not select the song from the database.");
            e.printStackTrace();
        }
        return song;
    }
    
    /**
     * Finds all the song ids of the songs that have bpm between the lower and
     * upper bounds inclusively.
     * @param lowerBound The lowest bpm you are looking for
     * @param upperBound The largest bpm you are looking for
     * @return
     */
    public ArrayList<Integer> getSongFromBPMRange(int lowerBound, int upperBound){
        ArrayList<Integer> ids = new ArrayList<Integer>();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("lowerbound", Integer.toString(lowerBound));
        params.put("upperbound", Integer.toString(upperBound));
        
        String completedSQL = DatabaseHelper.SQLBuilder(
                                                  selectSongIdsBPMRangeTemplate,
                                                  params);
        try {
            Statement stmt = dbConn.getDBConnection().createStatement();
            ResultSet result = stmt.executeQuery(completedSQL);
            while(result.next()){
                ids.add(result.getInt(DatabaseHelper.SONG_ID_COLUMN));
            }
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Could not select the song ids from the database.");
            e.printStackTrace();
        }
        return ids;
    }

    /**
     * Finds all the song ids of the songs that have a play count between the
     * lower and upper bounds inclusively.
     * @param lowerBound The lowest play count you are looking for
     * @param upperBound The largest play count you are looking for
     * @return
     */
    public ArrayList<Integer> getSongFromPlayCountRange( int lowerBound,
                                                         int upperBound){
        ArrayList<Integer> ids = new ArrayList<Integer>();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("lowerbound", Integer.toString(lowerBound));
        params.put("upperbound", Integer.toString(upperBound));
        
        String completedSQL = DatabaseHelper.SQLBuilder(
                                            selectSongIdsPlayCountRangeTemplate,
                                            params);
        try {
            Statement stmt = dbConn.getDBConnection().createStatement();
            ResultSet result = stmt.executeQuery(completedSQL);
            while(result.next()){
                ids.add(result.getInt(DatabaseHelper.SONG_ID_COLUMN));
            }
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Could not select the song ids from the database.");
            e.printStackTrace();
        }
        return ids;
    }
    
    /**
     * Helper function used by getAllSongs and getSong to transform a result
     * from the database into a usable java object.
     * @param result The result set that contains the information about the
     * song from the db
     * @return The completed song.
     */
    private DBSong dbsongFromResultSet(ResultSet result){
        try {
            return new DBSong( result.getInt(DatabaseHelper.SONG_ID_COLUMN),
                               result.getString(DatabaseHelper.NAME_COLUMN),
                               result.getString(DatabaseHelper.FILEPATH_COLUMN),
                               result.getString(DatabaseHelper.ALBUM_COLUMN),
                               result.getString(DatabaseHelper.ARTIST_COLUMN),
                               result.getInt(DatabaseHelper.LAST_PLAYED_COLUMN),
                               result.getInt(DatabaseHelper.PLAY_COUNT),
                               result.getInt(DatabaseHelper.BPM_COUNT));
        } catch (SQLException e) {
            System.err.println("Could not parse a song from the database.");
            e.printStackTrace();
            return null;
        }
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
    
    public boolean addMetaData( String songId, String metaTag, String value){
        return false;
    }
}
