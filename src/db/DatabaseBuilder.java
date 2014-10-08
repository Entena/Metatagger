/**
 * 
 */
package db;

import java.io.IOException;
import java.sql.SQLException;

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
        String sql = "";
        try {
            sql = DatabaseHelper.SQLFromFile(DatabaseHelper.SQL_FOLDER_PATH + "create_db.sql");
        } catch (IOException e) {
            System.err.println("Could not load the sql script to build the database tables. Make sure that the files are accecible.");
            e.printStackTrace();
            return false;
        } 
        try {
            dbConn.executeSQL(sql);
        } catch (SQLException e) {
            System.err.println("The database tables could not be created.");
            e.printStackTrace();
            return false;
        }
        return true;
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
