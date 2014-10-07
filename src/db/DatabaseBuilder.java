/**
 * 
 */
package db;

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
        String sql = "CREATE TABLE COMPANY " +
                "(ID INT PRIMARY KEY     NOT NULL," +
                " NAME           TEXT    NOT NULL, " + 
                " AGE            INT     NOT NULL, " + 
                " ADDRESS        CHAR(50), " + 
                " SALARY         REAL)"; 
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
