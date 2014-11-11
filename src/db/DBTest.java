package db;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;


/**
 * 
 * This file contains all the test code for the DB code. In addition to that
 * this file can act as a tutorial on how to do certain operations on the db.
 * To save time in figuring out the simple operations I will put them here.
 * But to learn them more in depth please look below or refer to the
 * documentation contained in the DatabaseModel class.<br/>
 * <br/>
 * Before doing anything you need to get and DatabaseConnector, the most basic
 * one is the SQLDatabaseConnector and set it up. To do that do the following:
 * 
 * <pre>
  DatabaseConnector dbConn = new SQLiteDatabaseConnector();
  try {
      dbConn.openDBConnection();
  } catch (SQLException e) {
      e.printStackTrace();
  }
 * </pre>
 * 
 * You need to always remember to call openDBConnection before trying to
 * perform any operations on the DB.<br/>
 * <br/>
 * If this is the first time running and you have not set up the database yet
 * then run the following:
 * 
 * <pre>
 DatabaseBuilder dbBuilder = new DatabaseBuilder(dbConn);
 dbBuilder.buildDatabase();
 * </pre>
 * 
 * Now you will have a database that is ready and setup run queries against. Now
 * most operations will be done by using a DatabaseModel object. This object
 * allows you to do operations like insert songs and manipulate songs meta data.
 * To create a DatabaseModel object run the following:
 * 
 * <pre>
 DatabaseModel dbModel = new DatabaseModel(dbConn);
 * </pre>
 * 
 * Now you can use that object to do something, like say insert a song into the
 * database. In order to do that do the following:
 * <pre>
 DBSong song = dbModel.insertSong("Eye of the Tiger",
                                  "c:/users/me/music/eye_of_the_tiger.mp3",
                                  "Single Release", "Survivor", 1111, 10);
 * </pre>
 * 
 * Now the from Eye of the Tiger is inserted into the database. The function
 * returns a song object. This object will be used in most other song related
 * functions to refer to the song in the database. Look at the docs for the
 * various DatabaseModel functions to figure out what else that you can do.<br/>
 * <br/>
 * Once you are all done doing database operations remember to close the
 * database connector that you have created. You can do that by doing the
 * following:
 * 
 * <pre>
  try {
      dbConn.closeDBConnection();
  } catch (SQLException e) {
      e.printStackTrace();
  }
 * </pre>
 * 
 * Now putting the whole example together:
 * 
 * <pre>
  DatabaseConnector dbConn = new SQLiteDatabaseConnector();
  try {
      dbConn.openDBConnection();
  } catch (SQLException e) {
      e.printStackTrace();
  }
  
  DatabaseBuilder dbBuilder = new DatabaseBuilder(dbConn);
  dbBuilder.buildDatabase();
  
  DBSong song = dbModel.insertSong("Eye of the Tiger",
                                   "c:/users/me/music/eye_of_the_tiger.mp3",
                                   "Single Release", "Survivor", 1111, 10);
                                 
  try {
      dbConn.closeDBConnection();
  } catch (SQLException e) {
      e.printStackTrace();
  }
 * </pre>
 * 
 * If you would like to run custom sql statements you would doing something like
 * the following with an open database connector:
 * <pre>
 Statement stmt = dbConn.getDBConnection().createStatement();
 stmt.execute(yourSQL);
 stmt.close();
 * </pre>
 * 
 *  
 * @author Tim Eck
 *
 */
public class DBTest {

    private static boolean failed = false;
    private static String reason = "";
    private static String dbFile = "dbTest.db";
    
    
    private static DatabaseConnector dbConn;
    private static DatabaseModel dbModel;
    private static DatabaseBuilder dbBuilder;

    private static int insertCount = 0;
    private static DBSong song;
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        File file = new File(dbFile);
        if(file.exists())
            file.delete();
        
        System.out.println("Starting DB Tests...");
        runTests();
        System.out.println("Fnished running DB Tests...");
        
        if(failed){
            System.err.println("The database tests did not all pass!! :(");
            System.err.println("The reason for the failure was: ");
            System.err.println(reason);
        } else {
            System.out.println("Congradulations! All the tests have passed.");
        }
        
