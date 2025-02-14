/**
 * 
 */
package db;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;

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
            System.err.println("Could not load the sql script to build the database tables. Make sure that the sql files are accessible.");
            e.printStackTrace();
            return false;
        } 
        try {
            Statement stmt = dbConn.getDBConnection().createStatement();
            stmt.execute(sql);
            stmt.close();
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
        String sql = "";
        try {
            sql = DatabaseHelper.SQLFromFile(DatabaseHelper.SQL_FOLDER_PATH + "drop_tables.sql");
        } catch (IOException e) {
            System.err.println("Could not load the sql script to drop the database tables. Make sure that the sql files are accessible.");
            e.printStackTrace();
            return false;
        } 
        try {
            Statement stmt = dbConn.getDBConnection().createStatement();
            stmt.execute(sql);
            stmt.close();
        } catch (SQLException e) {
            System.err.println("The database tables could not be dropped.");
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
