package tagger;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.jaudiotagger.audio.mp3.MP3File;
import org.json.JSONException;
import org.json.JSONObject;

import beatit.BPM;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.ID3v24Tag;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

public class Tagger {
	private ArrayList<File> music;
	private ArrayList<File> missingInfo;
	
	public Tagger(String musicFolder){
		try {
			//Path dir = FileSystems.getDefault().getPath(musicFolder);
			music = new ArrayList<File>();
			missingInfo = new ArrayList<File>();
			File dir = new File(musicFolder);
			File[] list = dir.listFiles();
			buildList(list);
			beginTagging();    
		} catch (DirectoryIteratorException x) {
		    System.err.println(x);
		    System.exit(-2);
		}
		
	}
	
	public void buildList(File[] files) {
	    for (File file : files) {
	        if (file.isDirectory()) {
	            //System.out.println("Directory: " + file.getName());
	            buildList(file.listFiles()); // Calls same method again.
	        } else {
	            //System.out.println("File: " + file.getName());
	            if(file.getName().contains(".mp3") == true){
	            	//System.out.println(file.getName());
	            	music.add(file);
	            }
	        }
	    }
	}
	
	/**
	 * This method begins the tagging process, it will isolate files missing tags
	 */
	public void beginTagging(){
		try {
			Mp3File mp3file;
			for(int i=0; i < music.size(); i++){
				//System.out.println(music.get(i).getAbsoluteFile().toString());
				mp3file = new Mp3File(music.get(i).getAbsoluteFile().toString());
				String title, album, artist;
				if(mp3file.hasId3v1Tag() || mp3file.hasId3v2Tag()){
					if(mp3file.hasId3v1Tag()){
						ID3v1 tagv1 = mp3file.getId3v1Tag();
						title = tagv1.getTitle();
						album = tagv1.getAlbum();
						artist = tagv1.getArtist();
					} else {
						ID3v2 tagv2 = mp3file.getId3v2Tag();
						title = tagv2.getTitle();
						album = tagv2.getAlbum();
						artist = tagv2.getArtist();
					}
					if(isMissingTag(music.get(i), title, artist, album)){
						music.remove(i);
					}
				} else {
					missingInfo.add(music.get(i));
					music.remove(i);
				}
				/*System.out.println("Length of this mp3 is: " + mp3file.getLengthInSeconds() + " seconds");
				System.out.println("Bitrate: " + mp3file.getLengthInSeconds() + " kbps " + (mp3file.isVbr() ? "(VBR)" : "(CBR)"));
				System.out.println("Sample rate: " + mp3file.getSampleRate() + " Hz");
				System.out.println("Has ID3v1 tag?: " + (mp3file.hasId3v1Tag() ? "YES" : "NO"));
				System.out.println("Has ID3v2 tag?: " + (mp3file.hasId3v2Tag() ? "YES" : "NO"));
				System.out.println("Has custom tag?: " + (mp3file.hasCustomTag() ? "YES" : "NO"));
				System.out.println("BPM "+BPM.getBPM(music.get(i).getAbsoluteFile().toString()));*/
			}
			//System.out.println("HIT");
			getTagInfo();
		} catch (UnsupportedTagException | InvalidDataException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	private boolean isMissingTag(File item, String title, String artist, String album){
		if(title == "" || artist == "" || album == ""){
			//if no tag exists it will be equal to an empty string "", missing info so add to get data list
			missingInfo.add(item);
			return true;
		}
		return false;
	}

	public void buildDB(ArrayList<File> mp3s){
		//TODO set up DB building code
	}
	
	/**
	 * This method is to be run after beginTagging, it will take problematic songs and get tag info.
	 */
	public void getTagInfo(){	
		PrinterGrabber pg = new PrinterGrabber();
		for(int i=0; i < missingInfo.size(); i++){
			String title, album, artist;
			JSONObject json = pg.fingerprint(missingInfo.get(i));
			System.out.println(json.toString());
		}
	}
}
