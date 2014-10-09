package db;

import java.sql.SQLException;



public class DBTest {

    private static boolean failed = false;
    private static String reason = "";
    
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
            System.exit(1);
        }
        System.out.println("Congradulations! All the tests have passed.");
    }
    
    public static void runTests(){
        System.out.println("Creating and opening sqlite db connector...");
        DatabaseConnector dbConn = new SQLiteDatabaseConnector();
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
