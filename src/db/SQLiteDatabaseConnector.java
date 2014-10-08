/**
 * 
 */
package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Tim Eck
 *
 */
public class SQLiteDatabaseConnector implements DatabaseConnector {

    Connection connection = null;
    String dbName = "test.db";
    
    /**
     * 
     */
    public SQLiteDatabaseConnector() {
        // TODO Auto-generated constructor stub
    }

    @Override
    /**
     * Opens a connection to the database. This requires that the database has
     * already been created or that the db file can be created.
     */
    public void openDBConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Could not load the SQLite driver. Please make sure that the build path is properly configured.");
            e.printStackTrace();
            System.exit(100);
        }
        connection = DriverManager.getConnection("jdbc:sqlite:" + dbName);
    }

    @Override
    public void closeDBConnection() throws SQLException {
        connection.close();
    }
    
    /* (non-Javadoc)
     * @see db.DatabaseConnector#getDBConnection()
     */
    @Override
    public Connection getDBConnection() {
        return connection;
    }

}
