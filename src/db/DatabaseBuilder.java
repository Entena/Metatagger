/**
 * 
 */
package db;

/**
 * Helper class that will initialize the database during the first time running
 * the application.
 * 
 * @author Tim Eck
 *
 */
public class DatabaseBuilder {

    private DatabaseConnector dbConn;
    
    /**
     * 
     */
    public DatabaseBuilder(DatabaseConnector dbConn) {
        this.dbConn = dbConn;
    }
    
    /**
     * Builds the database from scratch.
     * @return true if the operation completed successfully, else false if
     * something went wrong.
     */
    public boolean buildDatabase(){
        
        return false;
    }
    
    /**
     * Destroys the database.
     * @return true if the operation completed successfully, else false if
     * something went wrong.
     */
    public boolean destroyDatabase(){
        return false;
    }

}
