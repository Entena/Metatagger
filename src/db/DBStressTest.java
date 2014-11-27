package db;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;


/**
 * 
 * This file contains all the stress test code for the DB code. 
 *  
 * @author Tim Eck
 *
 */
public class DBStressTest {

    private static ArrayList<Result> results;
    private static String dbFile = "dbTest.db";
    
    
    private static DatabaseConnector dbConn;
    private static DatabaseModel dbModel;
    private static DatabaseBuilder dbBuilder;
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        File file = new File(dbFile);
        if(file.exists())
            file.delete();
        
        System.out.println("Starting DB Stess Tests...");
        results = new ArrayList<Result>();
        long start = System.currentTimeMillis();
        runTests();
        long end = System.currentTimeMillis();
        long elapse = end - start;
        System.out.println("\n\nFnished running DB Stess Tests...");
        System.out.format("It took the tests %02d:%02d:%02d to run\n", 
        		TimeUnit.MILLISECONDS.toHours(elapse),
        	    TimeUnit.MILLISECONDS.toMinutes(elapse) % TimeUnit.HOURS.toMinutes(1),
        	    TimeUnit.MILLISECONDS.toSeconds(elapse) % TimeUnit.MINUTES.toSeconds(1));
        System.out.println("Results:");
        System.out.printf("%-64s%s\n", "Test Name", "Elapse Time");
        System.out.println(String.format("%74s", "").replace(' ', '-'));
        for(Result result : results) {
        	String name = result.name + String.format("%" + (64 - result.name.length()) + "s", "").replace(' ', '.');
        	System.out.format("%s%-10d\n", name, result.elapse);
        }
        file.delete();
    }
    
    private static void runTests(){
        insertTests();
        
        selectTests();
        
        updateTests();
        
        deletionTests();
    }
    
    private static void initializationTests(){
        dbConn = new SQLiteDatabaseConnector(dbFile);
        try {
            dbConn.openDBConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        dbBuilder = new DatabaseBuilder(dbConn);
        if(!dbBuilder.buildDatabase()){
            System.exit(1);
        }
        
        dbModel = new DatabaseModel(dbConn);
    }

    private static void shutdownTests(){
        if(!dbBuilder.destroyDatabase()){
            System.exit(1);
        }
        
        try {
            dbConn.closeDBConnection();
        } catch (SQLException e) {
            System.exit(1);
        }
    }

    private static void insertTests(){
        System.out.println("Inserting song...");
        
        System.out.println("Testing 1000...");
        measuredInsertTest( "Inserting 1000 songs (no special chars)", 1000, 1,
							"foobars", "sdf/", "dsf", "dfs", 1111, 20, 1000);
        System.out.println("Testing 10000...");
    	measuredInsertTest( "Inserting 10000 songs (no special chars)", 10000, 1,
    						"foobars", "sdf/", "dsf", "dfs", 1111, 20, 1000);
        

        System.out.println("Testing 1000...");
    	measuredInsertTest( "Inserting 1000 songs (special chars)", 1000, 1,
    						"foobar's", "sdf/", "dsf", "dfs", 1111, 20, 1000);
        System.out.println("Testing 10000...");
    	measuredInsertTest( "Inserting 10000 songs (special chars)", 10000, 1,
    						"foobar's", "sdf/", "dsf", "dfs", 1111, 20, 1000);
    }
    
    private static void measuredInsertTest( String testName, int insertCount,
    										int threadCount,String name,
    										String filepath, String album,
    										String artist, int lastPlayed, 
    										int playCount, int bpm) {
        initializationTests();
    	long start = System.currentTimeMillis();
        for(int i = 0; i < insertCount; i++) {
	        DBSong song = dbModel.insertSong( name, filepath, album, artist, lastPlayed,
	        						   playCount, bpm);
	        if(song == null){
	            System.err.printf(
	                 "There was an error inserting on the %dth song.\n", i + 1);
	            System.exit(1);
	        }
        }
        long end = System.currentTimeMillis();
        
        results.add(new Result(testName + " (s)", (end - start) / 1000));
        results.add(new Result("Average Insert Time", (end - start) / insertCount));
        shutdownTests();
    }
    
    private static void selectTests(){
        System.out.println("Selecting songs...");

        System.out.println("Testing 1000...");
        measuredSelectionTest("Selecting with 1000 songs", 1000, 1);
        System.out.println("Testing 10000...");
        measuredSelectionTest("Selecting with 10000 songs", 10000, 1);
    }
    
    private static void measuredSelectionTest( String testName, int setSize,
    										   int threadCount) {
        initializationTests();
    	
        // Insert songs into the database
        insertSongs(setSize);
        
        // Select all the song ids in the database
        long start = System.currentTimeMillis();
        ArrayList<Integer> songIds = dbModel.getAllSongIds();
        long end = System.currentTimeMillis();
        results.add(new Result(testName + ": select all song ids", end - start));
        
        // Select all the song objects in the database
        start = System.currentTimeMillis();
        ArrayList<DBSong> songs = dbModel.getAllSongs();
        end = System.currentTimeMillis();
        results.add(new Result(testName + ": select all song objects", end - start));
        
        
        // Select all the songs individually by id
        DBSong song;
        start = System.currentTimeMillis();
        for(int id : songIds) {
        	song = dbModel.getSong(id);
        }
        end = System.currentTimeMillis();
        results.add(new Result(testName + ": select each song individually", end - start));
        results.add(new Result(testName + ": average selection time", (end - start) / setSize));
        
        // Query for 100 songs based upon bpm
        int bpmBase = 1000 + setSize / 2;
        int bpm;
        start = System.currentTimeMillis();
        for(int i = 0; i < 100; i++) {
        	bpm = bpmBase + i;
        	songIds = dbModel.getSongFromBPMRange(bpm, bpm);
        }
        end = System.currentTimeMillis();
        results.add(new Result(testName + ": select 100 songs by bpm (s)", end - start));
        results.add(new Result(testName + ": average selection time", (end - start) / 100));
        
        shutdownTests();
    }
    
    private static void updateTests(){
        System.out.println("Updating song...");

        System.out.println("Testing 1000...");
        measuredUpdateTests("Update with 1000 songs", 1000, 1);
        System.out.println("Testing 10000...");
        measuredUpdateTests("Update with 10000 songs", 10000, 1);
        
    }
    
    private static void measuredUpdateTests(String testName, int setSize, int threadCount) {
        initializationTests();
    	// Insert songs into the database
        insertSongs(setSize);
        
        // Update the songs with out measurement
        ArrayList<DBSong> songs = dbModel.getAllSongs();
        for(DBSong song : songs) {
        	song.setPlayCount(99);
        }
        
        // Measure length to just update songs
        long start = System.currentTimeMillis();
        for(DBSong song : songs) {
        	dbModel.updateSong(song);
        }
        long end = System.currentTimeMillis();
        results.add(new Result(testName + ": update all songs (s)", (end - start) / 1000));
        results.add(new Result(testName + ": average update time", (end - start) / setSize));
        
        shutdownTests();
    }
    
    private static void deletionTests(){
        System.out.println("Deleting song...");

        System.out.println("Testing 1000...");
        measuredDeletionTest("Delete with 1000 songs", 1000, 1);
        System.out.println("Testing 10000...");
        measuredDeletionTest("Delete with 10000 songs", 10000, 1);
        
    }
    
    private static void measuredDeletionTest(String testName, int setSize, int threadCount) {
        initializationTests();
    	
        // Delete all songs from db
        insertSongs(setSize);
        ArrayList<Integer> songIds = dbModel.getAllSongIds();
        long start = System.currentTimeMillis();
        for(Integer songId : songIds) {
        	dbModel.deleteSong(songId);
        }
        long end = System.currentTimeMillis();
        results.add(new Result(testName + ": delete all songs (s)", (end - start) / 1000));
        results.add(new Result(testName + ": average delete time", (end - start) / setSize));
        
        // Delete 100 random songs
        insertSongs(setSize);
        songIds = dbModel.getAllSongIds();
        ArrayList<Integer> songsToDelete = new ArrayList<Integer>();
        Random rand = new Random();
        for(int i = 0; i < 100; i++) {
        	int index = rand.nextInt(songIds.size());
        	songsToDelete.add(songIds.get(index));
        	songIds.remove(index);
        }
        start = System.currentTimeMillis();
        for(Integer id : songsToDelete) {
        	dbModel.deleteSong(id);
        }
        end = System.currentTimeMillis();
        results.add(new Result(testName + ": randomly delete 100 songs (s)", (end - start) / 1000));
        results.add(new Result(testName + ": average delete time", (end - start) / 100));
    	
        shutdownTests();
    }
    
    private static void insertSongs(int count) {
    	int processors = Runtime.getRuntime().availableProcessors();
    	Thread[] threads = new Thread[processors];
		int size = count / 2;
    	for(int i = 0; i < processors; i++) {
    		threads[i] = new InsertThread(size, size * i);
    		threads[i].start();
    	}
    	
    	for(Thread t : threads) {
    		try {
				t.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    
    static class Result{
        public String name;
        public long elapse;
        
        public Result(String name, long elapse) {
            this.name = name;
            this.elapse = elapse;
        }
    }
    
    static class InsertThread extends Thread {
    	
    	public int count;
    	public int baseVal;
    	
    	public InsertThread(int count, int baseVal) {
    		this.count = count;
    		this.baseVal = baseVal;
    	}

		@Override
		public void run() {
			for(int i = 0; i < count; i++) {
		        DBSong song = dbModel.insertSong( "foobars" + count + baseVal,
		        								  "sdf/" + count  + baseVal,
		        								  "dsf", "dfs",
		        								  1111 + count + baseVal,
		        								  20 + count + baseVal,
		        								  1000 + count + baseVal);
		        if(song == null){
		            System.err.printf(
		                 "There was an error inserting on the %dth song.\n", i + 1);
		            System.exit(1);
		        }
	        }
		}
    	
    }
}
