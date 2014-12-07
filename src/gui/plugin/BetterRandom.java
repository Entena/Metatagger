package gui.plugin;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.JLabel;
import javax.swing.JPanel;

import db.DBSong;
import db.DatabaseModel;

public class BetterRandom implements LearningPlugin {
	private DatabaseModel dbModel;
	private DBSong prevSongPlayed;
	private DBSong lastrec;
	private ArrayBlockingQueue<DBSong> history = new ArrayBlockingQueue<DBSong>(10);
	
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
		double [][] songs; int songNum = 0;
		if(songIDs.size() != 0){
			songs = new double[songIDs.size()][2];
			songNum = songIDs.size();
			for(int i=0; i < songIDs.size(); i++){
				int dist = Math.abs(prevSongPlayed.getBPM() - dbModel.getSong(songIDs.get(i)).getBPM());
				songs[i][0] = songIDs.get(i);
				songs[i][1] = ((double)dist/15) * 75;
				//System.out.println("Song "+dbModel.getSong((int)songs[i][0]).getName()+" scored "+songs[i][1]);
			}			
		} else {
			songs = null;
		}
		DBSong song;
		for(int j=0; j < songNum; j++){
			song = dbModel.getSong((int)songs[j][0]);
			if(song.getArtist().equals(prevSongPlayed.getArtist())){
				songs[j][1] += 25;
			}
		}
		int max = getMax(songs, songNum);
		song = dbModel.getSong((int)songs[max][0]);
		int count = 1;
		System.out.println("Song Rec "+song.getName());
		System.out.println("History "+history.toString());
		while(history.contains(song)){
			if(count > history.size()){
				System.out.println("Too much collision returning "+dbModel.getSong(index).getName());
				return dbModel.getSong(index);
			}
			System.out.println("History contains "+song.getName());
			songs[max][1] = 0;
			max = getMax(songs, songNum);
			song = dbModel.getSong((int)songs[max][0]);
			count++;
		}
		history.add(song);
		System.out.println("Recommending "+song.getName()+" with a score of "+songs[max][0]);
		return song;
		/*//songIDs = dbModel.getSongFromMetaQuery("artist", prevSongPlayed.getArtist());
		int max;
		if(songIDs.size() != 0){
			if(songs == null){//No songs in the BPM range so just return the first artist.
				return dbModel.getSong(songIDs.get(0));
			} else {
				for(int j=0; j < songNum; j++){
					DBSong song = dbModel.getSong((int)songs[j][0]);
					if(song.getArtist().equals(prevSongPlayed.getArtist())){
					//if(songIDs.contains((int)songs[j][0])){
						songs[j][1] += 25;
					}
				}
			}
		}
		max = getMax(songs,songNum);
		if((int)songs[max][0] == prevSongPlayed.getSongId()){
			songs[max][1] = 0;
			max = getMax(songs, songNum);
		}
		if(lastrec != null){
			if((int)songs[max][0] == lastrec.getSongId()){
				songs[max][1] = 0;
				max = getMax(songs, songNum);
				lastrec = dbModel.getSong((int)songs[max][0]);
			} else {
				lastrec = dbModel.getSong((int)songs[max][0]);
			}
		} else {
			lastrec = dbModel.getSong((int)songs[max][0]);
		}
		System.out.println("Lastrec "+lastrec.getName());
		System.out.println("You want "+dbModel.getSong((int)songs[max][0]).getName()+" with a score of:"+songs[max][1]);
		return dbModel.getSong((int)songs[max][0]);*/
	}
	
	private int getMax(double[][] songs, int rows){
		int biggest = 0;
		for(int i=1; i < rows; i++){
			if(songs[biggest][1] < songs[i][1]){
				biggest = i;
			}
		}
		return biggest;
	}
	
	@Override
	public void setPrevSong(DBSong song) {
		history.add(song);
		prevSongPlayed = song;	
	}

}
