package db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This is a basic interface that all database connector classes will implement.
 * The idea is that we will create a simple abstraction layer over the db so that
 * we can swap out dbs without having to redo a lot of work higher up the stack.
 * 
 * @author Tim Eck
 *
 */
public interface DatabaseConnector {
    
    public void openDBConnection() throws SQLException;
    public void closeDBConnection() throws SQLException;
    public Connection getDBConnection();
}
