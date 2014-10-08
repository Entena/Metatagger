/**
 * 
 */
package db;

/**
 * @author Tim Eck
 *
 */
public class DatabaseModel {

    private DatabaseConnector dbConn;
    
    /**
     * 
     */
    public DatabaseModel(DatabaseConnector dbConn) {
        this.dbConn = dbConn;
    }
    
    /**
     * Inserts a song into the database.
     * @param name
     * @param filepath
     * @param album
     * @param artist
     * @param lastPlayed
     * @param playCount
     */
    public void insertSong( String name, String filepath, String album,
                            String artist, int lastPlayed, int playCount){
        
    }

}