        file.delete();
    }
    
    private static void runTests(){
        if(!initializationTests()) return;
        
        if(!insertTests()) return;
        
        if(!selectTests()) return;
        
        if(!updateTests()) return;
        
        if(!deletionTests()) return;
        
        if(!shutdownTests()) return;
    }
    
    private static boolean initializationTests(){
        System.out.println("Creating and opening sqlite db connector...");
        dbConn = new SQLiteDatabaseConnector(dbFile);
        try {
            dbConn.openDBConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            reason = "Could not open the connector";
            failed = true;
            return false;
        }
        System.out.println("Sqlite db connector ready to use...");

        System.out.println("Building db tables...");
        dbBuilder = new DatabaseBuilder(dbConn);
        if(dbBuilder.buildDatabase()){
            System.out.println("DB tables have been created...");
        } else {
            reason = "Could not build database";
            failed = true;
            return false;
        }
        
        dbModel = new DatabaseModel(dbConn);
        
        return true;
    }

    private static boolean shutdownTests(){
        System.out.println("Destroying the database...");
        if(dbBuilder.destroyDatabase()){
            System.out.println("DB has been destroyed...");
        } else {
            reason = "Could not destroy the database";
            failed = true;
            return false;
        }
        
        try {
            dbConn.closeDBConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            reason = "Could not close the connector";
            failed = true;
            return false;
        }
        
        return true;
    }

    private static boolean shutdownTests(){
        System.out.println("Destroying the database...");
        if(dbBuilder.destroyDatabase()){
            System.out.println("DB has been destroyed...");
        } else {
            reason = "Could not destroy the database";
            failed = true;
            return false;
        }
        
        try {
            dbConn.closeDBConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            reason = "Could not close the connector";
            failed = true;
            return false;
        }
        
        return true;
    }

    private static boolean insertTests(){
        System.out.println("Inserting song...");
        song = dbModel.insertSong("foobar", "sdf/", "dsf",
                                  "dfs", 1111, 10, 100);
        if(song == null){
            reason = "The song was not inserted into the database";
            failed = true;
            return false;
        }
        insertCount++;
        
        System.out.println("Inserting song with special character...");
        DBSong specialCharSong = dbModel.insertSong("foobar's", "sdf/", "dsf",
                                                    "dfs", 1111, 20, 1000);
        if(specialCharSong == null){
            reason = "The song was not inserted into the database";
            failed = true;
            return false;
        }
        
        insertCount++;
        return true;
    }
    
    private static boolean selectTests(){
        System.out.println("Selecting songs...");
        
        ArrayList<Integer> ids = dbModel.getAllSongIds();
        if(ids.size() != insertCount){
            reason = "The wrong amount of ids were returned";
            failed = true;
            return false;
        }
        
        ArrayList<DBSong> songs = dbModel.getAllSongs();
        if(songs.size() != insertCount){
            reason = "The wrong amount of songs were returned";
            failed = true;
            return false;
        } else if (songs.get(0).getSongId() != song.getSongId()){
            reason = "The wrong song id was returned";
            failed = true;
            return false;
        }
        
        DBSong dbSong = dbModel.getSong(song.getSongId());
        if(dbSong.getSongId() != song.getSongId()){
            reason = "The wrong song id was returned";
            failed = true;
            return false;
        }
        
        ids = dbModel.getSongFromBPMRange(song.getBPM(), song.getBPM() + 1);
        if(ids.size() != 1){
            reason = "The wrong amount of ids were returned in bpm range";
            failed = true;
            return false;
        }
        
        ids = dbModel.getSongFromPlayCountRange( song.getPlayCount(),
                                                 song.getPlayCount() + 1);
        if(ids.size() != 1){
            reason = "The wrong amount of ids were returned in play count range";
            failed = true;
            return false;
        }
        
        return true;
    }
    
    private static boolean updateTests(){
        System.out.println("Updating song...");
        song.setPlayCount(11);
        boolean result = dbModel.updateSong(song);
        if(!result || song.isDirty()){
            reason = "The song was not update in the database";
            failed = true;
            return false;
        }
        return true;
    }
    
    private static boolean deletionTests(){
        System.out.println("Deleting song...");
        boolean result = dbModel.deleteSong(song.getSongId());
        if(!result){
            reason = "The song was not removed from the database";
            failed = true;
            return false;
        }
        
        return true;
    }
}
