package tagger;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.json.JSONException;
import org.json.JSONObject;

import beatit.BPM;

import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.IdentifySongParams;
import com.echonest.api.v4.Params;
import com.echonest.api.v4.Song;
import com.echonest.api.v4.Track;
import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.ID3v24Tag;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

public class Tagger {
	private ArrayList<File> music;
	private ArrayList<File> missingInfo;
	private ArrayList<JSONObject> jsonInfo;
	
	public Tagger(String musicFolder){
		try {
			//Path dir = FileSystems.getDefault().getPath(musicFolder);
			music = new ArrayList<File>();
			missingInfo = new ArrayList<File>();
			jsonInfo = new ArrayList<JSONObject>();
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
					if(isMissingTag(music.get(i), title, artist)){
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
	
	private boolean isMissingTag(File item, String title, String artist){
		if(title == "" || artist == ""){
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
		String API_KEY = "B8NV7C9CDT8EYNPOM";
		try {
			EchoNestAPI en = new EchoNestAPI(API_KEY);
			for(int i=0; i < missingInfo.size(); i++){
				String title, album, artist;
				JSONObject s = pg.fingerprint(missingInfo.get(i));
				//String[] values = JSONObject.getNames(s);				
				jsonInfo.add(s);
				System.out.println(s.toString());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		searchEcho();
	}
	
	public void searchEcho(){
		try {
			EchoNestAPI en = new EchoNestAPI("B8NV7C9CDT8EYNPOM");
			//String badRomanceCode = "eJxdVAmOXSEMuxIhZOE4EOD-Rxj7taNK1Yys2IRs5P3Wo7XWYwFmAhatQ-sSHkCF0AdAaQ1a5oCkNWltwiFcag8wGkEIvDu0A8YHBTACLrQRFzBpLcKmSyHROKT3Azo_mCaNQKtDM6VFHzOCUwtCIoBNauzINjUGtUPKePZgeYPlQotFupKyDjeCEzgcZzznhHwRNrVC-c5ZOZt2Nh1sOgQHwXihtDiw4MDCCUHArRYTrcYibLoUrUNgvGC8ZLwUaMn5ceAtjZYT-HjJ-SVDJeeXm1AEzi85v3wATrhN-QDiVFrsd3J-k_ObjDc5v8lYE_MTLW0rZisZr1XXMxRjLxT1DGLr-IszBDUmxLbOGIlinvRtEE9pjXXwShoQoYpB7GePVTbwNNtP3-hO_J6th89kW_p4Fogz15gQZbfus3OL8MhzaexKiFjEc1_HVvqxHVvzHtnm4Ff3cG0PPK_eKKwl9HjzoZDVjrwbWGJPiN2zXYhqawKzz318efZcyx-W4eCh1tyKPuxiOuMe9BHwF90U1_EctwvubVtald89O08h9tUlokpXrEw74HdeiLNW7oVFWR4Q8TmcNWe1FyIP-2epp0aQO0A3plH2Ld0d5K3Jhqj3fqt8Zr2NuTQND72Sr9o5E625PsrqCbHrasVv4a68qQquC_94W_C74Cjjkp9qZTxH_d99nNuRwuvjHPlDhiqi34KIl0R0PBaiVblqCaJB7PvBZ_GHZL0S7FWr2Tf8sVcIn7XqQEQjCrHhh6NekjcDCD6twg62-4aUKvLqPsiNj6Ug6vUhyAv-5oQuBX4dfNDpO0fVv-d__Hlu__zj4_S3jxv8sb0f9zmwveS2sP_ID-7zy8-P5L_87PNvfvSJfPLV3cErzm998tDcV98PwMpc3Q==";			
			String artist;
			for(int i=0; i<jsonInfo.size(); i++){
				JSONObject meta = new JSONObject(jsonInfo.get(i).get("metadata").toString());
				System.out.println(meta.toString());
				//IdentifySongParams p = new IdentifySongParams();
				Params p = new Params();
				//System.out.println(jsonInfo.get(i).getString("code"));
				//p.setCode(jsonInfo.get(i).getString("code"));
				String[] keys = jsonInfo.get(i).getNames(jsonInfo.get(i));
				for(int j=0; j<keys.length; j++){
					if(keys[j].equals("metadata")){
						String[] key2 = meta.getNames(meta);
						for(int k=0; k<key2.length; k++){
							if(!key2[k].equals("bitrate") && !key2[k].equals("samples_decoded") && !key2[k].equals("given_duration") && !key2[k].equals("sample_rate") && !key2[k].equals("start_offset") && !key2[k].equals("decode_time") && !key2[k].equals("codegen_time")){
								p.add(key2[k], meta.get(key2[k]).toString());
								System.out.println("Added Meta "+key2[k]);
							}
						}
					} else if(!keys[j].equals("code_count") && !keys[j].equals("tag") && !keys[j].equals("decode_time")){
						p.add(keys[j], jsonInfo.get(i).get(keys[j]).toString());
						System.out.println("Added outer "+keys[j]);
					}
				}
				//p.setCode(jsonInfo.get(i).getString("code"));
				//p.set("metadata", meta.toString());

				List<Song> songs = en.identifySongs(p);
				System.out.println(songs.get(0).toString());
				System.out.println(songs.size());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * This function fingerprints a list of files and returns an arraylist of the json info
	 * DO NOT GIVE THIS FUNCTION ANYTHING BUT MP3s!
	 * @param files is a list of MP3s. NOTE DO NOT PROVIDE OTHER FILES TO THIS FUNCTION
	 * @return null if no information can be found, else an arraylist of jsonobjects from echoprint
	 */
	public ArrayList<JSONObject> fingerPrintList(File[] files){
		PrinterGrabber pg = new PrinterGrabber();
		ArrayList<JSONObject> jsonInfo = new ArrayList<JSONObject>(files.length);
		for(int i=0; i < files.length; i++){
			JSONObject song = pg.fingerprint(files[i]);
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

	public void getBPM(File[] files){
		for(int i=0; i < files.length; i++){
			getBPM(files[i]);
		}
	}
	
	/**
	 * This function calculates the BPM/updates the BPM metatag
	 * @param file the MP3 to get the BPM for
	 * @return -1 if error, BPM if corect
	 */
	public int getBPM(File file){
		int bpm = BPM.getBPM(file.getAbsolutePath());
		try {
			AudioFile f = AudioFileIO.read(file);
			Tag tag = f.getTag();
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
	
	public void findSongInfo(JSONObject song){
		try {
			JSONObject meta = new JSONObject(song.get("metadata").toString());
			EchoNestAPI en = new EchoNestAPI("B8NV7C9CDT8EYNPOM");
			Params p = new Params();
			p.add("code", song.getString("code"));
			p.add("genre", meta.getString("genre"));
			p.add("duration", meta.getString("duration"));
			p.add("title", meta.getString("title"));
			p.add("filename", meta.getString("filename"));
			p.add("artist", meta.getString("artist"));
			p.add("release", meta.getString("release"));
			p.add("version", meta.getString("version"));
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
	
	private void updateTags(String artist, String title, String filename){
		try {
			AudioFile f = AudioFileIO.read(new File(filename));
			Tag tag = f.getTag();
			tag.setField(FieldKey.ARTIST, artist);
			tag.setField(FieldKey.TITLE, title);
			f.commit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
