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
		double score = 0;
		int index = (int) (Math.random() * songIDs.size());
		if(status == FinishedSongStatus.SKIPPED){						
			if(prevSongPlayed == null){//No information so random it is.	
				return dbModel.getSong(index);
			}
		}		
		lowerBound = prevSongPlayed.getBPM() - 15;
		upperBound = prevSongPlayed.getBPM() + 15;
		songIDs = dbModel.getSongFromBPMRange(lowerBound, upperBound);
		int testID = -1;
		double [][] songs;
		if(songIDs.size() != 0){
			songs = new double[songIDs.size()][2];
			//int dist = Math.abs(prevSongPlayed.getBPM() - dbModel.getSong(songIDs.get(0)).getBPM());			
			/*for(int i=1; i < songIDs.size(); i++){
				if(Math.abs(prevSongPlayed.getBPM() - dbModel.getSong(songIDs.get(i)).getBPM()) < dist){
					dist = Math.abs(prevSongPlayed.getBPM() - dbModel.getSong(songIDs.get(i)).getBPM());
					testID = songIDs.get(i);
				}
			}*/
			for(int i=0; i < songIDs.size(); i++){
				int dist = Math.abs(prevSongPlayed.getBPM() - dbModel.getSong(songIDs.get(i)).getBPM());
				songs[i][0] = songIDs.get(i);
				songs[i][1] = ((double)dist/15) * 75;
			}
			
			//return dbModel.getSong(songIDs.get(0));
		} else {
			songs = null;
		}
		songIDs = dbModel.getSongFromMetaQuery("artist", prevSongPlayed.getArtist());
		if(songIDs.size() != 0){
			if(songs == null){//No songs in the BPM range so just return the first artist.
				return dbModel.getSong(songIDs.get(0));
			}
			if(songIDs.contains(new Integer(testID))){
				score += 25;
			}
			//return dbModel.getSong(songIDs.get(0));
		}
		return dbModel.getSong(songIDs.get(testID));
	}

	@Override
	public void setPrevSong(DBSong song) {
		prevSongPlayed = song;	
	}

}
