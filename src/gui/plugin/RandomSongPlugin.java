package gui.plugin;

import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

import db.DBSong;
import db.DatabaseModel;

public class RandomSongPlugin implements LearningPlugin {

	private DatabaseModel dbModel;
	
	public RandomSongPlugin() {
		
	}
	
	public String getName() {
		return "Random Songs";
	}

	public String getDescription() {
		return "Plays a random song";
	}

	public boolean initialize(DatabaseModel dbModel) {
		this.dbModel = dbModel;
		return dbModel != null;
	}

	public void tearDown() {
		
	}

	public JPanel getConfigPanel() {
		JPanel panel = new JPanel();
		JLabel label = new JLabel("IT'S RANDOM YOU IDIOT!");
		panel.add(label);
		return panel;
	}

	public DBSong getNextSong(FinishedSongStatus status) {
		System.out.println(status.toString());
		ArrayList<Integer> songIDs = dbModel.getAllSongIds();
		int index = (int) (Math.random() * songIDs.size());
		
		return dbModel.getSong(songIDs.get(index));
	}

}
