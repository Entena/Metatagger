package tagger;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.json.JSONObject;

import beatit.BPM;

import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Params;
import com.echonest.api.v4.Song;


public class Tagger {	
	
	public Tagger(){
		
	}
	
	
	/**
	 * Tells you if your information is missing any "crucial" metadata (BPM, title, or artist)
	 * @param mp3 the file to be checked
	 * @return 0 if nothing missing, 2 if just artist missing, 3 if just title missing, 4 if just bpm missing
	 * 5 if artist/title missing, 6 if artist/bpm missing, 7 if title/bpm missing, 9 if all missing
	 */
	public int isMissingMeta(File mp3){
		try {
			String title, artist, bpm;
			AudioFile f = AudioFileIO.read(mp3);
			Tag tag = f.getTag();
			if(tag == null){
				return 9;//No tag what a drag.
			}
			artist = tag.getFirst(FieldKey.ARTIST);
			title = tag.getFirst(FieldKey.TITLE);
			bpm = tag.getFirst(FieldKey.BPM);
			int ret = 0;
			if(artist.equals("")){
				ret = ret + 2;//2+3 = 5, 2+4 = 6, 3+4 =7, 
			}
			if(title.equals("")){
				ret = ret + 3;
			}
			if(bpm.equals("")){
				ret = ret + 4;
			}
			return ret;
		}catch(Exception e){
			return 0;
		}
	}
	
	/**
	 * This function fingerprints a list of files and returns an arraylist of the json info
	 * DO NOT GIVE THIS FUNCTION ANYTHING BUT MP3s!
	 * @param files is a list of MP3s. NOTE DO NOT PROVIDE OTHER FILES TO THIS FUNCTION
	 * @return null if no information can be found, else an arraylist of jsonobjects from echoprint
	 */
	public ArrayList<JSONObject> fingerPrint(File[] files){
		ArrayList<JSONObject> jsonInfo = new ArrayList<JSONObject>(files.length);
		for(int i=0; i < files.length; i++){
			JSONObject song = fingerPrint(files[i]);
			if(song != null){
				jsonInfo.add(song);
			}
		}
		if(jsonInfo.size() > 0){
			return jsonInfo;
		} else {
			return null;
		}
	}
	
	/**
	 * Uses echoprint to fingerprint a song and returns the meta information in a json object
	 * if json object is null then echoprint failed
	 * @param mp3
	 * @return
	 */
	public JSONObject fingerPrint(File mp3){
		PrinterGrabber pg = new PrinterGrabber();
		return pg.fingerprint(mp3);
	}
	
	/**
	 * This function calculates the BPM/updates the BPM metatag
	 * @param file the MP3 to get the BPM for
	 * @return -1 if error, BPM if corect
	 */
	public int calculateAndUpdateBPM(File file){
		int bpm = BPM.getBPM(file.getAbsolutePath());
		try {
			AudioFile f = AudioFileIO.read(file);
			Tag tag = f.getTag();
			if(tag == null){
				tag = f.createDefaultTag();
			}
			tag.setField(FieldKey.BPM, Integer.toString(bpm));
			f.commit();
			return bpm;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}

	public void findSongInfo(ArrayList<JSONObject> songs){
		for(int i=0; i < songs.size(); i++){
			findSongInfo(songs.get(i));
		}
	}
	
	/**
	 * Fetches and updates meta information if echonest fetched it, will catch
	 * too many uses per minute exceptions, wait then try again.
	 * @param song
	 */
	public void findSongInfo(JSONObject song){
		try {
			JSONObject meta = new JSONObject(song.get("metadata").toString());
			EchoNestAPI en = new EchoNestAPI("B8NV7C9CDT8EYNPOM");
			Params p = new Params();
			String[] names = song.getNames(song);		
			for(int i=0; i<names.length; i++){
				System.out.println(names[i]);
			}/*
			names = meta.getNames(meta);
			for(int i=0; i<names.length; i++){
				System.out.println(names[i]);
			}*/
			p.add("code", song.getString("code"));
			p.add("genre", meta.getString("genre"));
			p.add("duration", meta.get("duration").toString());
			p.add("title", meta.getString("title"));
			p.add("filename", meta.getString("filename"));
			p.add("artist", meta.getString("artist"));
			p.add("release", meta.getString("release"));
			p.add("version", meta.get("version").toString());
			List<Song> songs = en.identifySongs(p);
			if(songs.size() > 0){
				updateTags(songs.get(0).getArtistName(), songs.get(0).getTitle(), meta.getString("filename"));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			if(e instanceof EchoNestException){
				if(((EchoNestException)e).getCode() == EchoNestException.ERR_RATE_LIMIT_EXCEEDED){
					try {
						Thread.sleep(1000*60);
						findSongInfo(song);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
			e.printStackTrace();
		}
	}
	
	/**
	 * Updates the artist/title tags of song
	 * @param artist
	 * @param title
	 * @param filename
	 */
	public void updateTags(String artist, String title, String filename){
		try {
			File file = new File(filename);
			AudioFile f = AudioFileIO.read(file);
			Tag tag = f.getTagOrCreateAndSetDefault();
			tag.setField(FieldKey.ARTIST, artist);
			tag.setField(FieldKey.TITLE, title);
			AudioFileIO.write(f);			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(filename);
			e.printStackTrace();
		}
	}
	
	/**
	 * Will update bpm of file, WILL NOT CALCULATE BPM
	 * @param bpm
	 * @param filename
	 */
	public void updateBPM(int bpm, String filename){
		try {
			AudioFile f = AudioFileIO.read(new File(filename));
			Tag tag = f.getTagOrCreateAndSetDefault();			
			tag.setField(FieldKey.BPM, Integer.toString(bpm));
			f.commit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Gets the title from the meta info
	 * @param song
	 * @return
	 */
	public String getTitle(File song){
		try {
			AudioFile f = AudioFileIO.read(song);
			Tag tag = f.getTag();
			return  tag.getFirst(FieldKey.TITLE);
		}catch(Exception e){
			return "";
		}
	}
	
	/**
	 * Gets the artist from the meta info
	 * @param song
	 * @return
	 */
	public String getArtist(File song){
		try {
			AudioFile f = AudioFileIO.read(song);
			Tag tag = f.getTag();
			return tag.getFirst(FieldKey.ARTIST);
		}catch(Exception e){
			return "";
		}	
	}
	
	/**
	 * Gets the BPM from the meta info
	 * @param song
	 * @return
	 */
	public String getBPM(File song){
		try {
			AudioFile f = AudioFileIO.read(song);
			Tag tag = f.getTag();
			return  tag.getFirst(FieldKey.BPM);
		}catch(Exception e){
			return "";
		}
	}
	
	/**
	 * Gets the album from the meta info
	 * @param song
	 * @return
	 */
	public String getAlbum(File song){
		try {
			AudioFile f = AudioFileIO.read(song);
			Tag tag = f.getTag();
			return  tag.getFirst(FieldKey.ALBUM);
		}catch(Exception e){
			return "";
		}
	}
}
