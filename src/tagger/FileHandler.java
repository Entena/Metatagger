package tagger;

import db.DBSong;
import db.DatabaseConnector;
import db.DatabaseModel;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import beatit.BPM;

public class FileHandler {
	private DatabaseConnector dbconnect;
	private DatabaseModel dbmodel;
	private Tagger tagger;
	
	public FileHandler(DatabaseConnector dbconnect){
		this.dbconnect = dbconnect;
		dbmodel = new DatabaseModel(dbconnect);
		tagger = new Tagger();
	}

	/**
	 * Add a new folder to scan for mp3s/autotagging. This method will return a list of all mp3s in a dir.
	 * @param dir is the directory
	 * @return a list of all mp3 in the dir/subdirs 
	 */
	public ArrayList<File> getMP3s(File dir){
		ArrayList<File> mp3s = new ArrayList<File>();
		buildList(dir.listFiles(), mp3s);
		return mp3s;
	}
	
	/**
	 * This helper function helps generate the list of files.
	 * @param files
	 * @param mp3s
	 */
	private void buildList(File[] files, ArrayList<File> mp3s) {
	    for (File file : files) {
	        if (file.isDirectory()) {	      
	            buildList(file.listFiles(), mp3s); // Calls same method again.
	        } else {
	            if(file.getName().contains(".mp3") == true){
	            	mp3s.add(file);
	            }
	        }
	    }
	}
	
	/**
	 * This method trims a list of mp3s and returns those missing metadata, the arraylist passed will be trimmed
	 * Thus we kill 2 birds with one stone. 
	 * @param mp3s list of mp3s not missing artist, title and bpm
	 * @return list of mp3s missing artist, title, and/or bpm
	 */
	public ArrayList<File> getIncomplete(ArrayList<File> mp3s){
		ArrayList<File> missingInfo = new ArrayList<File>();
		Iterator<File> it = mp3s.iterator();
		while(it.hasNext()){
			File mp3 = it.next();
			if(tagger.isMissingMeta(mp3) != 0){
				it.remove();
				System.out.println("Removing "+mp3.toString());
				//mp3s.remove(mp3);
				missingInfo.add(mp3);
			}
		}
		return missingInfo;
	}
	
	/**
	 * This method takes an arraylist of songs missing meta info and tries to update them if it can.
	 * @param missingInfo a list of mp3s missing meta info.
	 */
	public void identifyAndUpdateSongs(ArrayList<File> missingInfo){
		File song;
		for(int i=0; i < missingInfo.size(); i++){
			song = missingInfo.get(i);
			int cas = tagger.isMissingMeta(song);
			JSONObject songInfo;
			switch(cas){
				case  2://artist missing
				case  3://title missing
				case  5://artist/title missing
					songInfo = tagger.fingerPrint(song);
					if(songInfo != null)
						tagger.findSongInfo(songInfo);
				break;
				
				case 6://artist/bpm missing
				case 7://title/bpm missing
				case 9://all missing
					songInfo = tagger.fingerPrint(song);
					if(songInfo != null)
						tagger.findSongInfo(songInfo);
				case 4://bpm missing
					tagger.calculateAndUpdateBPM(song);
					System.out.println(tagger.getArtist(song)+" "+tagger.getBPM(song)+" "+tagger.getTitle(song));
					//tagger.updateBPM(BPM.getBPM(song.getAbsolutePath()), song.getAbsolutePath());
				break;
				default://Unrecognized issue
				break;
			}			 			
		}
	}
	
	/**
	 * This method enters songs into the database
	 * @param songs
	 */
	public void enterToDatabase(ArrayList<File> songs){
		for(int i=0; i<songs.size(); i++){
			File song = songs.get(i);
			String artist = tagger.getArtist(song);
			String title = tagger.getTitle(song);
			String bpm = tagger.getBPM(song);
			if(title.equals("")){
				title = song.getName();//If somehow this song has no title from echonest then set
				//it to file name
			}
			dbmodel.insertSong(title, song.getAbsolutePath(), tagger.getAlbum(song), artist, 0, 0, Integer.parseInt(bpm));
		}
	}
	
	/**
	 * This method enters songs to the database and returns a list of DBSongs
	 * This should be used to add to the database, not the initial dump of songs
	 * @param songs mp3 files to be added
	 * @return an ArrayList of DBSongs
	 */
	public ArrayList<DBSong> enterAndReturnIDs(ArrayList<File> songs){
		ArrayList<DBSong> ids = new ArrayList<DBSong>();
		for(int i=0; i<songs.size(); i++){
			File song = songs.get(i);
			String artist = tagger.getArtist(song);
			String title = tagger.getTitle(song);
			String bpm = tagger.getBPM(song);
			if(title.equals("")){
				title = song.getName();//If somehow this song has no title from echonest then set
				//it to file name
			}
			DBSong ret = dbmodel.insertSong(title, song.getAbsolutePath(), tagger.getAlbum(song), artist, 0, 0, Integer.parseInt(bpm));
			ids.add(ret);
		}
		return ids;
	}
	
}
