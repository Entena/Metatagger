package gui.plugin;

import java.util.ArrayList;
import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JPanel;

import db.DBSong;
import db.DatabaseModel;

/**
 * This plugin randomly picks two songs. One to play again and again, and
 * another to play every now and again to break up the playlist.
 */
public class SaltNPepperPlugin implements LearningPlugin {

	private DatabaseModel dbModel;
	private Random rand;
	private DBSong mainSong;
	private DBSong altSong;
    private int interval;
	private int intervalCount;
	private int cycle;
	private int cycleCount;
	
	public SaltNPepperPlugin() {
		rand = new Random();
	}
	
	public String getName() {
		return "Salt and Pepper Dinner";
	}

	public String getDescription() {
		return "Why not try me out!";
	}

	public boolean initialize(DatabaseModel dbModel) {
		this.dbModel = dbModel;
		cycleCount = cycle; // Makes sure that set up happens in getNextSong
		return true;
	}

	public void tearDown() { }

	public JPanel getConfigPanel() {
		JPanel panel = new JPanel();
		JLabel label = new JLabel(":)");
		panel.add(label);
		return panel;
	}

	public DBSong getNextSong(FinishedSongStatus status) {
		
	    if(cycleCount == cycle){
	        cycleCount = 0;
	        intervalCount = 0;
	        
	        interval = rand.nextInt(7) + 1;
	        cycle = rand.nextInt(3) + 1;
	        
	        ArrayList<Integer> songIDs = dbModel.getAllSongIds();
	        
	        mainSong = dbModel.getSong(songIDs.get(rand.nextInt(songIDs.size())));
            altSong = dbModel.getSong(songIDs.get(rand.nextInt(songIDs.size())));
	    }
	    
	    if(intervalCount < interval){
	        intervalCount++;
	        return mainSong;
	    } else { // intervalCount == interval
	        intervalCount = 0;
	        cycleCount++;
	        return altSong;
	    }
	}

	@Override
	public void setPrevSong(DBSong song) {
		// TODO Auto-generated method stub
		
	}

}
