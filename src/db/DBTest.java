package db;

import java.io.File;
import java.sql.SQLException;



public class DBTest {

    private static boolean failed = false;
    private static String reason = "";
    private static String dbFile = "dbTest.db";
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("Starting DB Tests...");
        System.out.println(System.getProperty("user.dir"));
        runTests();
        System.out.println("Fnished running DB Tests...");
        
        if(failed){
            System.err.println("The database tests did not all pass!! :(");
            System.err.println("The reason for the failure was: ");
            System.err.println(reason);
        } else {
            System.out.println("Congradulations! All the tests have passed.");
        }
        File file = new File(dbFile);
        file.delete();
    }
    
    public static void runTests(){
        System.out.println("Creating and opening sqlite db connector...");
        DatabaseConnector dbConn = new SQLiteDatabaseConnector(dbFile);
        try {
            dbConn.openDBConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            reason = "Could not open the connector";
            failed = true;
            return;
        }
        System.out.println("Sqlite db connector ready to use...");

        System.out.println("Building db tables...");
        DatabaseBuilder dbBuilder = new DatabaseBuilder(dbConn);
        if(dbBuilder.buildDatabase()){
            System.out.println("DB tables have been created...");
        } else {
            reason = "Could not build database";
            failed = true;
            return;
        }
        
        
        System.out.println("Inserting song...");
        DatabaseModel dbModel = new DatabaseModel(dbConn);
        int songId = dbModel.insertSong("foobar", "sdf/", "dsf",
                                        "dfs", 1111, 10);
        if(!(songId >= 0)){
            reason = "The song was not inserted into the database";
            failed = true;
            return;
        }
        
        
        
        System.out.println("Destroying the database...");
        if(dbBuilder.destroyDatabase()){
            System.out.println("DB has been destroyed...");
        } else {
            reason = "Could not destroy the database";
            failed = true;
            return;
        }
        
        try {
            dbConn.closeDBConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            reason = "Could not close the connector";
            failed = true;
            return;
        }
    }

}
