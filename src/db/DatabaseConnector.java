package db;

/**
 * This is a basic interface that all database connector classes will implement.
 * The idea is that we will create a simple abstraction layer over the db so that
 * we can swap out dbs without having to redue a lot of work higher up the stack.
 * 
 * @author Tim Eck
 *
 */
public interface DatabaseConnector {
    public void getDBConnection();
}
