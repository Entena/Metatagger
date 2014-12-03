package gui.plugin;

import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

import db.DBSong;
import db.DatabaseModel;

public class BetterRandom implements LearningPlugin {
	private DatabaseModel dbModel;
	private DBSong prevSongPlayed;
	
	@Override
	public String getName() {
		return "Learning Random";
	}

	@Override
	public String getDescription() {
		return "This algorithm will try to generate results that best match your mood based of your history.";
	}

	@Override
	public boolean initialize(DatabaseModel dbModel) {
		this.dbModel = dbModel;
		return dbModel != null;
	}

	@Override
	public void tearDown() {	}

	@Override
	public JPanel getConfigPanel() {
		JPanel panel = new JPanel();
		JLabel label = new JLabel("This algorithm will try to generate results that best match your mood based of your history.");
		panel.add(label);
		return panel;
	}

	@Override
	public DBSong getNextSong(FinishedSongStatus status) {
		ArrayList<Integer> songIDs;
		int lowerBound, upperBound;
		songIDs = dbModel.getAllSongIds();
		int index = (int) (Math.random() * songIDs.size());
		if(status == FinishedSongStatus.SKIPPED){						
			if(prevSongPlayed == null){				
				return dbModel.getSong(index);
			}
		}		
		lowerBound = prevSongPlayed.getBPM() - 15;
		upperBound = prevSongPlayed.getBPM() + 15;
		songIDs = dbModel.getSongFromBPMRange(lowerBound, upperBound);
		if(songIDs.size() != 0){
			return dbModel.getSong(songIDs.get(0));
		}
		songIDs = dbModel.getSongFromMetaQuery("artist", prevSongPlayed.getArtist());
		if(songIDs.size() != 0){
			return dbModel.getSong(songIDs.get(0));
		}
		return dbModel.getSong(index);
	}

	@Override
	public void setPrevSong(DBSong song) {
		prevSongPlayed = song;	
	}

}
